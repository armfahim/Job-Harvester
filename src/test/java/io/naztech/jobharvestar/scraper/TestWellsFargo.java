package io.naztech.jobharvestar.scraper;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.utils.URIBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * @author Tanbirul Hashan
 * @since 2019-02-13
 */
public class TestWellsFargo {
	private static final String PAGE_URL = "https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=1235&trk=companyTopCard_top-card-button&pageNum=0&position=12&location=Worldwide&currentJobId=1163604253";
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
		driver = new ChromeDriver(service, new ChromeOptions().setHeadless(false));
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
	public void testNextpages() throws InterruptedException {
		driver.get(PAGE_URL);
		Thread.sleep(2 * 1000);
		//List<String> pageUrlList = getPageUrlList();
	}

	@Test
	public void getPageUrlList() throws MalformedURLException, InterruptedException, URISyntaxException {
		//https://www.linkedin.com/jobs/search?location=Worldwide&f_C=1235&currentJobId=1163604253&pageNum=0&locationId=OTHERS.worldwide&position=12&trk=jobs_jserp_pagination_4&start=75&count=25
		driver.get(PAGE_URL);
		Thread.sleep(5 * 1000);
		List<WebElement> elList = driver.findElementsByXPath("//ul[@class = 'pagination__pages']/li//a");
		int limit = Integer.parseInt(elList.get(elList.size() - 1).getText().trim());
		int start = 25;
		for (int i = 1; i < limit; i++, start += 25) {
			URIBuilder b = new URIBuilder(elList.get(0).getAttribute("href").substring(0, 36));
			b.addParameter("location", "Worldwide");
			b.addParameter("f_C", "1235");
			b.addParameter("currentJobId", "1163604253");
			b.addParameter("pageNum", String.valueOf(i));
			b.addParameter("locationId", "OTHERS.worldwide");
			b.addParameter("position", "12");
			b.addParameter("trk", "jobs_jserp_pagination_" + String.valueOf(i + 1));
			b.addParameter("start", String.valueOf(start));
			b.addParameter("count", "25");
			System.out.println(b.build().toURL().toString());
		}
	}

}
