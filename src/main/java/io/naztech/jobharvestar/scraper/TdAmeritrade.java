package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
 * TD AMERITRADE HOLDING CO job site parsing class. <br>
 * URL: https://careers.bloomberg.com/job/search?
 * 
 * @author assaduzzaman.sohan
 * @since 2019-02-11
 * 
 * @author tanmoy.tushar
 * @since 2019-04-18
 */
@Slf4j
@Service
public class TdAmeritrade extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TD_AMERITRADE_HOLDING_CO;
	private static WebClient webClient = null;
	private static final int JOBS_PER_PAGE = 20;
	private String baseUrl;
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		webClient = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(ShortName.TD_AMERITRADE_HOLDING_CO));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 29);
		HtmlPage page;
		page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(15 * 1000);
		int totalJobs = getTotalJob(page);
		expectedJobCount = totalJobs;
		int totalPages = getTotalPage(totalJobs);

		for (int i = 1; i <= totalPages - 1; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			HtmlElement el = page.getBody().getOneHtmlElementByAttribute("button", "class",
					"btn btn-primary smallcaps load-more-button js-load-more-jobs");
			page = el.click();
			webClient.waitForBackgroundJavaScript(TIME_5S);
			Thread.sleep(RandomUtils.nextInt(2500, 5000));
		}
		getSummaryPage(page, siteMeta);
		webClient.close();
	}

	private int getTotalJob(HtmlPage page) {
		HtmlElement el = page.getBody().getOneHtmlElementByAttribute("h6", "class", "panel-label title");
		return Integer.parseInt(el.getTextContent().trim().substring(9, 12));
	}

	private int getTotalPage(int totalJobs) {
		int totalPages = totalJobs / JOBS_PER_PAGE;
		if (totalPages * JOBS_PER_PAGE < totalJobs)
			totalPages++;
		return totalPages;
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) {
		try {
			List<HtmlElement> list = page.getBody().getByXPath("//div[@class = 'job-results-name']/a");
			for (int i = 0; i < list.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(getBaseUrl() + list.get(i).getAttribute("href"));
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException | InterruptedException e) {
			log.warn("Failed to parse job list", e);
		}
	}

	private Job getJobDetail(Job job) throws InterruptedException {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element jobE = doc.selectFirst("h2");
			job.setTitle(jobE.text().trim());
			job.setName(job.getTitle());
			jobE = doc.selectFirst("h3");
			if (jobE != null)
				job.setLocation(jobE.text().trim());
			jobE = doc.selectFirst("div[class=col-xs-12 job-name-title]>p");
			if (jobE != null) {
				String[] parts = jobE.text().split("-");
				job.setPostedDate(parseDate(parts[0].split("ted")[1].trim(), DF1, DF2));
				job.setReferenceId(parts[1].split("No.")[1].trim());
			}
			jobE = doc.selectFirst("div[class=row description-sm]");
			job.setSpec(jobE.text().trim());
			return job;
		} catch (IOException | NullPointerException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
			return job;
		}
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
