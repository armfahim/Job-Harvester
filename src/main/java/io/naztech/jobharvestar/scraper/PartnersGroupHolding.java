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
 * Partners Group Holding<br>
 * URL: https://jobs.partnersgroup.com/search
 *
 * @author tohedul.islum
 * @since 2019-02-12
 */
@Service
@Slf4j
public class PartnersGroupHolding extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.PARTNERS_GROUP_HOLDING;
	private String baseUrl;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("d-MMM-yyyy");
	private static final String TAILURL = "/search?q=&sortColumn=referencedate&sortDirection=desc&startrow=";
	private WebClient client = null;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(ShortName.PARTNERS_GROUP_HOLDING));

	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 30);
		int totalJob = getTotalJobs(siteMeta.getUrl());
		expectedJobCount = totalJob;
		for (int i = 0; i < totalJob; i += 25) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			getSummaryPages(getBaseUrl() + TAILURL + i, siteMeta);
		}

	}

	private int getTotalJobs(String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(url);
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
				job.setPostedDate(parseDate(row.getElementsByTagName("td").get(2).asText().trim(), DF));
				if (job.getPostedDate() == null) log.info(" failed to parse date value " + row.getElementsByTagName("td").get(2).asText().trim() + " for job " + job.getUrl());
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException | FailingHttpStatusCodeException e) {
			log.warn("Failed to connect site", e);
		}
	}

	private Job getJobDetail(Job job) {
		try {
			HtmlPage page = client.getPage(job.getUrl());
			List<HtmlElement> category = page.getByXPath("//div[@class='col-xs-12 fontalign-left']");
			job.setCategory(category.get(1).getElementsByTagName("span").get(1).asText());
			List<HtmlElement> spec = page.getByXPath("//div[@class='col-xs-12 fontalign-left']");
			if (spec.get(3).getElementsByTagName("ul").size() > 0) {
				job.setSpec(spec.get(3).getElementsByTagName("ul").get(0).asText());
			}
			List<HtmlElement> prerequisite = page.getByXPath("//div[@class='newspaper']");
			if (prerequisite.size() > 0) {
				job.setPrerequisite(prerequisite.get(0).asText());
			}
			List<HtmlElement> appUrl = page.getByXPath("//div[@class='applylink pull-right']/a");
			job.setApplicationUrl(getBaseUrl() + appUrl.get(0).getAttribute("href"));
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
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
