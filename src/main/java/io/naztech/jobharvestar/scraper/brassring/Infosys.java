package io.naztech.jobharvestar.scraper.brassring;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import lombok.extern.slf4j.Slf4j;

/**
 * Infosys jobs site parse. <br>
 * URL: https://sjobs.brassring.com/TGnewUI/Search/Home/Home?partnerid=25633&siteid=5439#keyWordSearch=&locationSearch=
 * 
 * @author iftekar.alam
 * @since 2019-10-17
 */
@Slf4j
@Service
public class Infosys extends AbstractBrassring {
	private static final String SITE = ShortName.INFOSYS;

	@Override
	protected Job getJobDetails(String jobUrl, ChromeDriver driver, WebDriverWait wait) {
		try {
			driver.get(jobUrl);
			Job job = new Job(jobUrl);
			List<WebElement> jobDList1 = wait
					.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='ng-scope']")));
			if (jobDList1.size() == 12) {
				job.setTitle(jobDList1.get(2).getText().trim());
				job.setName(jobDList1.get(3).getText().trim());
				job.setReferenceId(jobDList1.get(4).getText().trim());
				job.setSpec(jobDList1.get(5).getText().trim());
				String loc=jobDList1.get(8).getText().split("Work Location")[1].trim();
				if(loc.contains("Anywhere in the")) job.setLocation(loc.split("Anywhere in the")[1].trim());
				else job.setLocation(loc);
			} else if (jobDList1.size() == 11) {
				job.setTitle(jobDList1.get(2).getText().trim());
				job.setReferenceId(jobDList1.get(3).getText().trim());
				job.setSpec(jobDList1.get(4).getText().trim());
				String loc=jobDList1.get(7).getText().split("Work Location")[1].trim();
				if(loc.contains("Anywhere in the")) job.setLocation(loc.split("Anywhere in the")[1].trim());
				else job.setLocation(loc);
			} else if (jobDList1.size() == 9) {
				job.setTitle(jobDList1.get(2).getText().trim());
				job.setReferenceId(jobDList1.get(3).getText().trim());
				job.setSpec(jobDList1.get(4).getText().trim());
				job.setLocation(jobDList1.get(5).getText().split("Country")[1].trim());
				String loc=jobDList1.get(5).getText().split("Country")[1].trim();
				if(loc.contains("Anywhere in the")) job.setLocation(loc.split("Anywhere in the")[1].trim());
				else job.setLocation(loc);
			}
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