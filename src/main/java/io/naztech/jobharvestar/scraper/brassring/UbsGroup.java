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
 * UBS Group jobs site parse. <br>
 * URL: https://jobs.ubs.com/TGnewUI/Search/Home/Home?partnerid=25008&siteid=5012&PageType=searchResults&SearchType=linkquery&LinkID=3108#home
 * 
 * @author Mahamud Rana
 * @since 2019-01-27
 * 
 * @author tanmoy.tushar
 * @since 2019-03-25
 */
@Service
public class UbsGroup extends AbstractBrassring {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String SITE = ShortName.UBS_GROUP;
	private static final String JOB_SPEC_PATH = "//div[@class='questionClass']/div";

	@Override
	protected Job getJobDetails(String jobUrl, ChromeDriver driver, WebDriverWait wait) {
		try {
			driver.get(jobUrl);
			Job job = new Job(jobUrl);
			List<WebElement> jobDList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(JOB_SPEC_PATH)));
			if(jobDList.size() > 9) {
				job.setTitle(jobDList.get(0).getText().trim());
				job.setName(job.getTitle());
				job.setLocation(jobDList.get(1).getText().trim());
				job.setCategory(jobDList.get(2).getText().trim());
				String[] ref = jobDList.get(4).getText().split("#");
				if (ref.length > 1)
					job.setReferenceId(ref[1].trim());
				String[] type = jobDList.get(6).getText().split("Type");
				if (type.length > 1)
					job.setType(type[1].trim());
				job.setSpec(jobDList.get(7).getText().trim());
				job.setPrerequisite(jobDList.get(9).getText().trim());		
			}
			return job;
		} catch (TimeoutException e) {
			log.warn("Failed to parse job details of " + jobUrl, e);
			throw e;
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
}