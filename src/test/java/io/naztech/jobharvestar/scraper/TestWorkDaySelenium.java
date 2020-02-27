package io.naztech.jobharvestar.scraper;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * MYWorkDay job site parsing test class. <br>
 * URL: https://aviva.wd1.myworkdayjobs.com/External
 * 
 * @author jannatul.maowa
 * @since 2019-06-09
 */

public class TestWorkDaySelenium extends TestAbstractScrapper {

	private static ChromeDriver driver;
	private static WebDriverWait wait;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();

	}
	@Test
	public void testLocationFromDetailsPage() {
		driver.get(
				"https://aviva.wd1.myworkdayjobs.com/en-US/External/job/Bois-Colombes/Alternance-Dveloppeur-Back-end---Ontologie-H-F_R-78632");
		wait = new WebDriverWait(driver, 40);
		wait.until(presenceOfAllElementsLocatedBy(By.cssSelector("button[title='Apply']")));
		int mark = 1;

		List<WebElement> list = driver.findElements(By.xpath("//div[@class='gwt-Label WEUO WOSO']"));
		System.out.println(list.size());
		for (int i = 0; i < list.size(); i++)
			if (list.get(i).getText().contains("Posted")) {
				mark = i;
				break;
			}
		if (mark == 1)
			System.out.println(list.get(mark - 1).getText());
		if (mark == 2)
			System.out.println(list.get(mark - 2).getText() + "," + list.get(mark - 1).getText());
	}
}
