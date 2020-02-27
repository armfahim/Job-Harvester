package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * PowerCorporationCanada job site parsing class. <br>
 * URL: https://www.powercorporation.com/en/other/careers/
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-13
 */
@Slf4j
@Service
public class PowerCorpCanada extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.POWER_CORPORATION_OF_CANADA;
	private static final String JOB_LIST_EL_PATH = "//div[@class = 'wrapper_job_posting']/div";
	private static WebClient webClient = null;
	private String baseUrl;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.POWER_CORPORATION_OF_CANADA));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		webClient = getChromeClient();
		this.baseUrl = siteMeta.getUrl().substring(0, 32);
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		getSummaryPage(page, siteMeta);
	
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) {
		try {
			Job job = new Job();
			List<HtmlElement> list = page.getBody().getByXPath(JOB_LIST_EL_PATH);
			expectedJobCount = list.size();
			for (int i = 0; i < list.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				String Nam = list.get(i).getElementsByTagName("div").get(0).getElementsByTagName("a").get(0).getTextContent().trim();
				String[] part = Nam.split("-");
				job.setName(part[0]);
				job.setTitle(part[0]);
				job.setUrl(baseUrl + list.get(i).getElementsByTagName("div").get(0).getElementsByTagName("a").get(0).getAttribute("href"));
				String JobDate = part[1].trim() + "/" + part[2].trim() + "/" + part[3].trim();
				job.setPostedDate(parseDate(JobDate, DF));
				if (job.getPostedDate() == null) log.info(" failed to parse date value " + JobDate + " for job " + job.getUrl());
				job.setApplicationUrl(job.getUrl());
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException | InterruptedException e) {
			log.warn("Failed parse job details " + e);
		}
	}

	private Job getJobDetail(Job job) throws InterruptedException {
		try {
			HtmlPage JobDetailPage = getFirefoxClient().getPage(job.getUrl());
			webClient.waitForBackgroundJavaScript(TIME_5S);
			Thread.sleep(RandomUtils.nextInt(2500, 5000));
			String JobDescription = JobDetailPage.getBody().getOneHtmlElementByAttribute("div", "class", "content_box blue_border_top").getTextContent().trim();
			job.setSpec(JobDescription);
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed parse job details " + job.getUrl()+e);
		}
		return null;
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
		webClient.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
