package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Abstract for Avature sites AT&T:
 * https://attglobal.avature.net/careers/SearchJobs
 * 
 * @author tohedul.islum
 * @since 2019-03-06
 */
public abstract class AbstractAvature extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private WebClient webClient = null;
	private static final int JOBS_PER_PAGE = 20;
	private static final String TAILURL = "/careers/SearchJobs/?jobOffset=";
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("d-MMM-yyyy");
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		webClient = getFirefoxClient();
		SiteMetaData site = getSiteMetaData(getSiteName());
		setBaseUrl(site);
		startSiteScrapping(site);

	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		int totalPage = getTotalPages(page);
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			getsummaryPages(getBaseUrl() + TAILURL + (i * JOBS_PER_PAGE - JOBS_PER_PAGE), siteMeta);
		}
	}

	private void getsummaryPages(String url, SiteMetaData siteMeta) {
		try {
			HtmlPage page = webClient.getPage(url);
			List<HtmlElement> jobList = page.getByXPath("//main[@id='mainContent']/ul/li");
			expectedJobCount += jobList.size();
			for (int i = 0; i < jobList.size(); i++) {
				HtmlElement link = jobList.get(i).getElementsByTagName("h3").get(0).getElementsByTagName("a").get(0);
				Job job = new Job(link.getAttribute("href"));
				job.setTitle(link.asText());
				job.setName(link.asText());
				HtmlElement appUrl = jobList.get(i).getElementsByAttribute("div", "class", "listButtonBar").get(0)
						.getElementsByTagName("a").get(0);
				job.setApplicationUrl(appUrl.getAttribute("href"));
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch(Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn(getSiteName() + "Exception in getSummaryPage: " + e);
		}

	}

	private Job getJobDetail(Job job) {
		try {
			HtmlPage page = webClient.getPage(job.getUrl());
			List<HtmlElement> detail = page.getByXPath("//div[@class='fieldSetValue']");
			job.setLocation(detail.get(0).asText());
			job.setReferenceId(detail.get(1).asText());
			job.setPostedDate(parseDate(detail.get(2).asText(), DF));
			List<HtmlElement> spec = page.getByXPath("//div[@class='jobDetailDescription']");
			job.setSpec(spec.get(0).asText());
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn(getSiteName() + "Exception in getJobDetail: " + e);
		}
		return job;
	}

	private int getTotalPages(HtmlPage page) {
		webClient.waitForBackgroundJavaScript(5000);
		List<HtmlElement> el = page.getByXPath("//span[@class='jobPaginationLegend']");
		return getPageCount((el.get(0).asText().split("of")[1]).split("results")[0].trim(), JOBS_PER_PAGE);
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

	protected abstract void setBaseUrl(SiteMetaData site);

}
