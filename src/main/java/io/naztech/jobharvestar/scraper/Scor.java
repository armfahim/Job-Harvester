package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * SCOR<br>
 *  URL: "https://careers.scor.com/search/?q="
 * 
 * @author tohedul.islum
 * @since 2019-02-10
 */
@Service
@Slf4j
public class Scor extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SCOR;
	private String baseUrl;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private static final String TAILURL = "/search/?q=&sortColumn=referencedate&sortDirection=desc&startrow=";
	private WebClient client = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.SCOR));

	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 24);
		int totalJob = getTotalJobs(siteMeta.getUrl());
		expectedJobCount = totalJob;
		for (int i = 0; i < totalJob; i += 20) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			getSummaryPages(getBaseUrl() + TAILURL + i, siteMeta);
		}

	}

	private int getTotalJobs(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = getWebClient().getPage(url);
		List<HtmlElement> pageNo = page.getByXPath("//span[@class='paginationLabel']/b");
		return Integer.parseInt(pageNo.get(1).asText());
	}

	private void getSummaryPages(String url, SiteMetaData siteMeta) throws InterruptedException {
		try {
			HtmlPage page1 = client.getPage(url);
			List<HtmlElement> list = page1.getByXPath("//div[@class='searchResultsShell']/table/tbody/tr");
			for (HtmlElement row : list) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				HtmlElement link = row.getElementsByTagName("td").get(0).getElementsByTagName("a").get(0);
				Job job = new Job(getBaseUrl() + link.getAttribute("href"));
				job.setTitle(link.asText());
				job.setName(job.getTitle());
				job.setLocation(row.getElementsByTagName("td").get(1).asText());
				job.setType(row.getElementsByTagName("td").get(2).asText());
				job.setPostedDate(parseDate(row.getElementsByTagName("td").get(3).asText().trim(), DF));
				if (job.getPostedDate() == null) log.info(" failed to parse date value " + row.getElementsByTagName("td").get(3).asText().trim() + " for job " + job.getUrl());
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException | FailingHttpStatusCodeException e) {
			log.warn(SITE + " failed to connect site", e);
		}
	}

	private Job getJobDetail(Job job) {
		try {
			HtmlPage page = getWebClient().getPage(job.getUrl());
			List<HtmlElement> list = page.getByXPath("//div[@class='job']");
			job.setCategory(list.get(0).getElementsByTagName("p").get(4).asText());
			List<HtmlElement> specs = page.getByXPath("//div[@class='job']/span/ul");
			if (specs.size() > 0) {
				job.setSpec(specs.get(0).asText());
			}
			List<HtmlElement> appUrl = page.getByXPath("//div[@class='applylink pull-right']/a");
			job.setApplicationUrl(getBaseUrl() + appUrl.get(0).getAttribute("href"));
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn(SITE + " failed to parse job", e);
		}
		return job;
	}

	private WebClient getWebClient() {
		if (client == null) {
			client = new WebClient(BrowserVersion.CHROME);
			WebClientOptions opts = client.getOptions();
			opts.setJavaScriptEnabled(false);
			opts.setCssEnabled(false);
			opts.setDoNotTrackEnabled(true);
			opts.setTimeout(50000);
		}
		return client;
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
