package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Wefox job site scraper. <br>
 * URL: https://www.wefox.de/en/careers/#vacancies
 * 
 * @author Asadullah Galib
 * @author tanmoy.tushar
 * @author fahim.reza
 * @since 2019-04-01
 */
@Slf4j
@Service
public class Wefox extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.WEFOX;
	private static WebClient CLIENT = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		CLIENT = getFirefoxClient();
		HtmlPage page = CLIENT.getPage(site.getUrl());
		String iFrameUrl = page.getElementById("personio-iframe").getAttribute("src");
		page = CLIENT.getPage(iFrameUrl);
		List<HtmlElement> jobList = page.getBody().getByXPath("//div[@id='all']/div");
		expectedJobCount = jobList.size();
		getSummaryPage(jobList, site);
	}

	private void getSummaryPage(List<HtmlElement> jobList, SiteMetaData site) {
		for (HtmlElement el : jobList) {
			Job job = new Job(el.getElementsByTagName("h6").get(0).getElementsByTagName("a").get(0).getAttribute("href"));
			job.setTitle(el.getElementsByTagName("h6").get(0).asText());
			job.setName(job.getTitle());
			job.setCategory(el.getElementsByTagName("p").get(0).asText().split(",")[0].trim());
			job.setType(el.getElementsByTagName("p").get(0).asText().split(",")[1].trim().split("·")[0]);
			job.setLocation(el.getElementsByTagName("p").get(0).asText().split(",")[1].trim().split("·")[1].trim());
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				exception = e;
				log.error("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) {
		try {
			HtmlPage page = CLIENT.getPage(job.getUrl());
			List<HtmlElement> elSpec = page.getByXPath("//div[@class='col-md-6 pull-right']");
			List<HtmlElement> elSpec2 = page.getByXPath("//div[@class='col-md-6 pull-left']");
			String spec = elSpec.get(0).asText() + elSpec.get(1).asText() + elSpec2.get(0).asText();
			job.setSpec(spec);
			job.setPrerequisite(elSpec2.get(1).asText());
			job.setApplicationUrl(job.getUrl() + "#apply");
			return job;
		} catch (IndexOutOfBoundsException e) {
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed to load detail page " + job.getUrl());
			return null;
		}
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