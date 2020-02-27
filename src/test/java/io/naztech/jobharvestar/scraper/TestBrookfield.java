package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Brookfield Asset Management job site parser Test.
 * https://www.brookfield.com/en/our-firm/careers/career-opportunities
 * 
 * @author jannatul.maowa
 * @since 2019-02-13
 */
public class TestBrookfield {

	private static WebClient client;
	private static final String SITE_URL = "https://www.brookfield.com/en/our-firm/careers/career-opportunities";
	private static final String JOB_LIST_EL_PATH = "//table[@class='tablesorter']/tbody/tr";
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM-d-yyyy");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client = new WebClient(BrowserVersion.FIREFOX_52);
		client.getOptions().setTimeout(30 * 1000);
		client.getOptions().setUseInsecureSSL(true);
		client.getCookieManager().setCookiesEnabled(true);
		client.setJavaScriptTimeout(30 * 1000);
	}

	@Before
	public void setUp() throws Exception {
		client.close();
	}

	@Test
	public void testGetScrapedJobs() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE_URL);
		client.waitForBackgroundJavaScript(60 * 1000);
		List<HtmlElement> listJobTitle = page.getBody().getByXPath(JOB_LIST_EL_PATH);
		for (int i = 0; i < listJobTitle.size(); i++) {
			System.out.println("Jobtitle:  "
					+ listJobTitle.get(i).getElementsByTagName("td").get(0).getElementsByTagName("a").get(0));
			System.out.println("joblink:  " + listJobTitle.get(i).getElementsByTagName("td").get(0)
					.getElementsByTagName("a").get(0).getAttribute("href"));
			System.out.println("division:  " + listJobTitle.get(i).getElementsByTagName("td").get(1).getTextContent());
		}
	}

	/*
	 * Testcase for Link e.g :
	 * https://brookfieldam.applytojob.com/apply/Y3YGFrAcIQ/NET-Developer
	 */
	/* ========================== First Type ============================= */
	@Test
	public void testGetJobType() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String str = "https://brookfieldam.applytojob.com/apply/Y3YGFrAcIQ/NET-Developer";
		HtmlPage page = client.getPage(str);
		client.waitForBackgroundJavaScript(3 * 1000);
		HtmlElement body = page.getBody();
		System.out.println(
				body.getOneHtmlElementByAttribute("li", "id", "resumator-job-employment").getTextContent().trim());
	}

	@Test
	public void testGetJobDept() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String str = "https://brookfieldam.applytojob.com/apply/Y3YGFrAcIQ/NET-Developer";
		HtmlPage page = client.getPage(str);
		client.waitForBackgroundJavaScript(3 * 1000);
		HtmlElement body = page.getBody();
		System.out.println(body.getOneHtmlElementByAttribute("li", "title", "Department").getTextContent().trim());
	}

	@Test
	public void testGetJobDescription() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String str = "https://brookfieldam.applytojob.com/apply/Y3YGFrAcIQ/NET-Developer";
		HtmlPage page = client.getPage(str);
		client.waitForBackgroundJavaScript(60 * 1000);
		HtmlElement body = page.getBody();
		System.out.println(body.getOneHtmlElementByAttribute("li", "title", "Location").getTextContent().trim());
		System.out.println(body.getOneHtmlElementByAttribute("div", "class", "description").getTextContent());
	}

	/*
	 * Testcase for Link e.g :
	 * https://brookfield.catsone.com/careers/index.php?m=portal&a=details&
	 * jobOrderID=11879320
	 */
	/* ========================== Second Type ============================= */
	@Test
	public void testGetJobLocation() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String str = "https://brookfield.catsone.com/careers/index.php?m=portal&a=details&jobOrderID=11879314";
		HtmlPage page = client.getPage(str);
		client.waitForBackgroundJavaScript(3 * 1000);
		HtmlElement body = page.getBody();
		System.out.println(body.getOneHtmlElementByAttribute("div", "id", "jobDetailLocation").getTextContent()
				.replace("Location: ", "").trim());
	}

	@Test
	public void testPostedDate() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String str = "https://brookfield.catsone.com/careers/index.php?m=portal&a=details&jobOrderID=11879314";
		HtmlPage page = client.getPage(str);
		client.waitForBackgroundJavaScript(3 * 1000);
		HtmlElement body = page.getBody();
		String date = body.getOneHtmlElementByAttribute("div", "id", "jobDetailPosted").getTextContent()
				.replace("Date Posted: ", "").trim();
		LocalDate localDate = LocalDate.parse(date.trim(), DF);
		System.out.println(localDate);
	}

	@Test
	public void testDetail() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String str = "https://brookfield.catsone.com/careers/index.php?m=portal&a=details&jobOrderID=11879314";
		HtmlPage page = client.getPage(str);
		client.waitForBackgroundJavaScript(3 * 1000);
		HtmlElement body = page.getBody();
		System.out.println(body.getOneHtmlElementByAttribute("div", "class", "detailsJobDescription").getTextContent());
	}

	@Test
	public void testApplyNowButton() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String str = "https://brookfield.catsone.com/careers/index.php?m=portal&a=details&jobOrderID=11879314";
		HtmlPage page = client.getPage(str);
		client.waitForBackgroundJavaScript(3 * 1000);
		List<HtmlElement> listJobType = page.getBody()
				.getByXPath("//div[@id='jobDetails']//div[@class='actionButtons']//div[@class='button']");
		System.out.println(listJobType.get(0).getElementsByTagName("a").get(0).getAttribute("href"));
	}

	/*
	 * Testcase for Link e.g :
	 * https://brookfieldam.applytojob.com/apply/ex3eUt0vRY/Accounts-Payable-
	 * Assistant-Finance-London
	 */
	/* ========================== Third Type ============================= */
	@Test
	public void testDetailPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		String url = "https://brookfieldam.applytojob.com/apply/ex3eUt0vRY/Accounts-Payable-Assistant-Finance-London";
		HtmlPage page = client.getPage(url);
		List<HtmlElement> jobE = page.getBody().getByXPath("//ul[@class='list-inline job-attributes']/li");
		System.out.println(jobE.size());
		HtmlElement jobEl = page.getFirstByXPath("//div[@class='container']/h1");
		System.out.println("Title:" + jobEl.getTextContent().trim());
		jobEl = page.getBody().getOneHtmlElementByAttribute("li", "title", "Location");
		System.out.println("location:" + jobEl.getTextContent().trim());
		jobEl = page.getBody().getOneHtmlElementByAttribute("li", "id", "resumator-job-employment");
		System.out.println("type:" + jobEl.getTextContent().trim());
		jobEl = page.getBody().getOneHtmlElementByAttribute("li", "title", "Department");
		System.out.println("Category:" + jobEl.getTextContent().trim());
		jobEl = page.getBody().getOneHtmlElementByAttribute("div", "class", "description");
		System.out.println("Description:" + jobEl.getTextContent().trim());

	}
}
