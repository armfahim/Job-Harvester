/**
 * 
 */
package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.talent.model.Job;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Tanbirul Hashan
 *
 */
@Slf4j
public class TestTryg {

	private static final String SITE = "JANUS";
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private String pageUrl = "https://career5.successfactors.eu/career?company=MUSI&career_ns=job_listing_summary&navBarLevel=JOB_SEARCH";
	private List<Job> jobList = new ArrayList<>();
	private String baseUrl;
	private int jobCount;
	private static WebClient webClient = null;

	@Test
	public void getScrapedJobs() {
		this.baseUrl = pageUrl.substring(0, 34);
		System.out.println(baseUrl);
		try {
			HtmlPage page = getWebClient().getPage(pageUrl);
			Thread.sleep(2000);
			while (page != null) {
				List<HtmlAnchor> anchorList = page.getByXPath("//a[@class='jobTitle']");
				for (HtmlAnchor htmlAnchor : anchorList) {
					Job job = new Job(this.baseUrl + htmlAnchor.getHrefAttribute());
					job.setTitle(htmlAnchor.asText());
					job.setName(job.getTitle());
					jobList.add(job);
					System.out.println(jobList.size());
				}
				page = getNextPage(page);
			}
			for (Job job : jobList) {
				show(getJobDetails(job));
			}

		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn(SITE + " Failed to get page  :", e);
		} catch (InterruptedException e) {
			log.warn(SITE + " Page loading interrupted :", e);
		}
	}

	private HtmlPage getNextPage(HtmlPage page) {

		try {
			HtmlElement nextPageAnchor = page.getBody().getOneHtmlElementByAttribute("a", "id", "45:_next");
			page = nextPageAnchor.click();
			Thread.sleep(2000);
			return page;
		} catch (ElementNotFoundException | IOException e) {
			log.info(SITE + " Next page traversal ended  :" + e);
		} catch (InterruptedException e) {
			log.warn(SITE + " Next page loading interrupted :" + e);
			getNextPage(page);
		}
		return null;
	}

	private Job getJobDetails(Job job) {
		if (job.getUrl() == null)
			return null;
		try {
			HtmlPage page = getWebClient().getPage(job.getUrl());
			Thread.sleep(3000);
			List<HtmlElement> jobDetList = page.getByXPath("//div[@id='jobAppPageTitle']/div[1]/div[1]//b");
			job.setReferenceId(jobDetList.get(0).asText());
			job.setPostedDate(LocalDate.parse(jobDetList.get(1).asText(), DF));
			if (jobDetList.size() == 4)
				job.setLocation(jobDetList.get(2).asText() + " - " + jobDetList.get(3).asText());
			else
				job.setLocation(jobDetList.get(2).asText());

			HtmlElement jobSpecElement = page.getBody().getOneHtmlElementByAttribute("div", "class",
					"joqReqDescription");
			job.setSpec(jobSpecElement.asText().trim());
			return job;
		} catch (FailingHttpStatusCodeException | IOException | ElementNotFoundException | InterruptedException e) {
			log.warn(SITE + " Failed to parse job details" + e);
		}
		return null;
	}

	private void show(Job job) {
		if (job == null)
			return;
		System.out.println("job Count: " + ++jobCount);
		if (job.getName() != null)
			System.out.println("Title: " + job.getName());
		if (job.getLocation() != null)
			System.out.println("Location: " + job.getLocation());
		if (job.getUrl() != null)
			System.out.println("job URL: " + job.getUrl());
		if (job.getApplicationUrl() != null)
			System.out.println("Application URL: " + job.getApplicationUrl());
		if (job.getPostedDate() != null)
			System.out.println("Posted Date: " + job.getPostedDate());
		if (job.getSpec() != null)
			System.out.println("Specifications: " + job.getSpec());
		if (job.getPrerequisite() != null)
			System.out.println("Prerequisite: " + job.getPrerequisite());
		System.out.println("=================================");

	}

	private WebClient getWebClient() {
		if (webClient == null) {
			webClient = new WebClient(BrowserVersion.FIREFOX_52);
			webClient.getOptions().setDoNotTrackEnabled(true);
			webClient.getOptions().setMaxInMemory(0);
			webClient.getOptions().setTimeout(30 * 1000);
			getWebClient().getOptions().setCssEnabled(false);
		}
		return webClient;
	}
}
