package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomUtils;
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
 * CGI Global job site parser. <br>
 * URL: https://www.cgi.com/en/careers-search?country_id=&job_type_id=Full+Time&category_id=
 * 
 * @author tanmoy.tushar
 * @since 2019-10-16
 */
@Slf4j
@Service
public class CgiGlobal extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.CGI;
	private static final String ROW_LIST = "//table[@class='views-table table-result-search cols-5 responsive']/tbody/tr";
	private int expectedJobCount;
	private Exception exception;
	private WebClient client;

	@Override
	public void scrapJobs() throws Exception {
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		HtmlPage page = client.getPage(site.getUrl());
		int totalPage = getTotalPage(page);
		List<HtmlElement> jobList = page.getByXPath(ROW_LIST);
		for (int i = 0; i < totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				browseJobList(jobList, site);
				if (i == totalPage - 1) break;				
				getNextButton(page).click();
				Thread.sleep(RandomUtils.nextInt(TIME_10S, TIME_4S * 4));
				jobList = page.getByXPath(ROW_LIST);
			} catch (Exception e) {
				log.warn("Failed to parse job list no - " + (i + 1));
			}
		}
	}

	private void browseJobList(List<HtmlElement> jobList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (HtmlElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			List<HtmlElement> trList = el.getElementsByTagName("td");
			HtmlElement refId = trList.get(0).getElementsByTagName("a").get(0);
			Job job = new Job(refId.getAttribute("href"));
			job.setReferenceId(refId.asText().trim());
			job.setTitle(trList.get(1).asText().trim());
			job.setName(job.getTitle());
			job.setCategory(trList.get(2).asText().trim());
			job.setLocation(trList.get(3).asText().trim() + ", " + trList.get(4).asText().trim());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element jobE = doc.selectFirst("section[class=job-desc]");
		job.setSpec(jobE.selectFirst("div[class=job-posting-content]").text().trim());
		Elements jobInfo = jobE.select("header>div");
		if (jobInfo.size() > 5) {
			job.setApplicationUrl(jobInfo.get(0).getElementsByTag("a").attr("href"));
			if (jobInfo.get(4).text().contains("Type")) job.setType(jobInfo.get(4).text().split(":")[1].trim());
		}
		return job;
	}

	private int getTotalPage(HtmlPage page) {
		HtmlElement el = page.getFirstByXPath("//div[@id='block-cgi-default-content']//header");
		String totalJob = el.asText().split(Pattern.quote("("))[1].split(Pattern.quote(")"))[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 50);
	}

	private HtmlElement getNextButton(HtmlPage page) {
		return page.getFirstByXPath("//li[@class='pager__item pager__item--next']/a");
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
