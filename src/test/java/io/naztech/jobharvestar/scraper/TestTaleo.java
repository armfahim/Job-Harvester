package io.naztech.jobharvestar.scraper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.naztech.talent.model.Job;

public class TestTaleo {
	private static final String PAGE_URL = "https://xl.taleo.net/careersection/001xlcatlinexternalcareersection/jobsearch.ftl?lang=en";
	private static ChromeDriverService service;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int jobCount = 0;

	@BeforeClass
	public static void beforeClass() {
		service = new ChromeDriverService.Builder().usingDriverExecutable(new File("webdrivers/chromedriver.exe"))
				.usingAnyFreePort().build();
	}

	@Before
	public void beforeTest() {
		driver = new ChromeDriver(service, new ChromeOptions().setHeadless(true));
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 60);
	}

	@AfterClass
	public static void afterClass() {
		service.stop();
	}

	@After
	public void afterTest() {
		driver.quit();
	}

	@Test
	public void getScrapedJobs() throws InterruptedException {
		//this.BASE_URL = PAGE_URL.substring(0, 20);
		driver.get(PAGE_URL);
		wait = new WebDriverWait(driver, 30);
		List<Job> jobList = new ArrayList<>();
		while (true) {
		wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='multiline-data-container']//a")));
		List<WebElement> jobElements = driver.findElementsByXPath("//div[@class='multiline-data-container']//a");
		for (WebElement webElement : jobElements) {
			Job job = new Job(webElement.getAttribute("href"));
			System.out.println("Job " + ++jobCount + ": " + webElement.getText());
			job.setTitle(webElement.getText());
			job.setName(job.getTitle());
			jobList.add(job);
		}
			try {
				WebElement nextAnchor = driver.findElementByCssSelector("a[id='next'][aria-disabled='false']");
				nextAnchor.click();
				Thread.sleep(2000);
			} catch (NoSuchElementException e) {
				break;
			}
		}
		jobCount = 0;
		for (Job job : jobList) {
			show(getJobDetails(job));
		}
	}

	private Job getJobDetails(Job job) throws InterruptedException {
		driver.get(job.getUrl());
		try {
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
					By.xpath("//span[@id='requisitionDescriptionInterface.reqContestNumberValue.row1']")));
			job.setReferenceId(
					driver.findElementByXPath("//span[@id='requisitionDescriptionInterface.reqContestNumberValue.row1']")
							.getText());
			
			job.setLocation(
					driver.findElementByXPath("//span[@id='requisitionDescriptionInterface.ID1727.row1']")
					.getText());
			job.setCategory(
					driver.findElementByXPath("//span[@id='requisitionDescriptionInterface.ID1779.row1']")
					.getText());
			job.setType(
					driver.findElementByXPath("//span[@id='requisitionDescriptionInterface.ID1831.row1']")
					.getText());
			job.setSpec(
					driver.findElementByXPath("//div[@id='requisitionDescriptionInterface.ID1556.row1']//ul")
					.getText());
			job.setPrerequisite(
					driver.findElementByXPath("//div[@id='requisitionDescriptionInterface.ID1612.row1']//ul")
					.getText());
			
			return job;
		} catch (NoSuchElementException |TimeoutException e) {
			System.out.println("page browsing finished"+ e);
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

}
