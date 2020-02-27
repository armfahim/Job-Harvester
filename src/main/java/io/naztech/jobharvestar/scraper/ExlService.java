package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;
/**
 * Exl Service job site parser. <br>
 * URL: https://outtowin.exlservice.com
 * 
 * @author tanmoy.tushar
 * @since 2019-10-24
 */
@Slf4j
@Service
public class ExlService extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.EXL;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd MMM yyyy");
	private static final String CONTAINER_FRAME = "icims_content_iframe";
	private static ChromeDriver driver;
	private static WebDriverWait wait;
	private static int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		Document doc = loadPage(site.getUrl());
		Elements contryList = doc.select("div[class=search-box]>div>a");
		for (Element el : contryList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = el.attr("href");
			try {
				if (url.contains("icims.com")) getUsCareers(url, site);
				else if (url.contains("uk-europe"))	getUkCareers(url, site);
				else if (url.contains("outtowinexlservice.alchemus.com")) getOthersCareers(url, site);
				else log.info("New country added to exl service careers list");
			} catch (Exception e) {
				log.warn("Failed to parse country career page of " + url, e);
			}
		}
	}
	
	/**
	 * This function is to parse us career job list.
	 * 
	 * @param url (us career url)
	 * @param site object of {@link SiteMetaData}
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void getUsCareers(String url, SiteMetaData site) throws IOException, InterruptedException {
		String baseUrl = url.split("icims.com")[0].trim() + "icims.com";
		Document doc = loadPage(baseUrl + "/jobs/search?pr=");
		String iFrameUrl = doc.getElementById(CONTAINER_FRAME).attr("src");
		doc = Jsoup.connect(iFrameUrl).get();
		int totalPage = getUsTotalPage(doc);
		log.info("Total expected Job Found in Us career is: " + (totalPage * 20));
		browseUsJobList(doc, site);
		for (int i = 1; i < totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String nextPageUrl = "";
			try {
				nextPageUrl = baseUrl + "/jobs/search?pr=" + i;
				doc = loadPage(nextPageUrl);
				iFrameUrl = doc.getElementById(CONTAINER_FRAME).attr("src");
				doc = Jsoup.connect(iFrameUrl).get();
				browseUsJobList(doc, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list of " + nextPageUrl, e);
			}
		}
	}
	
	/**
	 * This function is to parse uk/europe career job list.
	 * 
	 * @param url (uk career url)
	 * @param site object of {@link SiteMetaData}
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void getUkCareers(String url, SiteMetaData site) throws IOException, InterruptedException {
		Document doc = loadPage(url.replace("europe", "careers"));
		Elements urlList = doc.select("div[class=box]");
		log.info("Total Job Found in Uk career is: " + urlList.size());
		expectedJobCount += urlList.size();
		for (Element element : urlList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String specId = element.getElementsByTag("a").get(0).attr("href");
			Element content = doc.getElementById(specId.replace("#", "").trim());
			try {
				saveJob(getUkJobDetail(content, url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	/**
	 * This function is to parse other countries career job list.
	 * 
	 * @param url (other countries career url)
	 * @param site object of {@link SiteMetaData}
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void getOthersCareers(String url, SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 30);
		driver.get(url.substring(0, 39) + loadPage(url).selectFirst("frame[name=RightExManager]").attr("src").substring(5));
		WebElement jobLoadBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("JobListView_btnLoadMore")));
		jobLoadBtn.click();
		Thread.sleep(RandomUtils.nextInt(TIME_4S, TIME_5S));
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='info-box']")));
		log.info("Total Job Found in other country career is: " + jobList.size());
		expectedJobCount += jobList.size();
		for (WebElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getOthersJobDetail(el), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + driver.getCurrentUrl(), e);
			}
		}
		driver.quit();
	}
	
	/**
	 * This function is a part of {@link #getUsCareers(String, SiteMetaData) getUsCareers} to browseJobList page
	 * 
	 * @param doc {@link Document} instance which is loaded in {@link #getUsCareers(String, SiteMetaData) getUsCareers}
	 * @param site object of {@link pass SiteMetaData}
	 * @throws PageScrapingInterruptedException
	 * @throws IOException
	 */
	private void browseUsJobList(Document doc, SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		Elements rowList = doc.select("div[class=col-xs-12 title]>a");
		expectedJobCount += rowList.size();
		for (Element row : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = "";
			try {
				jobUrl = row.attr("href");
				saveJob(getUsJobDetail(jobUrl), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + jobUrl, e);
			}
		}
	}
	
	/**
	 * This function is a part of {@link #getUsCareers(String, SiteMetaData) getUsCareers} to find total pages
	 * 
	 * @param doc {@link Document} instance which is loaded in {@link #getUsCareers(String, SiteMetaData) getUsCareers}
	 */
	private int getUsTotalPage(Document doc) {
		Elements el = doc.select("h2[class=iCIMS_SubHeader iCIMS_SubHeader_Jobs]");
		String totalPage;
		if (el.size() == 1) totalPage = el.get(0).text();
		else totalPage = el.get(1).text();
		return Integer.parseInt(totalPage.split("of")[1].trim());
	}
	
	/**
	 * This function is a part of {@link #browseUsJobList(Document, SiteMetaData) browseUsJobList} to parse each Job Detail
	 * 
	 * @param url (job detail page url)
	 * @return
	 * @throws IOException
	 */
	private Job getUsJobDetail(String url) throws IOException {
		String extrUrlLink = "?hub=7&mobile=false&width=760&height=500&bga=true&needsRedirect=false&jan1offset=360&jun1offset=360";
		Job job = new Job(url);
		Document doc = loadPage(url + extrUrlLink);
		String iFrameUrl = doc.getElementById(CONTAINER_FRAME).attr("src");
		doc = loadPage(iFrameUrl);		
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(jobE.text());		
		Elements jobSpec = doc.select("div[class=iCIMS_Expandable_Container]");
		job.setSpec(jobSpec.get(0).text().trim());
		if(jobSpec.size() > 1) job.setPrerequisite(jobSpec.get(1).text().trim());
		job = getUsJobInfo(job, doc);
		return job;
	}
	
	/**
	 * This function is a part of {@link #getUsJobDetail(String) getUsJobDetail} to pass Job object.
	 * Object may contains Location, ReferenceId, Job Type & Category
	 * 
	 * @param job object of {@link Job}
	 * @param doc {@link Document} instance which is loaded in {@link #getUsJobDetail(String) getUsJobDetail}
	 * @return
	 */
	private Job getUsJobInfo(Job job, Document doc) {
		Element jobE = doc.selectFirst("a[class=iCIMS_Anchor iCIMS_Action_Button iCIMS_ApplyOnlineButton iCIMS_PrimaryButton]");
		if (jobE != null) job.setApplicationUrl(jobE.attr("href"));
		jobE = doc.selectFirst("div[class=col-xs-6 header left]>span");
		if (jobE != null) {
			job.setLocation(jobE.nextElementSibling().text().trim());
		} else {
			jobE = doc.selectFirst("div[class=col-xs-6 header right]>span");
			if (jobE != null) job.setLocation(jobE.nextElementSibling().text().trim());
		}
		Elements jobInfo = doc.select("div[class=iCIMS_JobHeaderGroup]>dl");
		for (Element element : jobInfo) {
			if (element.text().contains("Job ID"))
				job.setReferenceId(element.text().split("ID")[1].trim());
			if (job.getReferenceId() == null && element.text().contains("ID"))
				job.setReferenceId(element.text().split("ID")[1].trim());
			if (element.text().contains("Schedule Type") || element.text().contains("Position Type") || element.text().contains("Job Type"))
				job.setType(element.text().split("Type")[1].trim());
			if (element.text().contains("Type") && element.text().contains("Time"))
				job.setType(element.text().split("ype")[1].trim());
			if (element.text().contains("Business"))
				job.setCategory(element.text().split("ness")[1].trim());
			if (job.getCategory() == null && element.text().contains("Category"))
				job.setCategory(element.text().split("gory")[1].trim());
			if (job.getCategory() == null && element.text().contains("Department"))
				job.setCategory(element.text().split("ment")[1].trim());
			if (job.getLocation() == null && element.text().contains("Locations"))
				job.setLocation(element.text().split("Locations")[1].trim());
			if (job.getLocation() == null && element.text().contains("Location"))
				job.setLocation(element.text().split("cation")[1].trim());
		}
		return job;
	}
	
	/**
	 * This function is a part of {@link #getUkCareers(String, SiteMetaData) getUkCareers} to parse each Job Detail
	 * 
	 * @param content {@link Element} object of each Job 
	 * @param url (job detail page url)
	 * @return
	 */
	private Job getUkJobDetail(Element content, String url) {
		Job job = new Job();
		job.setTitle(content.getElementsByTag("h3").text().split(":")[1].trim());
		job.setName(job.getTitle());
		job.setSpec(content.text().substring(3).trim());
		Elements infoList = content.select("div[class=content]>p");
		for (Element el : infoList) {
			if (el.getElementsByTag("strong").size() == 0) continue;
			if (el.getElementsByTag("strong").text().contains("Location:"))
				job.setLocation(el.text().split(" - ")[1].trim());
			if (el.getElementsByTag("strong").text().contains("type:"))
				job.setType(el.text().split(":")[1].trim());
			if (el.getElementsByTag("strong").text().contains("function:"))
				job.setCategory(el.text().split(":")[1].trim());
		}
		job.setUrl(url + getJobHash(job));
		return job;
	}
	
	/**
	 * This function is a part of {@link #getOthersCareers(String, SiteMetaData) getOthersCareers} to parse each Job Detail
	 * 
	 * @param el {@link WebElement} object of each Job
	 * @return
	 */
	private Job getOthersJobDetail(WebElement el) {
		Job job = new Job();
		job.setTitle(el.findElement(By.tagName("h6")).getText().trim().split("   ")[0].trim());
		job.setName(job.getTitle());
		job.setLocation(el.findElement(By.tagName("p")).findElements(By.tagName("span")).get(0).getText().trim());
		job.setPostedDate(parseDate(el.findElement(By.tagName("p")).findElements(By.tagName("span")).get(1).getText().trim(), DF));
		job.setSpec(el.getText().trim());
		job.setUrl(driver.getCurrentUrl() + getJobHash(job));
		return job;
	}
	
	/**
	 * This function is to remove Jsoup.connect() to load {@link Document} several times.
	 * Just create an object of {@link Document} and called this function.
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	private Document loadPage(String url) throws IOException {
		return Jsoup.connect(url).validateTLSCertificates(false).timeout(TIME_1M).get();
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}

	@Override
	protected int getExpectedJob() {
		return expectedJobCount;
	}

	@Override
	protected void destroy() {

	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}

}
