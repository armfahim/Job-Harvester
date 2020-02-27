package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
 * Randstad Hr Outsourcing<br>
 * URL: https://www.randstad.com/jobs/
 * 
 * @author tohedul.islum
 * @author tanmoy.tushar
 * @since 2019-03-18
 */
@Service
@Slf4j
public class RandstadHrOutsourcing extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.RANDSTAD_HR_OUTSOURCING;
	private DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private String baseUrl;
	private WebClient webClient = null;
	private static final int JOBS_PER_PAGE = 10;
	private static final String TAILURL = "/jobs/page-";
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		webClient = getChromeClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 24);
		int totalPage = getTotalPage(siteMeta);
		getSummaryPage(siteMeta.getUrl(), siteMeta);
		for (int i = 2; i < totalPage; i++) {
			getSummaryPage(getBaseUrl() + TAILURL + i, siteMeta);
		}
	}

	private int getTotalPage(SiteMetaData siteMeta)	throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		List<HtmlElement> totalJobEl = page.getByXPath("//span[@id='ctl06_ctl05_NrOfJobsLabel']");
		String totalJob = totalJobEl.get(0).asText().split("of")[1].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, JOBS_PER_PAGE);
	}

	private void getSummaryPage(String url, SiteMetaData siteMeta) throws InterruptedException {
		try {
			HtmlPage page = webClient.getPage(url);
			List<HtmlElement> jobList = page.getByXPath("//div[@id='ctl06_ctl05_JobResultsDiv']/article");
			for (HtmlElement li : jobList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				HtmlElement link = li.getElementsByTagName("a").get(0);
				String jobUrl = getBaseUrl() + link.getAttribute("href");
				try {
					saveJob(getJobDetail(jobUrl), siteMeta);		
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed to parse job list of " + url,  e);
		}
	}

	private Job getJobDetail(String url) throws InterruptedException {
		Job job = new Job(url);
		try {
			Document doc = Jsoup.connect(url).get();
			Element jobE = doc.selectFirst("h1 > span");
			job.setTitle(jobE.text());
			job.setName(job.getTitle());
			jobE = doc.selectFirst("span[id=ctl06_ctl05_LocationLabel]");
			if(jobE != null) job.setLocation(jobE.text());
			jobE = doc.selectFirst("span[id=js_datePosted] > time");
			if(jobE != null) job.setPostedDate(parseDate(jobE.attr("datetime"), DF));
			jobE = doc.selectFirst("span[id=js_jobType]");
			if(jobE != null) job.setType(jobE.text());
			jobE = doc.selectFirst("span[id=ctl06_ctl05_ReferenceLabel]");
			if(jobE != null) job.setReferenceId(jobE.text());
			jobE = doc.selectFirst("a[id=ctl06_ctl05_ApplyTopHyperLink]");
			if(jobE != null) job.setApplicationUrl(getBaseUrl() + jobE.attr("href")); 
			jobE = doc.selectFirst("div[id=js_description]");
			job.setSpec(jobE.text());
			return job;
		} catch (IOException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
			return null;
		}
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
