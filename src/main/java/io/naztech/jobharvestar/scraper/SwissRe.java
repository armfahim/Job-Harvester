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
 * SwissRe job site scraper. <br>
 * URL: https://careers.swissre.com/search
 * 
 * @author muhammad.tarek
 * @since 2019-04-03
 */
@Slf4j
@Service
public class SwissRe extends AbstractScraper implements Scrapper {
	private int expectedJobCount;
	private Exception exception;
	private static final String SITE = ShortName.SWISS_RE;
	private String baseUrl;
	private static WebClient client = null;
	private static final String TAILURL = "/search?q=&sortColumn=referencedate&sortDirection=desc&startrow=";
	private static final int JOBPERPAGE = 25;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("d-MMM-yyyy");

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 20);
		this.baseUrl = site.getUrl().substring(0, 27);
		int totalPage = getTotalPages(site.getUrl());
		for (int i = 1; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			getSummaryPages(getBaseUrl() + TAILURL + (i * 25 - 25), site);
		}
	}

	private int getTotalPages(String url)
			throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(url);
		List<HtmlElement> elTotalPage = page.getByXPath("//span[@class='paginationLabel']/b");
		expectedJobCount = Integer.parseInt(elTotalPage.get(1).asText());
		return getPageCount(elTotalPage.get(1).asText(), JOBPERPAGE);
	}

	private void getSummaryPages(String url, SiteMetaData site) throws InterruptedException {
		try {
			HtmlPage page = client.getPage(url);
			List<HtmlElement> el = page.getByXPath("//tr[@class='data-row clickable']");
			for (HtmlElement tr : el) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				HtmlElement title = tr.getElementsByTagName("a").get(0);
				Job job = new Job(getBaseUrl() + title.getAttribute("href"));
				job.setTitle(title.asText());
				job.setName(title.asText());
				HtmlElement location = tr.getFirstByXPath("//span[@class='jobLocation']");
				job.setLocation(location.asText());
				List<HtmlElement> date = tr.getByXPath("//span[@class='jobDate']");
				job.setPostedDate(parseDate(date.get(2).asText(), DF));
				try {
					saveJob(getJobDetail(job), site);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn(SITE + " Failed to parse Summary page of " + url, e);
		}
	}

	private Job getJobDetail(Job job) {

		try {
			HtmlPage page = client.getPage(job.getUrl());
			HtmlElement spec = page.getBody().getOneHtmlElementByAttribute("div", "class", "row");
			job.setSpec(spec.asText().trim());
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn(getSiteName() + "Failed to parse job details", e);
		}
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
