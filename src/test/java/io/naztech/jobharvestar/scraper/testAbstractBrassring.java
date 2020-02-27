package io.naztech.jobharvestar.scraper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class testAbstractBrassring extends TestAbstractScrapper {
	public static String URL = "https://sjobs.brassring.com/TGnewUI/Search/Home/Home?partnerid=25713&siteid=5365#home";
	public static ChromeDriver driver; 
	public static WebDriverWait wait;
	
	private static final String JOB_ROW_LIST_PATH = "//div[@class='liner lightBorder']";
	private static final String MORE_JOBS_ID = "showMoreJobs";
	private static final String JOB_SEARCH_BTN = "//div[@class='searchControls']/button";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 50);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.quit();
	}
	
	@Test
	public void test() throws InterruptedException {
		driver.get(URL);
		WebElement search = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(JOB_SEARCH_BTN)));
		search.click();
		Thread.sleep(TIME_10S * 5);
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(JOB_ROW_LIST_PATH)));
		System.out.println("First Page Job Count: "+rowList.size());
		double totalJob = getTotalJob();
		double totalPage = totalJob/50;
		double totalP = Math.ceil(totalPage);
		System.out.println(totalPage);
		System.out.println("Total Job: "+totalJob);
		for(int j = 0; j < totalP; j++) {
			if(j == totalP - 1 ) break;
			WebElement moreJobs = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(MORE_JOBS_ID)));
			moreJobs.click();
			Thread.sleep(RandomUtils.nextInt(TIME_1S, TIME_5S));
			rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(JOB_ROW_LIST_PATH)));
			System.out.println("Page has: "+rowList.size()+" jobs");
		}
		
		System.out.println("Total Job Link Found: "+rowList.size());
		for(int i = 0; i <rowList.size(); i++) {
			WebElement jobUrl = driver.findElement(By.id("Job_"+i));
			System.out.println(jobUrl.getAttribute("href"));
		}
	}
	
	public int getTotalJob() {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='sectionHeading']/h2")));
		System.out.println("Total Job Text: "+el.getText());
		return Integer.parseInt(el.getText().trim().split(" ")[0]);
	}
}
