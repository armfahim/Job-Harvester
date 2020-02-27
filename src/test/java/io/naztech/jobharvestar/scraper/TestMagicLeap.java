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
 * MagicLeaps job parsing class<br>
 * URL:https://www.magicleap.com/careers
 * 
 * @author Muhammad Bin Farook
 * @since: 2019-03-20
 */

public class TestMagicLeap extends TestAbstractScrapper {
	private static final String SITE = "https://www.magicleap.com/careers";
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
		List<WebElement> cat = driver.findElementsByXPath("//div[@class='Kit-qft5of-0 gZMMAz']/div/*");
		List<String> catm = new ArrayList<String>();
		List<List<WebElement>> links = new ArrayList<List<WebElement>>();
		List<List<String>> joblink = new ArrayList<List<String>>();
		for (int i = 1; i < cat.size(); i++) {
			catm.add(cat.get(i).findElement(By.tagName("h2")).getText());

		}
		int j = 0;
		for (int i = 1; i < cat.size(); i++) {
			links.add(cat.get(i).findElements(By.tagName("a")));
			System.out.println(links.get(j++).size());

		}
		for (List<WebElement> el : links) {
			List<String> li = new ArrayList<String>();
			for (WebElement ell : el) {
				li.add(ell.getAttribute("href").toString());
			}
			joblink.add(li);

		}
		System.out.println(joblink.size());

		for (int i = 0; i < catm.size(); i++) {
			testJobDetailPage(joblink.get(i), catm.get(i));
		}

	}

	public void testJobDetailPage(List<String> joblink, String cate) {
		for (String el : joblink) {
			driver.get(el);
			wait = new WebDriverWait(driver, 30);
			System.out.println("JOB TITLE: "
					+ driver.findElement(By.xpath("//div[@id='header']/h1[@class='app-title']")).getText() + "\n");
			System.out.println("JOB CATAGORY: " + cate + "\n");

			System.out.println("JOB LOCATION: "
					+ driver.findElement(By.xpath("//div[@id='header']/div[@class='location']")).getText() + "\n");
			System.out.println("JOB APPLICATION URL: "
					+ driver.findElement(By.xpath("//div[@id='header']/a")).getAttribute("href") + "\n");

			System.out.println(
					"JOB SPECIFICATION: \n" + driver.findElement(By.xpath("//div[@id='content']")).getText() + "\n");

			System.out.println();
			System.out.println(
					"--------------------------------------------------------------------------------------------------------");
			System.out.println(
					"--------------------------------------------------------------------------------------------------------");

		}
	}
}
