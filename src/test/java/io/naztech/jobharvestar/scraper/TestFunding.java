package io.naztech.jobharvestar.scraper;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.naztech.jobharvestar.scraper.TestAbstractScrapper;

/**
 * fundingsocieties job parsing class<br>
 * URL: https://fundingsocieties.com/career
 * 
 * @author Muhammad Bin Farook
 * @since: 2019-03-27
 */


public class TestFunding extends TestAbstractScrapper {
	private static final String SITE = "https://fundingsocieties.com/career";

	private static ChromeDriver driver;
	private static WebDriverWait wait;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 50);
		
	}

	@Test
	public void testGetJobList() {

		driver.get(SITE);
		wait = new WebDriverWait(driver, 50);
		
		
		List<WebElement> el = wait.until(ExpectedConditions
				.numberOfElementsToBeMoreThan(By.xpath("//div[@class='row vacancyContainer margin-top-20']/*"), 0));

		
		int size=el.size();

		for (int i = 0; i < el.size(); i++) {

			if (el.get(i).isDisplayed() && el.get(i).isEnabled()) {
				el.get(i).click();
			}
			
			
			wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//div[@class='container-fluid careerContainer detail']/div[@class='row']"), 0));
			List<WebElement> job = driver.findElements(By.xpath("//div[@class='container-fluid careerContainer detail']/*"));
			List<WebElement> di=job.get(1).findElements(By.xpath("./div"));
			System.out.println(di.size());
			
			for(WebElement spec:di) 
				spec.click();
			
			for(WebElement spec:di) {
				
				WebElement title=spec.findElement(By.xpath("./label[@class='collapsed active']/div[@class='pull-left font-size-16']"));
				System.out.println(title.getText());
				WebElement location=spec.findElement(By.xpath("./label[@class='collapsed active']/div[@class='col-xs-12 col-sm-1 text-right pull-right margin-right-20 font-size-14']"));
				System.out.println(location.getText());
				List<WebElement> description=spec.findElements(By.xpath("./div[@class='well-lg']/div[@class='margin-bottom-20']/*"));
				System.out.println(description.size());
				String str="";
				for(WebElement ele:description) {
					str+=ele.getText();
					
					
				}
				System.out.println("DESCRIPTION: \n"+str);
				
			}
			
			driver.quit();
			driver = getChromeDriver();
			driver.get(SITE);
			wait = new WebDriverWait(driver, 50);
			List<WebElement> el2 = wait.until(ExpectedConditions
					.numberOfElementsToBeMoreThan(By.xpath("//div[@class='row vacancyContainer margin-top-20']/*"), size-1));
			el=el2;
		}

	}

	

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		driver.close();
	}
}