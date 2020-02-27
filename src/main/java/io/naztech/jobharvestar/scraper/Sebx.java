package io.naztech.jobharvestar.scraper;

import java.io.IOException;
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
 * Skand Enskilda Banken<br>
 * URL: https://sebx.io/careers/
 * 
 * @author Armaan Seraj Choudhury
 * @author tanmoy.tushar
 * @since 2019-02-18
 */
@Slf4j
@Service
public class Sebx extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SKAND_ENSKILDA_BANKEN_X;
	private static WebClient client;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(ShortName.SKAND_ENSKILDA_BANKEN_X));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 15);
		HtmlPage page = client.getPage(getBaseUrl() + "/career");
		client.waitForBackgroundJavaScript(TIME_5S);
		getSummaryPage(page, siteMeta);
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) throws InterruptedException {
		List<HtmlElement> row = page.getByXPath("//div[@class='careers-grid__row grid-row']/div/a");
		expectedJobCount = row.size();
		for (HtmlElement el : row) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.getAttribute("href"));
			job.setTitle(el.asText());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job deatils of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobSpec = doc.selectFirst("div[class=grid-col-12 grid-col-m-10 grid-push-m-2]");
		job.setSpec(jobSpec.text().trim());
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