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
 * Creditas job site parser. <br>
 * URL: https://jobs.kenoby.com/creditas/
 * 
 * @author Shadman Shahriar
 * @author tanmoy.tushar
 * @since 2019-03-25
 */
@Slf4j
@Service
public class Creditas extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CREDITAS;
	private static WebClient webClient;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		webClient = getChromeClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(TIME_5S);
		List<HtmlElement> jobList = page.getByXPath("//div[@id='content']/div/div/div/a");
		expectedJobCount = jobList.size();
		browseJobList(jobList, siteMeta);
	}

	private void browseJobList(List<HtmlElement> jobCategory, SiteMetaData site) throws InterruptedException {
		for (HtmlElement el : jobCategory) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = el.getAttribute("href");
			try {
				saveJob(getJobDetails(url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + url, e);
			}
		}
	}

	private Job getJobDetails(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text().trim());
		job.setName(job.getTitle());
		jobE = doc.selectFirst("h3");
		String[] parts = jobE.text().trim().split(" - ");
		job.setCategory(parts[0].trim());
		job.setLocation(parts[1].trim());
		job.setSpec(doc.selectFirst("div[class=description]").text().trim());
		jobE = doc.selectFirst("a[class=btn btn-primary btn-apply send-your-resume]");
		job.setApplicationUrl(jobE.attr("href"));
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
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
