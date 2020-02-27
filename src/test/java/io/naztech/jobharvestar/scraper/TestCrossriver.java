package io.naztech.jobharvestar.scraper;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Crossriver job parsing class<br>
 * URL:"https://workforcenow.adp.com/mascsr/default/mdf/recruitment/recruitment.html?cid=c4c744b8-3a1d-428d-8cc5-f90ccbe8d519&ccId=19000101_000001&type=MP&lang=en_US"
 * 
 * @author Muhammad Bin Farook
 * @since: 2019-03-27
 */
public class TestCrossriver extends TestAbstractScrapper {
	private static final String SITE = "https://workforcenow.adp.com/mascsr/default/mdf/recruitment/recruitment.html?cid=c4c744b8-3a1d-428d-8cc5-f90ccbe8d519&ccId=19000101_000001&type=MP&lang=en_US";
	private static ChromeDriver driver;
	private static WebDriverWait wait;

	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMMM d, yyyy");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 50);
	}

	@Test
	public void testGetJobList() throws InterruptedException {

		driver.get(SITE);
		wait = new WebDriverWait(driver, 50);
		Thread.sleep(TIME_5S);
		List<WebElement> jobLinks = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
				By.xpath("//div[@id='tileJobs']//div[@class='current-openings-item']"), 0));
		List<WebElement> el = driver.findElements(By.xpath("//div[@id='tileJobs']/div[@class='vdl-tile__wrapper']/*"));
		String str = el.get(0).getText();
		System.out.println(str);
		String[] str1 = str.split("\\(");
		String[] str2 = str1[str1.length - 1].split("\\)");
		int jobcount = Integer.parseInt(str2[0]);
		driver.findElement(By.xpath("//div[@id='tileJobs']//button[@id='btnShowAllJobs']")).click();

		Thread.sleep(TIME_5S);
		wait = new WebDriverWait(driver, 50);

		List<WebElement> jobLink = new ArrayList<WebElement>();

		while (true) {
			JavascriptExecutor js = ((JavascriptExecutor) driver);
			js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
			jobLink = driver
					.findElements(By.xpath("//div[@id='tileCurrentOpenings']//div[@class='current-openings-item']"));
			if (jobLink.size() == jobcount)
				break;

		}

		testGetJobDetails(jobLink);

	}

	public void testGetJobDetails(List<WebElement> list) throws InterruptedException {
		for (int i = 0; i < list.size(); i++) {

			list.get(i).click();
			Thread.sleep(TIME_5S);
			wait = new WebDriverWait(driver, 50);
			WebElement title = driver.findElement(
					By.xpath("//div[@class='job-description-details']//span[@class='job-description-title']"));
			System.out.println(title.getText());
			WebElement type = driver.findElement(By.xpath("//span[@class='job-description-worker-catergory']/span"));

			System.out.println(type.getText());
			WebElement location = driver.findElement(By.xpath("//span[@class='job-description-location']/div//span"));
			System.out.println(location.getText());

			WebElement description = driver.findElement(
					By.xpath("//div[@class='job-description-data-item']/div[@class='job-description-data']"));
			System.out.println(description.getText());

			driver.findElements(By.xpath("//div[@class='mdf-snackbar vdl-container job-description-snackbar']/*"))
					.get(0).click();
			Thread.sleep(TIME_5S);

			List<WebElement> bt = driver
					.findElements(By.xpath("//div[@class='careercenter-current-openings-view-all ']/*"));

			Thread.sleep(TIME_5S);
			wait = new WebDriverWait(driver, 50);

			List<WebElement> jobLink = new ArrayList<WebElement>();

			while (true) {
				JavascriptExecutor js = ((JavascriptExecutor) driver);
				js.executeScript("window.scrollTo(0, document.body.scrollHeight)");
				jobLink = driver.findElements(
						By.xpath("//div[@id='tileCurrentOpenings']//div[@class='current-openings-item']"));
				if (jobLink.size() == list.size())
					break;

			}

			list = jobLink;

		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
	}
}
