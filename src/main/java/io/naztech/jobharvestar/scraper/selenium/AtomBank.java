package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;

/**
 * Atom Bank job site parsing class. <br>
 * URL: https://www.atombank.co.uk/careers/#careers
 * 
 * @author tanmoy.tushar
 * @since 2019-03-27
 */
@Service
public class AtomBank extends AbstractSeleniumJobList {
	private static final String SITE = ShortName.ATOM_BANK;

	@Override
	protected Job getJobDetail(Job job) {
		try {
			driver.get(job.getUrl());
			Thread.sleep(TIME_5S);
			WebElement jobE = driver.findElementByXPath("//div[@class = 'hero__Basic-sc-1kifi8c-1 ckaevS']/h1");
			job.setTitle(jobE.getText().trim());
			job.setName(job.getTitle());
			try {
				jobE = driver.findElement(By.xpath("//div[@class='container__Container-qw5jjp-0 bOeind']"));
				job.setSpec(jobE.getText());
			} catch (NoSuchElementException e) {
				jobE = driver.findElement(By.xpath("//div[@class='sc-hkbPbT RoWIn']"));
				job.setSpec(jobE.getText());
			}
			jobE = driver.findElement(By.xpath("//span[@class='job-email__Email-sgh5ec-1 dWveIY']"));
			job.setApplyEmail(jobE.getText().trim());

			try {
				jobE = driver.findElementByXPath("//div[@class = 'items__Items-sc-12fyeio-0 jwYxAn']/div[1]/span");
				job.setLocation(jobE.getText().trim());
			} catch (NoSuchElementException e) {// It should blank to continue process
			}
			try {
				jobE = driver.findElementByXPath("//div[@class = 'items__Items-sc-12fyeio-0 jwYxAn']/div[3]/span");
				job.setType(jobE.getText().trim());
			} catch (NoSuchElementException e) {// It should blank to continue process
				jobE = driver.findElementByXPath("//div[@class = 'items__Items-sc-12fyeio-0 jwYxAn']/div[2]/span");
				job.setType(jobE.getText().trim());
			}
			return job;
		} catch (Exception e) {
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
		return "//div[@class='job-preview__JobPreviewWrapper-sc-1jnfqc6-0 kAeWEt']/a";
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
	protected DateTimeFormatter[] getDateFormats() {
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
}