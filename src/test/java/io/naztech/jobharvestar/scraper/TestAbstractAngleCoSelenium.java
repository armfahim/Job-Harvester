package io.naztech.jobharvestar.scraper;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class TestAbstractAngleCoSelenium extends TestAbstractScrapper {

	private ChromeDriver driver;
	private WebDriverWait wait;

	@Before
	public void beforeTest() {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 15);
	}
	
	@Test
	public void testFirstPage() {
		List<String> jobL = new ArrayList<>();
		driver.get("https://angel.co/company/alan-25/jobs");
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='listing-title s-grid-colSm18']/a")));
		System.out.println(jobList.size());
		for(WebElement job : jobList) {
			System.out.println(job.getAttribute("href"));
			jobL.add(job.getAttribute("href"));
		}
		System.out.println(jobL.size());
		for (String url : jobL) {
			System.out.println(url);
		}
		
		//For second pattern
		//jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
	   //By.xpath("//div[@class='component_ee038 component_e6bd3 expanded_80d76']/a")));
	}
	
	@Test
	public void testDetailPage() {
		driver.get("https://angel.co/company/alan-25/jobs/494491-user-care-expert-includes-working-on-saturday");
		WebElement el = driver.findElementByXPath("//h1[@class='u-colorGray3']");
		System.out.println(el.getText().trim());
		
		el = driver.findElementByXPath("//div[@class='company-summary s-grid-colSm24']/div");
		System.out.println(el.getText().split("·")[0].trim());
		System.out.println(el.getText().split("·")[1].trim());
		
		//el= driver.findElementByXPath("//div[@class='job-description u-fontSize14 u-colorGray6']");
		el = driver.findElementByXPath("//div[@class='listing showcase-section u-bgWhite']");
		System.out.println(el.getText().trim());
	}
	
	@Test
	public void testDetailPageSecondPattern() {
		driver.get("https://angel.co/company/alan-25/jobs/494491-user-care-expert-includes-working-on-saturday");
		WebElement el = driver.findElementByXPath("//h2[@class='__halo_textContrast_dark_AAAA __halo_fontSizeMap_size--2xl __halo_fontWeight_medium styles_component__1kg4S header_a3128']");
		System.out.println(el.getText().trim());
		
		List<WebElement> jobE = driver.findElements(By.xpath("//div[@class='characteristic_650ae']"));
		System.out.println(jobE.get(0).getText().split("Loaction")[1].trim());
		System.out.println(jobE.get(1).getText().split("Job type")[1].trim());
	
		
		el= driver.findElementByXPath("//div[@class='description_533b6']");
		System.out.println(el.getText().trim());
	}
}
