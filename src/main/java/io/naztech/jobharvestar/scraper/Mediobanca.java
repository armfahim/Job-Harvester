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

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;
/**
 * Mediobanca.<br>
 * URL: https://www.mediobanca.com/en/work-with-us/open-positions.html
 * 
 * @author naym.hossain
 * @since 2019-02-03
 */
@Slf4j
@Service
public class Mediobanca extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MEDIOBANCA;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("d/M/yyyy");
	private String baseUrl;
	private WebClient webClient;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		webClient = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = "https://mediobanca.mua.hrdepartment.com";
		HtmlPage pageI = webClient.getPage(site.getUrl());
		HtmlPage page = (HtmlPage) pageI.getFrames().get(0).getEnclosedPage();
		webClient.waitForBackgroundJavaScript(50000);
		getSummaryPages(page, site);
		webClient.close();
	}

	private void getSummaryPages(HtmlPage page, SiteMetaData site)
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		List<HtmlElement> rows = page.getByXPath("//table[@id = 'jobSearchResultsGrid_table']/tbody/tr");
		expectedJobCount = rows.size();
		for (HtmlElement row : rows) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = row.getElementsByTagName("td").get(0).getElementsByTagName("a").get(0).getAttribute("href");
			Job job = new Job();
			job.setUrl(getBaseUrl() + jobUrl);
			job.setTitle(row.getElementsByTagName("td").get(0).getTextContent().trim());
			job.setName(row.getElementsByTagName("td").get(0).getTextContent().trim());
			job.setLocation(row.getElementsByTagName("td").get(1).getTextContent());
			job.setPostedDate(parseDate(row.getElementsByTagName("td").get(2).getTextContent().trim(), DF));
			if (job.getPostedDate() == null) log.info(" failed to parse date value " + row.getElementsByTagName("td").get(2).getTextContent().trim() + " for job " + job.getUrl());
			job.setCategory(row.getElementsByTagName("td").get(3).getTextContent());
			job.setType(row.getElementsByTagName("td").get(4).getTextContent());
			Thread.sleep(5000);
			try {
			saveJob(getJobDetail(job), site);
			}catch(Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = webClient.getPage(job.getUrl());
		List<HtmlElement> jobInfo = page.getByXPath("//fieldset[@class = 'form']/div");
		job.setSpec(jobInfo.get(5).getTextContent().trim());
		if (!jobInfo.get(6).getTextContent().isEmpty()) job.setPrerequisite(jobInfo.get(6).getTextContent().trim());
		return job;
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
