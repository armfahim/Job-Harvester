package io.naztech.jobharvestar.scraper;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestHuimin extends TestAbstractScrapper{
	
	private static final String HEAD_URL="http://www.huimin.cn/job/index/p/";
	private static final String TAIL_URL=".html";
	private static WebDriver driver;
	private static WebDriverWait wait;
	private static int pageCounter=1;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver=getChromeDriver();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
	}

	@Test
	public void testGetJobList() throws InterruptedException {
		getJob(jobSearch());
	}
	
	private void nextPage() throws InterruptedException {
		List<WebElement> next=wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='fpage']/a")));
		if(next.get(next.size()-1).getText().trim().equals(">")) {
			pageCounter++;
			getJob(jobSearch());
		} else driver.close();
	}
	
	private void getJob(List<WebElement> jobList) throws InterruptedException {
		for (WebElement webElement : jobList) {
			System.out.println("TITLE: "+webElement.findElement(By.cssSelector(".name")).getText().trim());
			System.out.println("CATEGORY: "+webElement.findElement(By.cssSelector(".add")).getText().trim());
			System.out.println("DATE: "+webElement.findElement(By.cssSelector(".time")).getText().trim());
			webElement.click();
			Thread.sleep(TIME_4S);
			System.out.println("DESC: "+webElement.findElement(By.cssSelector(".employ-info")).getText().trim());
			//webElement.clear();
			//Thread.sleep(1500);
			System.out.println("============================================================================");
		}
		nextPage();
	}
	
	private List<WebElement> jobSearch() {
		driver.get(HEAD_URL+pageCounter+TAIL_URL);
		wait = new WebDriverWait(driver, TIME_1M);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollBy(0,1000)");
		List<WebElement> jobList=wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//li[@class='str']")));
		return jobList;
	}

}
