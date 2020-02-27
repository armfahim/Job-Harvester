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
 * rubrik job site scraper. <br>
 * URL: https://www.rubrik.com/company/careers/ 
 * 
 * @author Asadullah Galib
 * @author tanmoy.tushar
 * @since 2019-03-19
 */
@Slf4j
@Service
public class Rubrik extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.RUBRIK;
	private String baseUrl;
	private static WebClient client = null;
	private int expectedJobCount = 0;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta)
			throws IOException, IllegalStateException, RuntimeException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 22);
		getSummaryPages(getBaseUrl(), siteMeta);
	}

	private void getSummaryPages(String url, SiteMetaData site) throws PageScrapingInterruptedException,IllegalStateException, IOException {
		try {
			HtmlPage page;
			page = client.getPage(site.getUrl());
			client.waitForBackgroundJavaScript(5000);
			List<HtmlElement> el = page.getByXPath("//div[@class='col-xs-12 col-sm-6 col-lg-4 item']");
			for (HtmlElement tr : el) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				HtmlElement outerurl = tr.getElementsByTagName("a").get(0);
				HtmlPage page1 = client.getPage(outerurl.getAttribute("href"));
				client.waitForBackgroundJavaScript(2000);
				List<HtmlElement> ab = page1.getByXPath("//div[@class='job']");
				expectedJobCount += ab.size();
				for (HtmlElement tb : ab) {
					if (isStopped()) throw new PageScrapingInterruptedException();
					Job job = new Job(baseUrl + tb.getElementsByTagName("a").get(0).getAttribute("href"));
					try {
						saveJob(getJobDetails(job), site);						
					} catch (Exception e) {
						exception = e;
					}
				}
			}
		} catch (IOException e) {
			log.warn("Failed to parse Site: " + getSiteName(), e);
			throw e;
		}
	}

	private Job getJobDetails(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).get();
			Element jobE = doc.selectFirst("h1");
			job.setTitle(jobE.text());
			job.setName(job.getTitle());
			jobE = doc.selectFirst("span[class=hero__subtitle]");
			job.setLocation(jobE.text());
			jobE = doc.selectFirst("div[class=wysiwyg col-md-10 col-lg-8 col-lg-push-1]");
			job.setSpec(jobE.text());
		} catch (IOException e) {
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
