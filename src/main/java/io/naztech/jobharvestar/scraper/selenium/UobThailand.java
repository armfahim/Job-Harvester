package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import lombok.extern.slf4j.Slf4j;

/**
 * UOB Thailand job site parser. <br>
 * URL: https://www.uob.co.th/en/career/listing.html
 * 
 * @author tohedul.islum
 * @since 2019-02-13
 * 
 * @author tanmoy.tushar
 * @since 2019-04-30
 */
@Service
@Slf4j
public class UobThailand extends AbstractSeleniumJobList {
	private static final String SITE = ShortName.UNITED_OVERSEAS_BANKING_GROUP_THAILAND;
	
	@Override
	protected Job getJobDetail(Job job) {
		try {
			driver.get(job.getUrl());
			WebDriverWait wait = new WebDriverWait(driver, 15);
			WebElement titleE = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='title']")));
			job.setTitle(titleE.getText());
			job.setName(titleE.getText());
			job.setSpec(driver.findElements(By.xpath("//div[@class='detail']")).get(0).getText());
			job.setPrerequisite(driver.findElements(By.xpath("//div[@class='detail']")).get(1).getText());
			job.setApplicationUrl(
					driver.findElements(By.xpath("//a[@class='button Navyblue']")).get(0).getAttribute("href"));
		} catch (StaleElementReferenceException e) {
			log.warn(SITE + " failed to parse job", e);
		}
		return job;
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
		return "//ul[@class='dropdown-menu']/li/a";
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
	protected String getTitleXPath() {
		return null;
	}

	@Override
	protected String getLocationXPath() {
		return null;
	}

	@Override
	protected String getCategoryXPath() {
		return null;
	}

	@Override
	protected String getJobTypeXPath() {
		return null;
	}

	@Override
	protected String getRefXPath() {
		return null;
	}

	@Override
	protected String getSpecXPath() {
		return null;
	}

	@Override
	protected String getPreReqXPath() {
		return null;
	}

	@Override
	protected String getPostedDateXPath() {
		return null;
	}

	@Override
	protected String getApplyUrlXPath() {
		return null;
	}

	@Override
	protected DateTimeFormatter[] getDateFormats() {
		return null;
	}
}
