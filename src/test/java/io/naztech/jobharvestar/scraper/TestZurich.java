package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestZurich extends TestAbstractScrapper {
	private static Logger log = LoggerFactory.getLogger(TestZurich.class);
	private static final String SITE = "https://careers.zurich.com/apply/";
	private static final String HEAD_URL = "https://careers.zurich.com/apply/?page=";
	private static final String TAIL_URL = "&countryid=All";
	private static WebClient webClient;
	private static HtmlPage page;
	private static String baseUrl;

	/*
	 * @Autowired ProxyProvider pool; ProxyReader reader; List<String> proxyList;
	 */

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		webClient = getChromeClient();
		/*
		 * webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);
		 * webClient.waitForBackgroundJavaScript(20 * 1000);
		 * webClient.waitForBackgroundJavaScriptStartingBefore(10 * 1000);
		 * webClient.getOptions().setTimeout(30 * 1000);
		 * webClient.getOptions().setJavaScriptEnabled(true);
		 * webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		 * webClient.getCookieManager().setCookiesEnabled(true);
		 * webClient.getOptions().setUseInsecureSSL(true);
		 * webClient.getOptions().setThrowExceptionOnScriptError(false);
		 * webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		 */
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		webClient.close();
	}

	@Test
	public void testGetJobList() throws InterruptedException {
		try {
			baseUrl = SITE.substring(0, 26);
			page = webClient.getPage(SITE);
			webClient.waitForBackgroundJavaScript(TIME_10S);
			HtmlElement jobAmount = page.getBody().getOneHtmlElementByAttribute("div", "class", "job-list__amount");
			int pageNumber = Integer.parseInt(jobAmount.getTextContent().split("of")[1].trim());
			System.out.println("PAGE COUNT: " + pageNumber);
			//page = webClient.getPage(HEAD_URL + "1" + TAIL_URL);
			for (int i = 1; i <= pageNumber; i++) {
				List<String> jobUrl = new ArrayList<String>();
				System.out.println(HEAD_URL + i + TAIL_URL);
				Thread.sleep(20*1000);
				webClient.waitForBackgroundJavaScript(TIME_10S);
				List<HtmlElement> jobUrList = page.getByXPath("//a[@class='item'][@href]");
				for (HtmlElement htmlElement : jobUrList) {
					jobUrl.add(baseUrl + htmlElement.getAttribute("href").trim());
					//System.out.println("URL: -> " + baseUrl + htmlElement.getAttribute("href").trim());
				}
				getJobDetails(jobUrl);
				
				if (i<pageNumber) {
					List<HtmlButton> nextBtn = page.getByXPath("//button[@class='pagination']");
					for (int j = 0; j < nextBtn.size(); j++) {
						if (j==nextBtn.size()-1) nextBtn.get(j).click();
					}
				}
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error("Error on testing Summary Page", e, e);
		}
	}

	private void getJobDetails(List<String> jobUrl) throws IOException {
		Document document;
		for (String url : jobUrl) {
			document=Jsoup.connect(url).get();
			Element jobHeader = document.selectFirst(".job-details__info");
			Element apply = document.selectFirst(".job-details__text__apply");
			Element details = document.selectFirst(".job-details__text");
			
			System.out.println("TITLE: -> "+jobHeader.getElementsByTag("h2").get(0).text().trim());
			System.out.println("JOB POSTING DATE: -> "+jobHeader.getElementsByTag("p").get(0).text().split(" ")[2].trim());
			System.out.println("APPLY URL: -> "+apply.attr("href").trim());
			System.out.println("LOCATION: -> "+jobHeader.getElementsByTag("span").get(0).text().trim());
			System.out.println("DESC: -> "+details.text().trim());
		}
		System.out.println("=========================================================================");
	}

	@Test
	public void testJobDetailPage() throws IOException {
		String Link = "https://careers.zurich.com/apply/job-details/1621";
		
		Document document;
			document=Jsoup.connect(Link).get();
			Element jobHeader = document.selectFirst(".job-details__info");
			Element apply = document.selectFirst(".job-details__text__apply");
			Element details = document.selectFirst(".job-details__text");
			
			System.out.println("TITLE: -> "+jobHeader.getElementsByTag("h2").get(0).text().trim());
			System.out.println("JOB POSTING DATE: -> "+jobHeader.getElementsByTag("p").get(0).text().split(" ")[2].trim());
			System.out.println("APPLY URL: -> "+apply.attr("href").trim());
			System.out.println("LOCATION: -> "+jobHeader.getElementsByTag("span").get(0).text().trim());
			System.out.println("DESC: -> "+details.text().trim());
		
		
		/*page = webClient.getPage(Link);
		webClient.waitForBackgroundJavaScript(TIME_10S);
		HtmlElement jobHeader = page.getBody().getOneHtmlElementByAttribute("div", "class", "job-details__info");
		HtmlElement apply = page.getBody().getOneHtmlElementByAttribute("a", "class", "job-details__text__apply");
		HtmlElement details = page.getBody().getOneHtmlElementByAttribute("div", "class", "job-details__text");
		
		System.out.println("TITLE: -> "+jobHeader.getElementsByTagName("h2").get(0).getTextContent().trim());
		System.out.println("JOB POSTING DATE: -> "+jobHeader.getElementsByTagName("p").get(0).getTextContent().split(" ")[2].trim());
		System.out.println("APPLY URL: -> "+apply.getAttribute("href").trim());
		System.out.println("LOCATION: -> "+jobHeader.getElementsByTagName("span").get(0).getTextContent().trim());
		System.out.println("DESC: -> "+details.asText().trim());*/
		
		/*String locString = "";
		if(jobHeader.getElementsByTagName("span").get(0).getTextContent().trim().length() > 3)
			locString=jobHeader.getElementsByTagName("span").get(0).getTextContent().trim();
		if (locString=="") {
			List<HtmlElement> locateLoc = details.getByXPath("//div/*");
			for (HtmlElement htmlElement : locateLoc) {
				if (htmlElement.getTextContent().contains("Location:")) {
					locString=htmlElement.getTextContent().trim();
					System.out.println(locString);
					break;
				}
			}
			if(!locString.equals("")) System.out.println("LOCATION: -> "+locString.split("Location:")[1].trim());
			else System.out.println("LOCATION: -> "+locString.trim());
			System.out.println("DESC: -> "+details.asText().replace(locString, "").trim());
		} else {
			System.out.println("LOCATION: -> "+jobHeader.getElementsByTagName("span").get(0).getTextContent().trim());
			System.out.println("DESC: -> "+details.asText().trim());
		}*/
	}
	
	@Test
	public void testGetNextPage() throws IOException {
		try {
			baseUrl = SITE.substring(0, 26);
			page = webClient.getPage(SITE);
			webClient.waitForBackgroundJavaScript(TIME_10S);
			HtmlElement jobAmount = page.getBody().getOneHtmlElementByAttribute("div", "class", "job-list__amount");
			int pageNumber = Integer.parseInt(jobAmount.getTextContent().split("of")[1].trim());
			System.out.println("PAGE COUNT: " + pageNumber);
			for (int i = 1; i <= pageNumber; i++) {
				System.out.println(HEAD_URL + i + TAIL_URL);
				page = webClient.getPage(HEAD_URL + i + TAIL_URL);
				webClient.waitForBackgroundJavaScript(TIME_1S);
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error("Error on testing Summary Page", e, e);
		}
	}
}
