package io.naztech.jobharvestar.scraper;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


import io.naztech.talent.model.Job;

/**
 * Test Rocket Lab jobs site parsing using jsoup.
 *  https://www.rocketlabusa.com/careers/positions/
 * @author Rahat Ahmad
 * @since 2019-03-31
 */

public class TestRocketLab extends TestAbstractScrapper{

	private static String URL = "https://www.rocketlabusa.com/careers/positions/";
	private static Document document;
	private static String HEAD = "https://www.rocketlabusa.com";
	private static WebDriver driver = null;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		document = Jsoup.connect(URL).get();
		driver = getChromeDriver();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		document.clearAttributes();
	}
	
	@Test
	public void getJobList() throws InterruptedException {
		List<WebElement> jobListE = viewAlljob();
		System.out.println(jobListE.size());
	}
	
	@Test
	public void getFirstPage() throws InterruptedException {
		List<WebElement> jobListE = viewAlljob();
		for (WebElement webElement : jobListE) {
			System.out.println(webElement.getAttribute("href"));
			System.out.println(webElement.findElement(By.className("job__title")).getText());
			System.out.println(webElement.findElement(By.className("job__location")).getText());
		}
	}
	
	@Test
	public void getJobDetail() throws IOException {
		Elements jobEl= document.select("a.job");
		List<Job> jobList = new ArrayList<>();
		for(int i = 0;i<jobEl.size();i++) {
			Job job = new Job();
			job.setUrl(HEAD+jobEl.get(i).attr("href"));
			job.setTitle(jobEl.get(i).select("h3.job__title").text());
			job.setLocation(jobEl.get(i).select("h5.job__location").text());
			jobList.add(job);
		}
		
		for (Job job : jobList) {
			document = Jsoup.connect(job.getUrl()).get();
			job.setSpec(document.select("div.job__info-subtitle").text());
			String[] ab = document.select("p.job__hero-description").text().split(Pattern.quote("|"));
			System.out.println(ab[0].replace("Job Ref: ", ""));
			System.out.println(ab[1].replace("Type: ", ""));
			System.out.println(job.getSpec());
		}
	}
	
	private List<WebElement> viewAlljob() throws InterruptedException {
		driver.get(URL);
		for(;;) {
			if(driver.findElements(By.xpath("//button[@id='JobsAjaxBtn']")).isEmpty()) break;
			driver.findElement(By.id("JobsAjaxBtn")).click();
			Thread.sleep(TIME_4S);
		}
		return driver.findElements(By.xpath("//a[@class='job']"));
	}

}
