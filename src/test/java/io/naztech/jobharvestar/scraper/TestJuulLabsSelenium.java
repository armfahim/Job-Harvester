package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

/**
 * JUUL Labs test job site parser.<br>
 * URL: https://www.juul.com/join-us
 * 
 * @author jannatul.maowa
 * @since 2019-05-02
 */
public class TestJuulLabsSelenium  extends TestAbstractScrapper{

	private static WebDriver driver;
	private static WebDriverWait wait;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver=getChromeDriver();
		wait =new WebDriverWait(driver, 10);
	}

	@Test
	public void testgetScrapedJobs() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		driver.get("https://www.juul.com/join-us");
		List<WebElement> jobLinks = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='JobBoard__job-title']")));
		System.out.println(jobLinks.size());
		for(WebElement webElement : jobLinks) {
			System.out.println(webElement.getAttribute("href"));
		}
	}
}
