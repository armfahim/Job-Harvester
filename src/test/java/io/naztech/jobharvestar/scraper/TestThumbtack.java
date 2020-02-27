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
 * URL:https://www.thumbtack.com/careers/#jobs-all-roles
 * 
 * @author Muhammad Bin Farook
 * @since: 2019-03-14
 */

public class TestThumbtack extends TestAbstractScrapper {

	private static final String SITE = "https://www.thumbtack.com/careers/#jobs-all-roles";
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

		List<WebElement> joblist = driver.findElements(By.xpath("//div[@class='tp-wrap-snap']//div"));

		List<WebElement> divs = new ArrayList<>();

		List<String> divsStr = new ArrayList<>();
		List<List<WebElement>> links = new ArrayList<List<WebElement>>();
		List<List<String>> flinks = new ArrayList<List<String>>();

		for (WebElement el : joblist) {

			if (el.getAttribute("class").contains("opportunities__entry")) {
				divs.add(el);
				System.out.println(el.getAttribute("id").toString());
				divsStr.add(el.getAttribute("id").toString());
				List<WebElement> joblink = el.findElements(By.tagName("a"));
				links.add(joblink);
			}
		}

		for (List<WebElement> el : links) {
			List<String> fas = new ArrayList<>();
			for (WebElement ell : el) {
				if (ell.getAttribute("href").contains("boards.greenhouse.io")) {
					fas.add(ell.getAttribute("href").toString());
				}
			}
			flinks.add(fas);
		}

		for (int i = 0; i < divs.size(); i++) {
			String id = "";

			id = divsStr.get(i);

			List<String> li = flinks.get(i);

			testJobDetailPage(li, id);

		}

	}

	public void testJobDetailPage(List<String> joblink, String category) {

		for (String ell : joblink) {

			driver.get(ell);
			wait = new WebDriverWait(driver, 30);
			System.out.println("JOB TITLE: "
					+ driver.findElement(By.xpath("//div[@id='header']/h1[@class='app-title']")).getText());
			System.out.println("JOB CATAGORY: " + category + "\n");
			System.out.println("JOB DESCRIPION: " + driver.findElement(By.xpath("//div[@id='content']")).getText());

			System.out.println("JOB LOCATION: "
					+ driver.findElement(By.xpath("//div[@id='header']/div[@class='location']")).getText() + "\n");
			System.out.println();

		}
	}

}