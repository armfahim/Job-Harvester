package io.naztech.jobharvestar.scraper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * ThumbTack job parsing class<br>
 * URL:https://careers.toasttab.com/
 * 
 * @author Muhammad Bin Farook
 * @since: 2019-03-14
 */

public class TestToast extends TestAbstractScrapper {
	private static final String SITE = "https://careers.toasttab.com/";
	private static ChromeDriver driver;
	private static WebDriverWait wait;

	@BeforeClass
	public static void beforeClass() {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 50);
	}

	@Test
	public void testJobTotalJob() throws InterruptedException, IllegalMonitorStateException, TimeoutException {
		driver.get(SITE);
		Thread.sleep(TIME_4S);

		List<WebElement> joblist = driver.findElements(By.xpath("//div[@id='nwh-positions']/a"));
		System.out.println(joblist.size());

		List<String> div = new ArrayList<>();
		for (WebElement el : joblist) {

			div.add(el.getAttribute("href").toString());

		}

		for (String el : div) {

			testJobDetailPage(el);

		}

	}

	public void testJobDetailPage(String url) {

		driver.get(url);
		wait = new WebDriverWait(driver, 30);
		driver.manage().window().maximize();
		driver.switchTo().frame("grnhse_iframe");
		System.out.println(url);
		System.out.println(
				"JOB TITLE: " + driver.findElement(By.xpath("//div[@id='header']/h1[@class='app-title']")).getText());
		
		System.out.println("JOB DESCRIPION: " + driver.findElement(By.xpath("//div[@id='content']")).getText());

		System.out.println("JOB LOCATION: "
				+ driver.findElement(By.xpath("//div[@id='header']/div[@class='location']")).getText() + "\n");
		System.out.println();

	}

}
