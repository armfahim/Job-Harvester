package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
 * Raymon James Group Job Site Parser<br>
 * URL: https://jobs.raymondjames.com/search-jobs
 * 
 * @author Mahmud Rana
 * @author tanmoy.tushar
 * @since 2019-02-12
 */
@Service
@Slf4j
public class RaymondJames extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.RAYMOND_JAMES_FINANCIAL;
	private static final String ROW_ANCHOR_PATH = "//section[@id='search-results-list']/ul/li/a";
	private static final String PAGING_EL_PATH = "//h1[@role='status']";
	private static final String NEXT_EL_PATH = "//a[@class='next']";
	private static final String TITLE_EL_PATH = ROW_ANCHOR_PATH + "/h2";
	private static final String LOC_EL_PATH = ROW_ANCHOR_PATH + "/span[@class='job-location']";
	private static final String DAT_EL_PATH = ROW_ANCHOR_PATH + "/span[@class='job-date-posted']";
	private static final String APPLY_EL_PATH = "a[class=button job-apply top]";
	private static final String SPEC_EL_PATH = "div[class=ats-description]";
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static WebClient client = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 29);
		try {
			HtmlPage page = client.getPage(site.getUrl());
			int pageCounter = 1;
			client.waitForBackgroundJavaScript(TIME_10S * 2);			
			int totalPage = getTotalPages(page);
			List<HtmlElement> rowList = page.getBody().getByXPath(ROW_ANCHOR_PATH);
			HtmlElement el;
			do {
				if (isStopped()) throw new PageScrapingInterruptedException();
				int i = 0;
				for (HtmlElement row : rowList) {
					if (isStopped()) throw new PageScrapingInterruptedException();
					Job job = new Job(getBaseUrl() + row.getAttribute("href"));
					el = (HtmlElement) page.getBody().getByXPath(TITLE_EL_PATH).get(i);
					job.setTitle(el.getTextContent().split("Req.")[0]);
					job.setName(job.getTitle());
					job.setReferenceId(el.getTextContent().split("Req.")[1]);
					el = (HtmlElement) page.getBody().getByXPath(LOC_EL_PATH).get(i);
					job.setLocation(el.getTextContent());
					el = (HtmlElement) page.getBody().getByXPath(DAT_EL_PATH).get(i);
					job.setPostedDate(parseDate(el.getTextContent().trim(), DF));
					if (job.getPostedDate() == null)
						log.info(" failed to parse date value " + el.getTextContent().trim() + " for job " + job.getUrl());
					try {
						saveJob(getJobDetail(job), site);						
					} catch (Exception e) {
						exception = e;
						log.warn("Failed to parse job details of " + job.getUrl(), e);
					}
					i++;
				}
				Thread.sleep(RandomUtils.nextInt(TIME_1S * 3, TIME_5S));
				el = page.getBody().getFirstByXPath(NEXT_EL_PATH);
				if (el != null) { page = el.click(); }
				client.waitForBackgroundJavaScriptStartingBefore(TIME_5S);
				client.waitForBackgroundJavaScript(TIME_10S);
				rowList = page.getBody().getByXPath(ROW_ANCHOR_PATH);
				pageCounter++;
			} while (pageCounter <= totalPage);
		} catch (FailingHttpStatusCodeException e) {
			log.warn(getSiteName() + "--> Fails to load Page. Quiting...");
			throw e;
		} finally {
			client.close();
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setSpec(doc.selectFirst(SPEC_EL_PATH).text().trim());
		job.setApplicationUrl(doc.selectFirst(APPLY_EL_PATH).attr("href"));
		return job;
	}
	
	private int getTotalPages(HtmlPage page) {
		HtmlElement el = page.getBody().getFirstByXPath(PAGING_EL_PATH);
		String totalJob = el.asText().trim().split(" ")[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 15);
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
