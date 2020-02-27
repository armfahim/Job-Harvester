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
 * @name OpenDoorLabs jobsite
 * @author Muhammad Bin Farook
 * @since 2019-03-13
 */
public class TestOpenDoorLabsSelenium extends TestAbstractScrapper{
	private static final String SITE = "https://www.opendoor.com/jobs";
	private static ChromeDriver driver;
	private static WebDriverWait wait;

	@BeforeClass
	public static void beforeClass() {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 50);
	}

	

	
	
	@Test
	public void testJobTotalJob()   throws InterruptedException, IllegalMonitorStateException, TimeoutException {
		driver.get(SITE);
		Thread.sleep(TIME_4S);
		//wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div.regular-container >*")));
		List<WebElement> joblist = driver.findElements(By.xpath("//div[@class='regular-container']//a"));
		//System.out.println(joblist.size());
		List<String>joblink=new ArrayList<>();
		for(WebElement el : joblist) {
			if(el.getAttribute("href").contains("jobs.lever.co")) {
				joblink.add(el.getAttribute("href").toString());
				
			}
		}
		//System.out.println(joblink.size());
		for(int i=0;i<joblink.size();i++) {
			testJobDetailPage(joblink.get(i));
			
			}
		
			
		
	}
	
	public void testJobDetailPage(String joblink) {
		
		driver.get(joblink);
		wait = new WebDriverWait(driver, 30);
		System.out.println("JOB TITLE: "+driver.findElement(By.xpath("//div[@class='posting-headline']/h2")).getText()+"\n");
		 System.out.println("JOB CATAGORY: "+driver.findElement(By.xpath("//div[@class='sort-by-team posting-category medium-category-label']")).getText()+"\n");
		 System.out.println("JOB TYPE: "+driver.findElement(By.xpath("//div[@class='sort-by-commitment posting-category medium-category-label']")).getText()+"\n");
		 System.out.println("JOB LOCATION: "+driver.findElement(By.xpath("//div[@class='sort-by-time posting-category medium-category-label']")).getText()+"\n");
		 System.out.println("JOB APPLICATION URL: "+driver.findElement(By.xpath("//div[@class='postings-btn-wrapper']/a[@class='postings-btn template-btn-submit cerulean']")).getAttribute("href")+"\n");
		 //WebElement jobD=driver.findElement(By.xpath("//div[@class='section-wrapper page-full-width']"));
		//System.out.println(driver.findElementsByCssSelector("div[class='section-wrapper page-full-width'] > div[class='section page-centered']").size());
		List <WebElement> list= driver.findElementsByCssSelector("div[class='section-wrapper page-full-width'] > div[class='section page-centered']");
		//System.out.println(list.size());
		//for(int i=0;i<list.size();i++) {
		System.out.println("JOB SPECIFICATION: \n"+list.get(0).getText()+"\n");
		String str="";
		for(int i=1;i<list.size()-1;i++) {
			str+=list.get(i).getText();
		}
		System.out.println("JOB REQUIREMENTS: \n"+str);
		// }
		System.out.println();
		System.out.println("--------------------------------------------------------------------------------------------------------");
		System.out.println("--------------------------------------------------------------------------------------------------------");	 
		// System.out.println("JOB DETEAILS: "+driver.findElement(By.xpath("//div[@class='section-wrapper page-full-width']/div[@class='section page-centered']")).getText());
		
		
		
		//wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("main#main-content > div > div > div > div > div > p")));
		
		
	}
	

}
