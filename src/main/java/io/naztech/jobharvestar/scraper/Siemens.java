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
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * SEIMENS job site parser. <br>
 * URL: https://jobs.siemens-info.com/jobs?page=1
 * 
 * @author tohedul.islum
 * @since 2019-03-07
 * 
 * @author tanmoy.tushar
 * @since 2019-04-24
 */
@Slf4j
@Service
public class Siemens extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SIEMENS;
	private String baseUrl;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private static final String JOB_LINK_ID = "/jobs?page=";
	private static DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	private static DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.SIEMENS));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 29);
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(180, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 15);
		int i = 1;
		while (true) {
			try {
				getSummaryPage(getBaseUrl() + JOB_LINK_ID + i, site);
				int totalPage = getTotalPage();
				while (true) {
					try {
						JavascriptExecutor js = driver;
						js.executeScript("arguments[0].click();", getNextBtn());
						Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_5S));
						List<WebElement> rowList = getRowList();
						try {
							getSummaryPageDetails(rowList, site);
						} catch (StaleElementReferenceException e) {
							log.warn("Failed to parse job list of " + driver.getCurrentUrl(), e);
						}
						if (i == totalPage)	break;
						i++;
					} catch (NoSuchElementException e) {
						break;
					}
				}
				if (i == totalPage) break;
				i++;
			} catch (StaleElementReferenceException e) {
				getSummaryPage(getBaseUrl() + JOB_LINK_ID + i, site);
				i++;
			}
		}
		driver.quit();
	}

	private void getSummaryPage(String url, SiteMetaData site) {
		driver.get(url);
		List<WebElement> rowList = getRowList();
		getSummaryPageDetails(rowList, site);
	}

	private void getSummaryPageDetails(List<WebElement> rowList, SiteMetaData site) {
		List<WebElement> dateL = getDateList();
		for (int i = 0; i < rowList.size(); i++) {
			Job job = new Job(rowList.get(i).getAttribute("href"));
			try {
				job.setPostedDate(parseDate(dateL.get(i).getText().trim(), DF1, DF2));
			} catch (IndexOutOfBoundsException e) {
				job.setPostedDate(null);
			}
			try {
				saveJob(getJobDetail(job), site);				
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("section[class=main-description-section]");
		job.setSpec(jobE.text().trim());
		Elements info = jobE.select("p>b");
		for (Element el : info) {
			if (el.parent().text().contains("Job Type:")) job.setType(el.parent().text().replace("Job Type:", "").trim());
		}
		jobE = doc.selectFirst("li[itemprop=jobLocation]");
		job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("li[itemprop=occupationalCategory]");
		job.setCategory(jobE.text().trim());
		jobE = doc.selectFirst("li[itemprop=reqId]");
		job.setReferenceId(jobE.text().trim());
		jobE = doc.getElementById("link-apply");
		job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		return job;
	}

	private List<WebElement> getRowList() {
		return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("job-title-link")));
	}

	private List<WebElement> getDateList() {
		return driver.findElementsByClassName("posted-date label-value");
	}
	
	private WebElement getNextBtn() {
		return driver.findElement(By.xpath("//a[@class='page-link next']"));
	}

	private int getTotalPage() {
		WebElement el = driver.findElementById("search-results-indicator");
		String totalJob = el.getText().trim().split(" ")[2].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 10);
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}
	
	@Override
	protected int getExpectedJob() {
		return expectedJobCount;
	}
	
	@Override
	protected void destroy() {
		driver.quit();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
