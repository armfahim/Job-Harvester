package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.jsoup.HttpStatusException;
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
 * Brookfield Asset Management job site parser. <br>
 * URL: https://www.brookfield.com/about-us/careers/opportunities
 * 
 * @author jannatul.maowa
 * @author tanmoy.tushar
 * @since 2019-02-13
 */
@Slf4j
@Service
public class BrookField extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BROOKFIELD_ASSET_MAN_A;
	private static WebClient webClient = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		webClient = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws InterruptedException, IOException {
		try {
			HtmlPage page = webClient.getPage(site.getUrl());
			webClient.waitForBackgroundJavaScript(3 * 1000);
			List<HtmlElement> jobListE = page.getBody().getByXPath("//tbody/tr");
			expectedJobCount = jobListE.size();
			for (HtmlElement row : jobListE) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				List<HtmlElement> columns = row.getElementsByTagName("td");
				HtmlElement jobUrlE = columns.get(0).getElementsByTagName("a").get(0);
				Job job = new Job(jobUrlE.getAttribute("href"));
				job.setTitle(jobUrlE.getTextContent());
				job.setName(job.getTitle());
				try {
					saveJob(getJobDetail(job), site);
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse job details of " + job.getUrl(), getFailedException());
				}
			}
		} catch (IOException e) {
			log.warn("Failed to load JobSite", e);
			throw e; 
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		String url = job.getUrl();
		Document doc = null;
		try {
			if(!url.contains("brookfield")) {
				String concate = url.substring(8, 39);
				url = url.replace(concate, "brookfieldam");
			}
			doc = Jsoup.connect(url).get();
			Element jobE = doc.selectFirst("div[class=description]");
			job.setSpec(jobE.text());
			jobE = doc.selectFirst("li[title=Location]");
			if(jobE != null) job.setLocation(jobE.text().trim());
			jobE = doc.selectFirst("li[id=resumator-job-employment]");
			if(jobE != null) job.setType(jobE.text().trim());
			jobE = doc.selectFirst("li[title=Department]");
			if(jobE != null) job.setCategory(jobE.text().trim());
			return job;
		} catch (HttpStatusException e) {
			return getHttpStatusExceptionDetail(job);
		} catch (NullPointerException e) {
			return getAnotherLayoutDetail(doc, job);
		}
	}
	
	private Job getAnotherLayoutDetail(Document doc, Job job) {
		Element jobE = doc.selectFirst("span[class=text-muted]");
		job.setLocation(jobE.text());
		jobE= doc.selectFirst("div[class=sc-fAjcbJ llvTUS]");
		job.setSpec(jobE.text());
		return job;
	}
	
	private Job getHttpStatusExceptionDetail(Job job) throws IOException {
		webClient.getOptions().setJavaScriptEnabled(false);
		HtmlPage page = webClient.getPage(job.getUrl());
		HtmlElement jobE = page.getFirstByXPath("//div[@class='container']/div/p");
		job.setSpec(jobE.asText());
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
