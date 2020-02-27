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
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import io.naztech.talent.model.Job;
import lombok.extern.slf4j.Slf4j;

/**
 * LinkedIn test case
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-07
 */
@Slf4j
public class TestLinkedInWithSelenium {
	private static final String DETAIL_PAGE_URL = "https://www.linkedin.com/jobs/search/?f_C=265778&locationId=OTHERS.worldwide&pageNum=0&position=1";

	private static ChromeDriverService service;
	private ChromeDriver driver;
	private WebDriverWait wait;

	@BeforeClass
	public static void beforeClass() {
		service = new ChromeDriverService.Builder().usingDriverExecutable(new File("webdrivers/chromedriver.exe"))
				.usingAnyFreePort().build();
	}

	@Before
	public void beforeTest() {
		driver = new ChromeDriver(service, new ChromeOptions().setHeadless(false));
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
	public void testPage() throws InterruptedException {
		driver.get(DETAIL_PAGE_URL);
		List<WebElement> list = wait.until(ExpectedConditions
				.presenceOfAllElementsLocatedBy(By.xpath("//ul[@class = 'pagination__pages']/li//a")));
		List<String> pageUrls = new ArrayList<>();
		for (WebElement row : list) {
			pageUrls.add(row.getAttribute("href"));
		}

		for (String url : pageUrls) {
			getSummaryPage(url);
		}
	}

	private void getSummaryPage(String url) throws InterruptedException {
		driver.get(url);
		try {
			List<WebElement> jobAnchorlist = wait.until(ExpectedConditions
					.presenceOfAllElementsLocatedBy(By.xpath("//ul[@class = 'jobs-search-content__results']/li//a")));
			List<String> jobUrlList = new ArrayList<>();
			for (WebElement row : jobAnchorlist) {
				jobUrlList.add(row.getAttribute("href"));
			}
			for (String jobUrl : jobUrlList) {
				getJobDetails(jobUrl);
			}
		} catch (FailingHttpStatusCodeException e) {
			log.warn("Failed to parse on summary page " + e);
		}

	}

	public void getJobDetails(String jobUrl) throws InterruptedException {
		//driver.manage().deleteAllCookies();
		driver.get(jobUrl);
		Thread.sleep(1000);
		if(driver.getTitle().contains("LinkedIn: Log In or Sign Up")) {
			driver.findElementByXPath("//p[@class='form-subtext login']//a").click();
			Thread.sleep(2000);
			driver.findElementById("login-email").sendKeys("tanu.tanbirul@hotmail.com");
			driver.findElementById("login-password").sendKeys("nazTech123Test");
			driver.findElementById("login-submit").click();
		}
		List<WebElement> elList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//p[@class='jobs-box__body js-formatted-employment-status-body']")));
		Job job=new Job(jobUrl);
		try {
			job.setTitle(driver.findElementByTagName("h1").getText().trim());
			job.setName(job.getTitle());
			try {
				job.setLocation(driver.findElementByXPath("span[@class='jobs-top-card__bullet']").getText().trim());
				job.setType(elList.get(1).getText().trim());
				job.setCategory(elList.get(2).getText().trim());	
			}catch (NoSuchElementException e) {
				/*Intentionally Left Blank*/
			}
			job.setSpec(driver.findElementByXPath("//div[@class='jobs-description__content jobs-description__content--condensed jobs-description-content']").getText());
			
		}
		catch (NoSuchElementException e) {
			log.warn("Failed to parse job details of "+job.getUrl() ,e);
			
		}

	}
}
