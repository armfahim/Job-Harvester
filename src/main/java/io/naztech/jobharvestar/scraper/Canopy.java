package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Canopy job site scraper. <br>
 * URL:https://www.canopygrowth.com/careers/job-openings/
 * 
 * @author Asadullah Galib
 * @author bm.alamin
 * @since 2019-03-24
 */
@Slf4j
@Service
public class Canopy extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CANOPY;
	private String baseUrl = null;
	private WebClient client = null;
	private static Document document;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
	private int expectedJobCount = 0;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";
	
	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		
		baseUrl = siteMeta.getUrl().substring(0, 30);
		HtmlPage page = client.getPage("https://canr56.dayforcehcm.com/CandidatePortal/en-US/cgc");
		HtmlAnchor nextBtn = page.getBody().getFirstByXPath("//nav[@class = 'pagination']/ul/li/a[@aria-label = 'Next Page']");
		
		do {
			if (isStopped()) throw new PageScrapingInterruptedException();
			getSummaryPages(page, siteMeta);
			page = nextBtn.click();
			nextBtn = page.getBody().getFirstByXPath("//nav[@class = 'pagination']/ul/li/a[@aria-label = 'Next Page']");
		}while(nextBtn != null);
	}

	private void getSummaryPages(HtmlPage page, SiteMetaData siteMeta) {
		try {
			List<HtmlElement> joblinks = page.getByXPath("//div[@class='posting-title']/a");
			expectedJobCount += joblinks.size();
			for (HtmlElement link : joblinks) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(getBaseUrl() + link.getAttribute("href"));
				try {
				saveJob(getJobDetails(job), siteMeta);
				}catch(Exception e) {
					log.warn("Failed to parse job details of " + job.getUrl(), e);
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException | PageScrapingInterruptedException e) {
			log.warn("Failed to parse Site: " + getSiteName(), e);
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		document = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setUrl(job.getUrl());
		Elements spec = document.select("div.job-posting-section");
		job.setSpec(spec.text());
		Elements title = document.select("div > h1");
		job.setTitle(title.text());
		job.setName(title.text());
		Elements location = document.select("span.job-location");
		job.setLocation(location.text());
		String refId = document.select("span.job-req-number").text().split("#")[1].trim();
		job.setReferenceId(refId);
		String postdate = document.select("div.job-date-posted").text().trim();
		job.setPostedDate(parseDate(postdate, DF));
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
		client.close();
	}
	
	@Override
	protected Exception getFailedException() {
		return exception;
	}
}