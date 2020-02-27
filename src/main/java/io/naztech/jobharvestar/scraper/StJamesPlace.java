package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

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
 * St James's Place job site parsing class. <br>
 * URL: https://careers.sjp.co.uk/vacancies.html
 * 
 * @author tanmoy.tushar
 * @author sanowar.ali
 * @since 2019-02-20
 */
@Service
@Slf4j
public class StJamesPlace extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ST_JAMES_PLACE;
	private String baseUrl;
	private WebClient client = null;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.ST_JAMES_PLACE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException, IOException {
		this.baseUrl = site.getUrl();
		client = getFirefoxClient();
		getSummaryPages(getBaseUrl(), site);
	}

	private void getSummaryPages(String url, SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		try {
			HtmlPage page = client.getPage(url);
			client.waitForBackgroundJavaScript(5 * TIME_1S);
			List<HtmlElement> list = page.getByXPath("//div[@class='jobpost_wrapper']/h2");
			expectedJobCount = list.size();
			for (HtmlElement row : list) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				HtmlElement link = row.getElementsByTagName("a").get(0);
				Job job = new Job(link.getAttribute("href"));
				try {
					saveJob(getJobDetails(job), site);					
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse job details " + job.getUrl(), getFailedException());
				}
			}
		} catch (IOException e) {
			log.error("Failed to parse job list... Site exiting " + url, e);
			throw e;
		}
	}

	public Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Element jobE = doc.selectFirst("h2");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("div[class=job_summary]");
		job.setSpec(jobE.text());
		jobE = doc.selectFirst("div[id=location]");
		if(jobE != null) job.setLocation(jobE.text());
		jobE = doc.selectFirst("div[id=hours]");
		if(jobE != null) job.setType(jobE.text());
		jobE = doc.selectFirst("div[id=department]");
		if(jobE != null) job.setCategory(jobE.text());
		try {
			jobE = doc.select("div[class=class_value]").get(5);
			if(jobE != null) job.setDeadline(parseDate(jobE.text(),DF));
		} catch (Exception e) {
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