package io.naztech.jobharvestar.scraper.brassring;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;

/**
 * Ameriprise Financial jobs site parse. <br>
 * URL:
 * https://sjobs.brassring.com/TGnewUI/Search/Home/Home?partnerid=25678&siteid=5275#keyWordSearch=&locationSearch=
 * 
 * @author tanmoy.tushar
 * @since 2019-02-26
 */
@Service
public class AmeripriseFinancial extends AbstractBrassring {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String SITE = ShortName.AMERIPRISE_FINANCIAL;
	private static final String JOB_SPEC_PATH = "//div[@class='questionClass']/div";

	@Override
	protected Job getJobDetails(String jobUrl, ChromeDriver driver, WebDriverWait wait) {
		try {
			driver.get(jobUrl);
			Job job = new Job(jobUrl);
			List<WebElement> jobD = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(JOB_SPEC_PATH)));
			job.setTitle(jobD.get(0).getText().trim());
			job.setName(job.getTitle());
			job.setLocation(jobD.get(1).getText().trim());
			job.setReferenceId(jobD.get(2).getText().trim());
			job.setSpec(jobD.get(3).getText().trim());
			job.setPrerequisite(jobD.get(5).getText().trim());
			String[] types = jobD.get(jobD.size() - 4).getText().split("/ Part-Time");
			if (types.length > 1)
				job.setType(types[1].trim());
			String[] cats = jobD.get(jobD.size() - 2).getText().split("Family");
			if (cats.length > 1)
				job.setCategory(cats[1].trim());
			return job;
		} catch (TimeoutException e) {
			log.warn("Failed to parse job details of " + jobUrl, e);
		}
		return null;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
