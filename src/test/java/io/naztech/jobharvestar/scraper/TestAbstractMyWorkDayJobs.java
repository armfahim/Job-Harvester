package io.naztech.jobharvestar.scraper;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Test AbstractMyWorkDayJobs jobs site parsing using selenium web driver.
 * 
 * @author assaduzzaman.sohan
 * @author jannatul.maowa
 * @since 2019-04-15
 */
public class TestAbstractMyWorkDayJobs extends TestAbstractScrapper {
	private static final String SITE = "https://tal.wd3.myworkdayjobs.com/TAL-current-opportunities";
	private static final String detailPageLink = "https://ncino.wd5.myworkdayjobs.com/en-US/nCino/job/Wilmington-NC/Recruiter_R749";

	private static final String JOB_COUNT_EL_ID = "wd-FacetedSearchResultList-PaginationText-facetSearchResultList.jobProfile.data";

	private static ChromeDriver driver;
	private static WebDriverWait wait;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 50);
	}

	@Test
	public void testTotalJob() {
		driver.get(SITE);
		wait.until(presenceOfElementLocated(
				By.id("wd-FacetedSearchResultList-PaginationText-facetSearchResultList.newFacetSearch.Report_Entry")));
		String[] part = driver
				.findElement(By.id(
						"wd-FacetedSearchResultList-PaginationText-facetSearchResultList.newFacetSearch.Report_Entry"))
				.getText().split(" ");
		int totalJobs = Integer.parseInt(part[0].trim());
		System.out.println("Total Jobs:" + totalJobs);
	}

	@Test
	public void testAllLocation() {

		driver.get(SITE);
		wait.until(presenceOfElementLocated(By.id(JOB_COUNT_EL_ID)));

		List<WebElement> allLocation = driver.findElements(By.xpath("//span[@class='gwt-InlineLabel WO-F WNYF']"));
		System.out.println(allLocation.size());
		for (int i = 0; i < allLocation.size(); i++)
			System.out.println(allLocation.get(i).getText());
	}

	@Test
	public void testGetJobList() throws InterruptedException {

		driver.get(SITE);
		wait.until(presenceOfElementLocated(By.id(JOB_COUNT_EL_ID)));

		int totalJobs = getTotalJobs();
		List<WebElement> rowListE;
		do {
			driver.executeScript("window.scrollBy(0,document.body.scrollHeight)");
			rowListE = driver.findElements(By.xpath("//div[@class='gwt-Label WEUO WOSO']"));
			if(rowListE.isEmpty()) {
				//log.warn("Job row list id chnaged");
				break;
			}
		} while (rowListE.size() < totalJobs);
		
		System.out.println(rowListE.size());
		
		Actions action = new Actions(driver);
		Set<String> st = new HashSet<>();

		for (int i = 0; i < rowListE.size(); i++) {
			int count = 0;
			int flag = 0;
			while (flag == 0) {
				WebElement el = rowListE.get(i);
				try {
					action.moveToElement(el);
					action.contextClick().build().perform();
					WebElement linkE = wait.until(presenceOfElementLocated(By.cssSelector("div[data-automation-id='copyUrl']")));
					String url = linkE.getAttribute("data-clipboard-text");
					st.add(url);
					flag = 1;
					Thread.sleep(1000);
					action.contextClick();
				} catch (StaleElementReferenceException e) {
					System.out.println("Stale khaisi: "+el.getText().trim());
					count++;
					if (count == 3) {
						flag = 1;
					}
				}
			}
		}

		System.out.println("Total Links in Set: " + st.size());
		for (String it : st) {
			System.out.println(it);
		}

	}

	private int getTotalJobs() {
		wait.until(presenceOfElementLocated(By.id(JOB_COUNT_EL_ID)));
		String[] part = driver.findElement(By.id(JOB_COUNT_EL_ID)).getText().split(" ");
		System.out.println("total jobs: "+Integer.parseInt(part[0].trim()));
		return Integer.parseInt(part[0].trim());
	}

	@Test
	public void testFirstPage() throws InterruptedException {

	}

	@Test
	public void testGetAllTitle() throws InterruptedException {
		driver.get(SITE);
		wait.until(presenceOfElementLocated(By.id(JOB_COUNT_EL_ID)));

		List<WebElement> allTitle = driver.findElements(By.id("monikerList"));
		for (int i = 0; i < allTitle.size(); i++)
			System.out.println(allTitle.get(i).getText());

		System.out.println(allTitle.size());

	}

	@Test
	public void testGetJobDetails() throws IOException {
		driver.get(detailPageLink);
		wait = new WebDriverWait(driver, 40);
		wait.until(presenceOfAllElementsLocatedBy(By.cssSelector("button[title='Apply']")));

		List<WebElement> titleE = driver.findElementsByClassName("GWTCKEditor-Disabled");
		System.out.println("Title: " + titleE.get(0).getText());
		List<WebElement> lists = wait
				.until(presenceOfAllElementsLocatedBy(By.xpath("//div[@data-automation-id='responsiveMonikerInput']")));
		System.out.println(lists.size());
		System.out.println("Location:" + lists.get(0).getText());
		System.out.println("type:" + lists.get(lists.size() - 3).getText());
		System.out.println("refid:" + lists.get(lists.size() - 2).getText());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		//driver.close();
	}

}