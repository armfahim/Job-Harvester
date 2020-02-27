package io.naztech.jobharvestar.scraper.selenium;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;

/**
 * Lemon Way job site scraper. <br>
 * URL: https://www.welcometothejungle.co/companies/lemon-way/jobs
 * 
 * @author Asadullah Galib
 * @since 2019-03-31
 * 
 * @author tanmoy.tushar
 * @author fahim.reza
 * @since 2019-04-21
 * 
 */
@Service
public class LemonWay extends AbstractSeleniumJobLink {
	private static final String SITE = ShortName.LEMON_WAY;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";
	
	@Override
	protected Job getJobDetail(Job job) {
		try {
			Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
			String baseUrl=job.getUrl().substring(0, 34);
			Element jobE = doc.selectFirst("h1");
			job.setTitle(jobE.text().trim());
			job.setName(job.getTitle());
			jobE = doc.select("ul[class=sc-1qc42fc-4 gETmlE]>li").get(1);
			job.setLocation(jobE.text().trim());
			jobE = doc.selectFirst("a[class=sc-eerKOB gdtCTh xz9rvh-0 kvNfuE]");
			job.setApplicationUrl(baseUrl+jobE.attr("href"));
			jobE = doc.select("div[class=sc-11obzva-1 czRmi]").get(2);
			job.setSpec(jobE.text().trim());
			jobE = doc.select("div[class=sc-11obzva-1 czRmi]").get(3);
			job.setPrerequisite(jobE.text().trim());
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
		return null;
	}

	@Override
	protected String getRowListPath() {
		return "//a[@class='sc-1kkiv1h-2 cHZvby']";
		
	}

	@Override
	protected String getFirstPageCatPath() {
		return null;
	}

	@Override
	protected String getFirstPageLocPath() {
		return null;
	}

	@Override
	protected String getTitleCssQuery() {
		return null;
	}

	@Override
	protected String getLocationCssQuery() {
		return null;
	}

	@Override
	protected String getCategoryCssQuery() {
		return null;
	}

	@Override
	protected String getJobTypeCssQuery() {
		return null;
	}

	@Override
	protected String getRefCssQuery() {
		return null;
	}

	@Override
	protected String getSpecCssQuery() {
		return null;
	}

	@Override
	protected String getPreReqCssQuery() {
		return null;
	}

	@Override
	protected String getPostedDateCssQuery() {
		return null;
	}

	@Override
	protected String getApplyUrlCssQuery() {
		return null;
	}

	@Override
	protected DateTimeFormatter[] getDateFormats() {
		return null;
	}
}