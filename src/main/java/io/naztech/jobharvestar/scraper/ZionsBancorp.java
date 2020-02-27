package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
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
 * ZionsBankCorp Jobsite parser<br>
 * URL: https://careers.zionsbancorp.com/
 *
 * @author BM Al-Amin
 * @author fahim.reza
 * @since: 24.02.2019
 */
@Service
@Slf4j
public class ZionsBancorp extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ZIONS_BANCORP;
	private static String baseUrl;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private WebClient client;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		client = getFirefoxClient();
		HtmlPage page = client.getPage(siteMeta.getUrl());
		client.waitForBackgroundJavaScript(6 * TIME_10S);
		List<HtmlElement> categoryLinks = page.getByXPath("//div[@role='listitem']/a");
		for (HtmlElement el : categoryLinks) {
			String url=el.getAttribute("href");
			try {
				getSummaryPages(url, siteMeta);
			} catch (Exception e) {
				log.warn("failed to parse details of "+url,e);
			}
		}
	}

	private void getSummaryPages(String categoryUrl, SiteMetaData siteMeta) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(categoryUrl);
		client.waitForBackgroundJavaScript(TIME_10S * 2);
		List<HtmlElement> jobLinks = page.getByXPath("//li[@class='jobs-list-item']/div/a");
		expectedJobCount += jobLinks.size();
		for (HtmlElement el : jobLinks) {
			Job job = new Job(el.getAttribute("href"));
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("failed to parse details of "+job.getUrl(),e);
			}
		}
	}

	private Job getJobDetails(Job job) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(job.getUrl());
		client.waitForBackgroundJavaScript(TIME_10S * 2);
		HtmlElement el = page.getFirstByXPath("//div[@class='job-info au-target']/h1");
		job.setTitle(el.getTextContent().trim());
		job.setName(job.getTitle());
		el = page.getFirstByXPath("//div[@class='job-other-info']/span[3]");
		job.setReferenceId(el.asText().substring(8));
		el = page.getFirstByXPath("//div[@class='job-other-info']/span[1]");
		job.setLocation(el.getTextContent().trim());
		el = page.getFirstByXPath("//div[@class='job-other-info']/span[2]");
		job.setCategory(el.getTextContent().trim());
		el = page.getFirstByXPath("//div[@class='job-other-info']/span[4]");
		job.setPostedDate(parseDate(el.getTextContent().split(":")[1].trim(), DF));
		el = (HtmlElement) page.getElementById("requisitionDescriptionInterface.ID1628.row1");
		job.setSpec(el.getTextContent().trim());
		el = (HtmlElement) page.getElementById("requisitionDescriptionInterface.ID1641.row1");
		job.setPrerequisite(el.getTextContent().trim());
		el = page.getBody().getFirstByXPath("//div[@class='job-header-actions']/a");
		if (el != null) {
			String a = el.getAttribute("data-applyurl");
			job.setApplicationUrl(a);
		}
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
	}

	@Override
	protected int getExpectedJob() {
		return expectedJobCount;
	}

	@Override
	protected void destroy() {
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
