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
 * Pnc Finl Services Group jobs site parse. <br>
 * URL: https://sjobs.brassring.com/TGnewUI/Search/Home/Home?partnerid=15783&siteid=5130#keyWordSearch=&locationSearch=
 * 
 * @author tanmoy.tushar
 * @since 2019-02-26
 */
@Service
public class PncFinlServicesGroup extends AbstractBrassring {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String SITE = ShortName.PNC_FINL_SERVICES_GROUP;
	private static final String JOB_DESC_PATH = "//div[@class='questionClass']/div";

	@Override
	protected Job getJobDetails(String jobUrl, ChromeDriver driver, WebDriverWait wait) {
		try {
			driver.get(jobUrl);
			Job job = new Job(jobUrl);
			List<WebElement> jobInfoL = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(JOB_DESC_PATH)));
			if(jobInfoL.size() > 4) {
				job.setReferenceId(jobInfoL.get(0).getText().trim());
				job.setTitle(jobInfoL.get(1).getText().trim());
				job.setName(job.getTitle());
				job.setCategory(jobInfoL.get(2).getText().trim());
				String[] parts = jobInfoL.get(4).getText().trim().split(" ");
				if (parts.length > 2) {
					job.setType(jobInfoL.get(3).getText().trim());
				} else {
					job.setLocation(jobInfoL.get(3).getText().trim());
					job.setType(jobInfoL.get(4).getText().trim());
				}
			}			
			WebElement jobSpec = driver.findElement(By.xpath("//div[@class='questionClass']"));
			job.setSpec(jobSpec.getText().trim());
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
