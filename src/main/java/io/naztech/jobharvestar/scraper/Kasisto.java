package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Kasisto job site <br>
 * URL: https://kasisto.com/careers/
 * 
 * @author masum.billa
 * @author iftekar.alam
 * @since 2019-03-25
 */
@Slf4j
@Service
public class Kasisto extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.KASISTO;
	private String baseUrl;
	private static WebClient webClient = null;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		try {
			getSummaryPages(doc, siteMeta);
		} catch (Exception e) {
			log.warn("Failed to parse jobList of " + siteMeta.getUrl(), e);
		}
	}

	protected void getSummaryPages(Document doc, SiteMetaData siteMeta) throws IOException {
		Elements jobList = doc.select("table[class=jobs-table]>tbody>tr");
		expectedJobCount = jobList.size();
		for (Element el : jobList) {
			Job job = new Job(el.getElementsByTag("td").get(0).getElementsByTag("a").attr("href"));
			job.setTitle(el.getElementsByTag("td").get(0).getElementsByTag("a").text().trim());
			job.setName(job.getTitle());
			job.setCategory(el.getElementsByTag("td").get(1).getElementsByTag("a").text().trim());
			job.setLocation(el.getElementsByTag("td").get(2).child(0).getElementsByTag("a").text().trim());
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse details of " + job.getUrl(), e);
			}
		}
	}

	protected Job getJobDetails(Job job) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		webClient=getChromeClient();
		HtmlPage page = webClient.getPage(job.getUrl());
		List<FrameWindow> frames = page.getFrames();
		page = (HtmlPage) frames.get(0).getEnclosedPage();
		webClient.waitForBackgroundJavaScript(TIME_10S);
		job.setSpec(page.getBody().getOneHtmlElementByAttribute("div", "id", "content").asText().trim());
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
