package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Nordea Bank job site parser<br>
 * URL: "https://www.nordea.com/en/careers/vacant-positions/"
 * 
 * @author tohedul.islum
 * @author iftekar.alam
 * @author tanmoy.tushar
 * @since 2019-01-23
 */
@Service
@Slf4j
public class NordeaBank extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NORDEA_BANK;
	private DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final int JOBS_PER_PAGE = 20;
	private String baseUrl;
	private WebClient client = null;
	private int expectedJobCount;
	private Exception exception;
	private int maxRetry = 0;

	@Override
	public void scrapJobs() throws Exception{
		client = getChromeClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 22);
		int totalPage = getTotalPages(siteMeta.getUrl());
		HtmlPage page = client.getPage(siteMeta.getUrl() + "&p=" + totalPage);
		List<HtmlElement> rowList = page.getByXPath("//tr[@class='job-item']");
		for (HtmlElement row : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			DomNodeList<HtmlElement> cols = row.getElementsByTagName("td");
			HtmlElement link = cols.get(0).getElementsByTagName("a").get(0);
			Job job = new Job(getBaseUrl() + link.getAttribute("href"));
			job.setTitle(link.asText());
			job.setName(job.getTitle());
			job.setCategory(cols.get(1).asText());
			job.setLocation(cols.get(2).asText());
			job.setDeadline(parseDate(cols.get(3).asText(), DF));
			try {
				saveJob(getJobDetails(job), siteMeta);
			}
			/**
			 * Sometime detail page got SocketTimeoutException. But If reload the page in
			 * browser, then it's working. To handle socketTimeoutException ,blindly reload
			 * this page 3 times.
			 */
			catch (SocketTimeoutException e) {
				maxRetry++;
				if (maxRetry < 3) {
					log.info("Failed to load the detail page.. Reloading once again... " + job.getUrl(), e);
					saveJob(getJobDetails(job), siteMeta);
				} else {
					log.warn("Failed to parse job detail of " + job.getUrl(), e);
				}
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).timeout(TIME_1M).get();
		Element jobE = doc.selectFirst("a[class=button button--green white-text]");
		job.setApplicationUrl(jobE.attr("href"));
		job.setReferenceId(jobE.attr("data-wa-job-id"));
		Elements jobSpec = doc.select("div[class=job-details__content-wrapper]>div>ul");
		if (jobSpec.size() == 0) {
			jobSpec = doc.select("div[class=job-details__content-wrapper]>div>div>ul");
			if (jobSpec.size() == 0) job.setSpec(doc.selectFirst("div[class=content]").text());
			else job.setSpec(jobSpec.get(0).text());
			if (jobSpec.size() > 1)	job.setPrerequisite(jobSpec.get(1).text());
		} else {
			job.setSpec(jobSpec.get(0).text());
			if (jobSpec.size() > 1)	job.setPrerequisite(jobSpec.get(1).text());
			jobE = doc.selectFirst("div[class=job-details__content-wrapper]>div>div>ul");
			if (jobE != null && job.getPrerequisite() != null)
				job.setPrerequisite(job.getPrerequisite() + "\n" + jobE.text());
		}
		return job;
	}

	private int getTotalPages(String url) throws IOException {
		HtmlPage page = client.getPage(url);
		HtmlElement el = page.getFirstByXPath("//div[@class='search-message']/p");
		String totalJob = el.asText().split("\\s")[3].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, JOBS_PER_PAGE);
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
