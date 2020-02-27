package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

/**
 * 
 * @author tanmoy.tushar
 * @since May 6, 2019
 */
public class TestUberSelenium extends TestAbstractScrapper {
	private static String URL = "https://www.uber.com/us/en/careers/list/";
	private static String ADD_URL = "/global/en";

	private static WebDriver driver;
	private static WebDriverWait wait;
	private String baseUrl;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 20);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.quit();
	}

	@Test
	public void testGetList()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		driver.get(URL);
		List<WebElement> jobList = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//span[@class='ot']/a"), 0));
		int totalJob = getTotalJob();
		System.out.println(totalJob);
		int j = 0;
		for(int i = 0; i < jobList.size(); i++) {
			if(i == totalJob - 1) break;
			browseJobList(j, jobList);
			testGetShowMoreButton().click();
			Thread.sleep(RandomUtils.nextInt(TIME_1S*2, TIME_5S));
			j = j+10;
			jobList = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//span[@class='ot']/a"), 0));
		}					
	}
	
	public void browseJobList(int i, List<WebElement> jobList) {
		System.out.println(jobList.size());
		for(int j = i; j < jobList.size(); j++) {
			System.out.println(jobList.get(j).getText());
			System.out.println(jobList.get(j).getAttribute("href"));
		}
	}
	
	public WebElement testGetShowMoreButton() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		WebElement showMoreBtn = driver.findElement(By.xpath("//button[@class='ft nu qa lv qb be gw qc h2 ag fz qd']"));
		return showMoreBtn;	
	}
	
	public int getTotalJob() {
		WebElement totalJob = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='oo op oq']/div/p")));
		return Integer.parseInt(totalJob.getText().split(" ")[0].trim());
	}
	
	@Test
	public void testDetailsPage() throws IOException {
		String url = "https://www.uber.com/global/en/careers/list/50624/";
		Document doc = Jsoup.connect(url).get();
		Element jobE = doc.selectFirst("h1");
		System.out.println(jobE.text());
		jobE = doc.selectFirst("div[class=o5 be nn gw o9]");
		String[] parts = jobE.text().split(" in ");
		System.out.println(parts[0].trim());
		System.out.println(parts[1].trim());
		jobE = doc.selectFirst("div[class=bq bz c0]>a");
		System.out.println(getBaseUrl() + jobE.attr("href"));
		jobE = doc.selectFirst("div[class=o5 nn]");
		System.out.println(jobE.text());		
	}
	
	public String getBaseUrl() {
		return URL.substring(0, 20);
	}
}