package io.naztech.jobharvestar.scraper.selenium;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * BioCatchy job site parser <br>
 * URL: https://www.biocatch.com/biometrics-cybersecurity-careers
 * 
 * @author jannatul.maowa
 * @since 2019-03-25
 * 
 * @author tanmoy.tushar
 * @since 2019-04-23
 */
@Slf4j
@Service
public class BioCatchy extends AbstractScraper implements Scrapper {

	private static final String SITE = ShortName.BIOCATCH;
	private ChromeDriver driver = null;
	private final String JOB_EL_PATH = "//ul[@class = 'positionDetails']/";
	private int expectedJobCount = 0;
	private Exception ex = null;

	@Override
	public void scrapJobs() throws Exception {
		driver = getChromeDriver();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver.get(siteMeta.getUrl());
		List<String> jobLinks = getJobLinks();
		expectedJobCount = jobLinks.size();
		for (String url : jobLinks) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			try {
			saveJob(getJobDetails(url), siteMeta);
			}catch(Exception e) {
				ex = e;
			}
		}
	}

	private List<String> getJobLinks() {
		List<WebElement> elList = driver.findElementsByClassName("positionItem");
		List<String> jobLinks = new ArrayList<String>();
		for (WebElement el : elList) {
			jobLinks.add(el.getAttribute("href"));
		}
		return jobLinks;
	}

	private Job getJobDetails(String url) {
		Job job = new Job(url);
		try {
			driver.get(url);
			WebElement el = driver.findElementByClassName("positionName");
			job.setTitle(el.getText().trim());
			job.setName(job.getTitle());

			el = driver.findElementByXPath("//div[@class = 'col-lg-6 col-md-12 col-sm-12 positionInfo']");
			job.setSpec(el.getText().trim());
			try {
				el = driver.findElementByXPath(JOB_EL_PATH + "li[1]");
				job.setCategory(el.getText().trim());

				el = driver.findElementByXPath(JOB_EL_PATH + "li[2]");
				job.setLocation(el.getText().trim());

				el = driver.findElementByXPath(JOB_EL_PATH + "li[3]");
				job.setType(el.getText().trim());

				el = driver.findElementByXPath(JOB_EL_PATH + "li[4]");
				job.setReferenceId(el.getText().split(":")[1].trim());
			} catch (NoSuchElementException e) {
				return job;
			}
			return job;
		} catch (NoSuchElementException e) {
			log.warn("Failed to parsed job" + url, e);
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
	protected int getExpectedJob() {
		return expectedJobCount;
	}

	@Override
	protected void destroy() {
		driver.quit();
	}

	@Override
	protected Exception getFailedException() {
		return ex;
	}

}
