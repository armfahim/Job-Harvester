package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Barclays Job Site Scraper<br>
 * URL: https://search.jobs.barclays/search-jobs
 * https://barclays.taleo.net/careersection/2/moresearch.ftl
 * 
 * @author Mahmud Rana
 * @since 2019-02-03
 * 
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-04-23
 */
@Slf4j
@Service
public class Barclays extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BARCLAYS;
	private String baseUrl;
	private static WebClient webClient = null;
	private int expectedJobCount;
	private Exception exception;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl=siteMeta.getUrl().substring(0, 28);
		webClient = getFirefoxClient();
		try {
			browseJobList(siteMeta);
		} catch (Exception e) {
			log.warn("Failed to parse list of " + siteMeta.getUrl(), e);
		}
	}

	private void browseJobList(SiteMetaData siteMeta) throws InterruptedException, FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(TIME_10S);
		expectedJobCount = Integer.parseInt(((DomNode) page.getFirstByXPath("//p[@id='status']")).asText().split(" ")[0].trim());
		HtmlElement nextButton = page.getFirstByXPath("//a[@class='next']");
		for (int i = 0; i <Integer.parseInt(((DomNode) page.getFirstByXPath("//span[@class='pagination-total-pages']")).asText().split("of")[1].trim()); i++) {
			List<HtmlElement> jobList = page.getByXPath("//a[@class='job']");
			jobList.forEach(el -> { Job job = new Job(getBaseUrl()+el.getAttribute("href")); 
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
			});
			try {
				page = nextButton.click();
			} catch (Exception e2) {
				log.warn("Failed to click page of "+i);
			}
			Thread.sleep(TIME_4S);
			nextButton = page.getFirstByXPath("//a[@class='next']");
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.selectFirst("div[class=ats-description]").text().trim());
		Element jobE = doc.select("span[class=bluetext]").get(1);
		if(jobE != null) job.setLocation(jobE.text().trim());
	    jobE = doc.select("span[class=bluetext]").get(4);
		if(jobE != null)  job.setCategory(jobE.text().trim());
		jobE = doc.select("span[class=bluetext]").get(2);
		if(jobE != null)  job.setReferenceId(jobE.text().trim());
		jobE = doc.select("span[class=bluetext]").get(0);
		if(jobE != null)  job.setPostedDate(parseDate(jobE.text().replace(".", "").trim(), DF));
		jobE = doc.selectFirst("a[class=button btn job-apply top]");
		if(jobE != null) job.setApplicationUrl(jobE.attr("href"));
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
