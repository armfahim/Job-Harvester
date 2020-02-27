package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestBancoSantanderUs extends TestAbstractScrapper {

	private static final String JOB_LOCATION_ID = "requisitionDescriptionInterface.ID1767.row1";
	private static final String JOB_TYPE_ID = "requisitionDescriptionInterface.ID1931.row1";
	private static final String JOB_REFID_ID = "requisitionDescriptionInterface.reqContestNumberValue.row1";
	private static final String JOB_DES_ID = "requisitionDescriptionInterface.ID1544.row1";
	private static final String JOB_POSTDATE_ID = "requisitionDescriptionInterface.reqPostingDate.row1";
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM d yyyy");

	private static final String SITE = "https://jobs.santanderbank.com/search-jobs?fl=6252001";
	private static WebClient client = null;

//	private static ChromeDriver driver;
//	private static WebDriverWait wait;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = getFirefoxClient();
	}

	@Test
	public void test() throws IOException {
		HtmlPage page = client.getPage(SITE);
		
		System.out.println(page.getTitleText());
		
		List<HtmlElement> jobList = page.getBody().getByXPath("//section[@id = 'search-results-list']/ul/li/a");
		
		for (HtmlElement it : jobList) System.out.println(it.getAttribute("href"));
		System.out.println("================================================");
		
		HtmlElement nextButton = page.getBody().getOneHtmlElementByAttribute("a", "class", "next");
		page = nextButton.click();

		List<HtmlElement> jobList2 = page.getBody().getByXPath("//section[@id = 'search-results-list']/ul/li/a");
		for (HtmlElement it : jobList2) System.out.println(it.getAttribute("href"));


	}
	
	
	@Test
	public void testGetFirstPage() throws IOException {
		
		HtmlPage page = client.getPage(SITE);
		client.waitForBackgroundJavaScriptStartingBefore(TIME_10S);
		
		List<HtmlElement> jobList = page.getBody().getByXPath("//section[@id = 'search-results-list']/ul/li/a");
		
		for (HtmlElement it : jobList) System.out.println(it.getAttribute("href"));
		System.out.println("================================================");
		
		HtmlElement nextButton = page.getBody().getOneHtmlElementByAttribute("a", "class", "next");
		page = nextButton.click();
		client.waitForBackgroundJavaScriptStartingBefore(TIME_10S);
		List<HtmlElement> jobList2 = page.getBody().getByXPath("//section[@id = 'search-results-list']/ul/li/a");
		
		for (HtmlElement it : jobList2) System.out.println(it.getAttribute("href"));
		
	}

	@Test
	public void testGetDetailPage() throws IOException {

		HtmlPage detailPage = client.getPage("https://jobs.santanderbank.com/job/boston/associate-director-compliance/1771/9925571");

		String[] str = detailPage.getElementById(JOB_POSTDATE_ID).asText().split(",");
		String newDate = str[0] + str[1];
		System.out.println(parseDate(newDate.trim(), DF));

		System.out.println(detailPage.getTitleText());
		System.out.println(detailPage.getElementById(JOB_REFID_ID).asText());
		System.out.println(detailPage.getElementById(JOB_LOCATION_ID).asText());
		System.out.println(detailPage.getElementById(JOB_TYPE_ID).asText());
		System.out.println(detailPage.getElementById(JOB_DES_ID).asText());
		System.out.println(detailPage.getBody().getOneHtmlElementByAttribute("a", "class", "job-apply top").getAttribute("href"));

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
