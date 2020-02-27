package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class TestUobMalaysia extends TestAbstractScrapper {
	
	private static final String SITE = "https://www.jobstreet.com.my/career/uobm.htm";
	private static ChromeDriver driver;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
	}

	@Test
	public void testGetJobList()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		driver.get(SITE);
		Thread.sleep(TIME_4S);
		String total =driver.findElement(By.xpath("//form[@name='criteria']/section[1]/span[1]")).getText().split("of")[1].trim();
		System.out.println(total.split(Pattern.quote("job(s)"))[0]);
		System.out.println(driver.findElementsByXPath("//div[@class = 'rPaging']/a").get(driver.findElementsByXPath("//div[@class = 'rPaging']/a").size()-2).getText());
		for(int i = 0 ; i<3;i++) {
			List<WebElement> jobListE = driver.findElementsByXPath("//table[@class = 'result']/tbody/tr");
			for (int j = 1;j<jobListE.size()-1;j++) {
				System.out.println(jobListE.get(j).findElement(By.tagName("a")).getAttribute("href")); //job url
				System.out.println(jobListE.get(j).findElement(By.tagName("a")).getText()); // title
				System.out.println(jobListE.get(j).findElements(By.tagName("td")).get(2).getText()); // job location
				System.out.println(jobListE.get(j).findElements(By.tagName("td")).get(3).getText()); // job category
			}
			System.out.println(jobListE.size());
			List<WebElement> list = driver.findElementsByXPath("//div[@class = 'rPaging']/a");
			list.get(list.size()-1).click();
			Thread.sleep(TIME_5S);
		}
		
		
	}

}
