package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
 * Unicredit Italy job site scrapper <br>
 * URL: https://jobs.unicredit.eu/search/?q
 * 
 * @author naym.hossain
 * @author tanmoy.tushar
 * @since 2019-01-24
 */
@Slf4j
@Service
public class UnicreditItaly extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UNICREDIT_ITALY;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private String baseUrl;
	private WebClient client;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.UNICREDIT_ITALY));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		client = getFirefoxClient();
		client.setJavaScriptTimeout(TIME_10S);
		this.baseUrl = site.getUrl().substring(0,25);
		HtmlPage page = client.getPage(getBaseUrl() + "/Announcement?search=");
		List<HtmlElement> jobList = page.getByXPath("//ul[@class='list list-type-1']/li/article/a");
		browseJobList(jobList, site);
	}

	private void browseJobList(List<HtmlElement> jobList, SiteMetaData site) throws InterruptedException {
		expectedJobCount = jobList.size();
		for(HtmlElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + el.getAttribute("href");
			try {
				saveJob(getJobDetail(url), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail page of " + url, e);
			}
		}
	}

	private Job getJobDetail(String url) throws IOException {
		Job job = new Job(url);
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Element jobE = doc.selectFirst("h1");
		job.setTitle(jobE.text());
		job.setName(job.getTitle());
		jobE = doc.getElementById("description");
		job.setSpec(jobE.text());
		Elements jobInfo = doc.select("ul[class=attr location]>li>div>span[class=title]");
		for (Element el : jobInfo) {
			if(el.text().contains("Location")) job.setLocation(el.nextElementSibling().text());
			if(el.text().contains("Working Type")) job.setType(el.nextElementSibling().text());
			if(el.text().contains("Data rilascio")) job.setPostedDate(parseDate(el.nextElementSibling().text(), DF));
		}
		jobInfo = doc.select("div[class=jobDetail]>div[class=content]");
		for (Element el : jobInfo) {
			if(el.equals(jobInfo.get(0))) continue;
			if(job.getPrerequisite() == null) job.setPrerequisite(el.text());
			else job.setPrerequisite(job.getPrerequisite() + "\n" + el.text());
		}
		job.setReferenceId(job.getUrl().split("/Detail/")[1]);
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
