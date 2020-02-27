package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * ASSURANT<br>
 * URL: https://jobs.assurant.com/search-jobs
 * 
 * @author tohedul.islum
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-03-04
 */
@Slf4j
@Service
public class Assurant extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ASSURANT;
	private String baseUrl;
	private static WebClient webClient = null;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.ASSURANT));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		webClient = getChromeClient();
		this.baseUrl = siteMeta.getUrl().substring(0, 25);
		List<Job> jobList = new ArrayList<>();
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		int totalPage = getTotalPage(page);
		for(int i = 0; i < totalPage; i++) {
			jobList.addAll(getSummaryPages(page, siteMeta));
			if(i == totalPage - 1) break;
			HtmlElement nextPageButton = page.getFirstByXPath("//a[@class = 'next']");
			page = nextPageButton.click();
			Thread.sleep(RandomUtils.nextInt(TIME_10S, TIME_10S * 2));			
		}
		log.info("Total Job Link Found: " + jobList.size());
		for(Job job: jobList) {
			try {
				saveJob(getJobDetail(job), siteMeta);				
			} catch(Exception e) {
				exception = e;
				log.warn("Failed to parse details of "+job.getUrl(),e);
			}
		}
	}

	private int getTotalPage(HtmlPage page) throws InterruptedException {
		HtmlElement totalJob = page.getFirstByXPath("//h1[@role = 'status']");
		String totalJ = totalJob.asText().split(" ")[0].trim();
		expectedJobCount = Integer.parseInt(totalJ);
		return getPageCount(totalJ, 15);
	}

	private List<Job> getSummaryPages(HtmlPage page, SiteMetaData siteMeta)
			throws InterruptedException, FailingHttpStatusCodeException, IOException {
		List<Job> jobL = new ArrayList<>();
		List<HtmlElement> jobList = page.getByXPath("//section[@id='search-results-list']/ul/li");
		for (HtmlElement li : jobList) {
			HtmlElement link = li.getElementsByTagName("a").get(0);
			Job job = new Job(getBaseUrl() + link.getAttribute("href"));
			job.setTitle(link.getElementsByTagName("h2").get(0).asText());
			job.setName(job.getTitle());
			job.setLocation(link.getElementsByTagName("span").get(0).asText());
			jobL.add(job);
			
		}
		return jobL;
	}
	private Job getJobDetail(Job job) throws IOException {
		Document doc= Jsoup.connect(job.getUrl()).get();
		Element refId=doc.selectFirst("span[class=job-id job-info]");
		job.setReferenceId(refId.text().trim().split("Number")[1].trim());
		Element spec=doc.selectFirst("div[class=ats-description]");		
		job.setSpec(spec.text().trim());
		Element appUrl = doc.selectFirst("a[class=button job-apply circle-button top]");
		if(appUrl !=null) job.setApplicationUrl(appUrl.attr("href"));
		else job.setApplicationUrl(doc.selectFirst("a[id=anchor-apply]").attr("href"));
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