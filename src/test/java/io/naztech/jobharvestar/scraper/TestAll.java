package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestAll extends TestAbstractScrapper{

	private static final String SITE = "https://www.particle.io/jobs/?gh_jid=1566691";

	private static ChromeDriver driver;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		driver = getChromeDriver();
	}
	
	
	@Test
	public void test() throws IOException {
		driver.get(SITE);
		driver.get(driver.findElementById("grnhse_iframe").getAttribute("src"));
//		List<WebElement> list = driver.findElementsByCssSelector(".link.link-inline");
//		List<String> allJobLink = new ArrayList<>();
//		for (int i = 0; i < list.size(); i++) {
//			String Link = list.get(i).getAttribute("href");
//			if(Link==null)continue;
//			if (Link.contains("/jobs/")) allJobLink.add(Link);
//		}
//		System.out.println("Link Found: "+allJobLink.size());
//		for(int i=0; i<allJobLink.size(); i++) {
//			System.out.println(allJobLink.get(i));
//		}
//	
//		HtmlPage page = CLIENT.getPage(SITE);
//		CLIENT.waitForBackgroundJavaScript(TIME_10S);
//		System.out.println(page.asXml());
	}
}



//System.out.println("===================================================================");
//System.out.println("Job Title = "+title);
//System.out.println("Job Id = "+jobId);
//System.out.println("Job cate = "+cate);
//System.out.println("Location = "+location);
//System.out.println("salary = "+Salary);
//System.out.println("Job closingDate = "+closingDate);
//System.out.println("Job applyUrl = "+applyUrl);
//System.out.println("Job prereq = "+prereq);
//System.out.println("Job des = "+des);
//System.out.println("===================================================================");
