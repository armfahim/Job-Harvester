package io.naztech.jobharvestar.scraper;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.naztech.talent.model.Job;

/**
 * @name Microsoft
 * @author Tanbirul Hashan
 * @since 2019-03-06
 */
public class TestMicrosoftWithSelenium {
	private static final String PAGE_URL = "https://careers.microsoft.com/us/en/search-results?from=340&s=1&rt=university";
	private static DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private static DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMM d, yyyy");
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
		driver = new ChromeDriver(service, new ChromeOptions().setHeadless(true));
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 40);
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
	public void testJobTotalJob() throws InterruptedException {
		driver.get(PAGE_URL);
		List<Job> jobList = new ArrayList<>();
		while (true) {
			List<WebElement> jobElList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='information']/a")));
			for (WebElement webElement : jobElList) {
				Job job = new Job(webElement.getAttribute("href"));
				job.setTitle(webElement.getText());
				//System.out.println(job.getTitle());
				job.setName(job.getTitle());
				jobList.add(job);
			}
			try {
				browseNextpage();
				System.out.println("nextPage browsed");
			} catch (NoSuchElementException | ElementNotVisibleException e) {
				System.out.println("pageEnded"+e);
				break;
			}
		}
//		for (Job job : jobList) {
//			System.out.println(getJobDetails(job).toString());
//		}
	}
	
	/**
	 * browseNextPage method used to browse next page <br>
	 * Whenever "aria-label" anchor will not visible or found it throws {@link ElementNotVisibleException} or {@link NoSuchElementException}
	 * 
	 * @throws NoSuchElementException
	 * @throws ElementNotVisibleException
	 * @throws InterruptedException
	 */
	private void browseNextpage() throws NoSuchElementException,ElementNotVisibleException, InterruptedException {
		WebElement nextAnchor = driver.findElementByCssSelector("a[aria-label='View Next page']");
		nextAnchor.click();
		Thread.sleep(2000);
	}

	private Job getJobDetails(Job job) {
		driver.get(job.getUrl());
		List<WebElement> jobElList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[@class='lable-text']")));
		System.out.println(jobElList.size());
		job.setReferenceId(jobElList.get(0).getText().trim());
		String date = jobElList.get(1).getText().trim();
//		job.setPostedDate(LocalDate.parse(date, DF1));
//		job.setCategory(jobElList.get(3).getText().trim());
		job.setType(jobElList.get(5).getText().trim());
		try{
			job.setSpec(driver.findElementByCssSelector("p[data-ph-at-id='job-responsibilities-text']").getText().trim() );
			job.setPrerequisite(driver.findElementByCssSelector("p[data-ph-at-id='job-qualifications-text']").getText().trim() );
		}catch (NoSuchElementException e1) {
			job.setSpec(driver.findElementByCssSelector("section[class='job-description']").getText().trim());
			
		}

		return job;

	}

}
