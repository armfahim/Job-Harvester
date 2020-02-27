package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.naztech.talent.model.Job;


public class TestZuoyeSelenium extends TestAbstractScrapper {

	String jobUrl = "https://app.mokahr.com/apply/17zuoye/524#/jobs/?keyword=&_k=roz9fw";

	String baseUrl = "https://app.mokahr.com";
	String tailUrl;
	private static WebDriver driver;
	private static WebDriverWait wait;

	//private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 10);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
	}

	@Test
	public void testJobSummaryPage() throws IOException, InterruptedException {
		try {

			Job job = new Job();

			driver.get(jobUrl);
			Thread.sleep(TIME_1S);

			List<WebElement> jobListE = driver.findElements(By.xpath("//div[@class='jobs-2J09M']/a"));
			// System.out.println(jobListE.size());
			for (int i = 0; i < jobListE.size(); i++) {

				WebElement title = (WebElement) jobListE.get(i).findElements(By.xpath("//div[@class='title-1X3Vf']"))
						.get(i);
				//System.out.println(title.getText());
				WebElement location = (WebElement) jobListE.get(i)
						.findElements(By.xpath("//div[@class='status-3wqaa']/span[3]")).get(i);
				// System.out.println(location.getText());

				WebElement catagory = (WebElement) jobListE.get(i)
						.findElements(By.xpath("//div[@class='status-3wqaa']/span[2]")).get(i);
				// System.out.println(catagory.getText());

				WebElement postDate = (WebElement) jobListE.get(i).findElements(By.xpath("//span[@class='opened-at-3hbqT']")).get(i);
				System.out.println(postDate.getText().split("：")[1]);
				//System.out.println(postDate.getText());
				WebElement jobLink = (WebElement) jobListE.get(i).findElements(By.xpath("//a[@class='link-11ZhH']"))
						.get(i);
				//System.out.println(jobLink.getAttribute("href"));

				job.setTitle(title.getText());
				job.setName(job.getTitle());
				job.setLocation(location.getText());
				job.setCategory(catagory.getText());
				// job.setApplicationUrl(baseUrl + applicationUrl.getAttribute("href"));
				job.setUrl(jobLink.getAttribute("href"));
				// job.setPostedDate(postDate.getText().split("：")[1],DF);

			}

		} catch (TimeoutException e) {

		}

	}

	@Test
	public void testNextPage() throws InterruptedException {
		driver.get(jobUrl);
		WebElement nextBtn;
		while (true) {
			// getSummaryPage
			
			nextBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@class=' rc-pagination-next']")));
			nextBtn.click();
			try {
				WebElement btnLastE = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//li[@class='rc-pagination-disabled rc-pagination-next']")));
				if (btnLastE != null)
					System.out.println("Break Success");
				break;
			} catch (NoSuchElementException e) {
				continue;
			}
		}

	}

	@Test
	public void testJobDetailPage() throws IOException {

		String jobLink = "https://app.mokahr.com/apply/17zuoye/524#/job/be559ac9-1890-43af-8c88-8ba63e827481";

		// list-k05pg

		try {

			//Job job = new Job();

			driver.get(jobLink);
			Thread.sleep(TIME_1S);

			// List<WebElement> jobListE =
			// driver.findElements(By.xpath("//div[@class='jobs-2J09M']/a"));
			// System.out.println(jobListE.size());

			WebElement description = (WebElement) driver.findElements(By.xpath("//div[@class='list-k05pg']")).get(0);
			System.out.println(description.getText());

		} catch (TimeoutException | InterruptedException e) {

		}

	}

}
