package io.naztech.jobharvestar.scraper;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * BioCatchy job site parser Test in Selenium.
 * https://www.biocatch.com/biometrics-cybersecurity-careers
 * 
 * @author jannatul.maowa
 * @since 2019-03-25
 */
public class TestBioCatchySelenium extends TestAbstractScrapper {
	private static ChromeDriver driver;
	private WebDriverWait wait;

	@Before
	public void setUp() throws Exception {
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 40);
	}

	@Test
	public void jobListTest() {
		String url = "https://www.biocatch.com/biometrics-cybersecurity-careers";
		driver.get(url);
		List<WebElement> jobList = wait.until(
				ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='link-to-posting']/a")));
		for (int i = 0; i < jobList.size(); i++) {
			System.out.println(jobList.get(i).getAttribute("href"));
		}
	}

	// HtmlUnit use kore korsi
	@Test
	public void jobDetailsTest() {
		String url = "https://www.comeet.co/jobs/biocatch/03.00E/dataops-engineer/E3.D09";
		driver.get(url);
		List<WebElement> jobList = wait.until(
				ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='link-to-posting']/a")));
		for (int i = 0; i < jobList.size(); i++) {
			System.out.println(jobList.get(i).getAttribute("href"));
		}
	}
}
