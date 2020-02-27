package io.naztech.jobharvestar.scraper.selenium;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;

/**
 * Unity Technologies job site parsing class. <br>
 * URL: https://careers.unity.com/find-position
 * 
 * @author Shajedul Islam
 * @since 2019-03-18
 * 
 * @author tanmoy.tushar
 * @since 2019-04-10
 */
@Service
public class UnityTechnologies extends AbstractSeleniumJobLink {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private static final String SITE = ShortName.UNITY_TECHNOLOGIES;

	@Override
	protected Job getJobDetail(Job job) {
		try (WebClient client = getFirefoxClient()) {
			HtmlPage page = client.getPage(job.getUrl());
			HtmlElement jobE = page.getFirstByXPath("//h1[@class='job-title']");
			job.setTitle(jobE.asText().trim());
			job.setName(job.getTitle());
			jobE = page.getFirstByXPath("//h3[@class='subhead']");
			job.setLocation(jobE.asText().trim());
			jobE = page.getFirstByXPath("//div[@class='section-role-description__job-info img-rounded']/p[2]");
			job.setCategory(jobE.asText().trim());
			jobE = page.getFirstByXPath("//div[@class='col-xs-12 col-sm-8 col-sm-offset-1 section-role-description__job-description']");
			job.setSpec(jobE.asText().trim());
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
			return job;
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
		return "//div[@class='component-open-positions__position']/a";
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
