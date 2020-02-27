package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.utils.ConnectionProvider;

public class TestMarshallWace {
	private static final String SITE_URL = "https://www.mwam.com/roles";
	private static final String Details_URL="https://www.mwam.com/vacancies/29";
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
	public void testAllJobTitle() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException{
		driver.get(SITE_URL);
		Thread.sleep(5000);
		List<WebElement> titleList = driver.findElements(By.xpath("//div[@class='Vacancies__wrapper']/ul/li[@class='Vacancies__list__item']"));
		System.out.println(titleList.size());
		for(WebElement e : titleList)
		{
			System.out.println(e.getText());
		}
	}
		@Test
		public void testAllJobUrl() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException{
			driver.get(Details_URL);
			Thread.sleep(5000);
			List<WebElement> s = driver.findElements(By.xpath("//div[@class='Columns__buttons']/a[1]"));
			System.out.println(s.size());
			System.out.println(s.get(0).getAttribute("href"));
			//wait.until(ExpectedConditions.elementToBeClickable(By.xpath(aboutButtonXpath)));
	}
		public void testDetails() throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException{
			driver.get(SITE_URL);
			Thread.sleep(5000);
			List<WebElement> linkList = driver.findElements(By.xpath("//div[@class='Vacancies__wrapper']/ul/li[@class='Vacancies__list__item']"));
			for(int i = 0; i<linkList.size();i++)
			{
				String link =linkList.get(i).findElements(By.tagName("a")).get(0).getAttribute("href");
				System.out.println(link);	
//				driver.get(link);
				
			}
		}

}
