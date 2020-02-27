package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestBetter extends TestAbstractScrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private final String SITE="https://www.better.org.uk/jobs";
	private static WebClient webClient;
	private final int JOB_PER_PAGE = 8;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		webClient=getChromeClient();
		webClient.getOptions().setRedirectEnabled(true);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		webClient.close();
	}

	@Test
	public void testGetJobList() {
		try {
			HtmlPage page = webClient.getPage(SITE);
			webClient.waitForBackgroundJavaScript(TIME_5S);
			List<HtmlElement> divSelector = page.getByXPath("//div[@class='column']");
			for (HtmlElement divEl : divSelector) if(isValid(divEl)) getCategory(divEl);
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error("Failed to load page"+e);
		}
	}
	
	private void getCategory(HtmlElement divEl) {
		try {
			List<HtmlElement> categoryEl = divEl.getElementsByAttribute("span", "class", "title");
			for (HtmlElement catEl : categoryEl) {
				/*System.out.println("CATEGORY: "+catEl.getElementsByTagName("a").get(0).getTextContent().trim());
				System.out.println("CATEGORY URL: "+catEl.getElementsByTagName("a").get(0).getAttribute("href").trim());*/
				getJobList(catEl.getElementsByTagName("a").get(0).getAttribute("href").trim(),catEl.getElementsByTagName("a").get(0).getTextContent().trim());
			}
		} catch (ElementNotFoundException e) {
			log.error("Not found EXPECTED element"+e);
		}
	}
	
	private void getJobList(String url,String category) {
		try {
			HtmlPage page = webClient.getPage(url);
			webClient.waitForBackgroundJavaScript(TIME_5S);
			if (getTotalJob(page) == null) return;
			int pageCount = getPageCount(getTotalJob(page), JOB_PER_PAGE);
			for (int i = 0; i < pageCount; i++) {
				List<HtmlElement> jobSelector = page.getByXPath("//td[@class='erq_searchv4_result_row']");
				for (int j = 0 ; j < jobSelector.size(); j++) {
					System.out.println("CATEGORY: "+category);
					System.out.println("TITLE: "+jobSelector.get(j).getElementsByAttribute("a", "class", "erq_searchv4_big_anchor").get(0).getTextContent().trim());
					System.out.println("JOB ID: "+jobSelector.get(j).getElementsByAttribute("td", "class", "erq_searchv4_heading5_text").get(0).getTextContent().trim());
					System.out.println("LOCATION: "+jobSelector.get(j).getElementsByAttribute("td", "class", "erq_searchv4_heading5_text").get(1).getTextContent().trim());
					page = getJobDetails(jobSelector.get(j).getElementsByAttribute("a", "class", "erq_searchv4_big_anchor").get(0),i,pageCount,j,jobSelector.size());
					System.out.println("----------------------------NEW JOB-----------------------------------");
				}
			}
			System.out.println("----------------------------NEW CATEGORY-----------------------------------");
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error("Failed to load page"+e);
		} catch (IndexOutOfBoundsException e) {
			log.error("Error at Element (tag) or Index of Element"+e);
		}
	}
	
	private boolean isValid(HtmlElement divEl) {
		return "Or browse by division".equalsIgnoreCase(divEl.getElementsByAttribute("h1","class","gg zeta underline").get(0).asText().trim().toString())
					|| "".equalsIgnoreCase(divEl.getElementsByAttribute("h1","class","gg zeta underline").get(0).asText().trim().toString());
	}

	private String getTotalJob(HtmlPage page) {
		try {
			HtmlElement jobCount = page.getBody().getOneHtmlElementByAttribute("span", "class", "erq_searchv4_count");
			return jobCount.getTextContent().split("of")[1].trim();
		} catch (ElementNotFoundException e) {
			return null;
		}
	}
	
	@Test
	public void testGetNextPage() throws InterruptedException {
		try {
			String url = "https://my.corehr.com/pls/gllrecruit/erq_search_version_4.start_search_with_params?p_company=1&p_internal_external=E&p_display_in_irish=N&p_competition_type=LM&p_force_type=E";
			HtmlPage page = webClient.getPage(url);
			webClient.waitForBackgroundJavaScript(TIME_5S);
			int pageCount = getPageCount(getTotalJob(page), JOB_PER_PAGE);
			for (int i = 0; i < pageCount; i++) {
				List<HtmlElement> jobSelector = page.getByXPath("//td[@class='erq_searchv4_result_row']");
				for (int j = 0 ; j < jobSelector.size(); j++) {
					System.out.println("TITLE: "+jobSelector.get(j).getElementsByAttribute("a", "class", "erq_searchv4_big_anchor").get(0).getTextContent().trim());
					System.out.println("JOB ID: "+jobSelector.get(j).getElementsByAttribute("td", "class", "erq_searchv4_heading5_text").get(0).getTextContent().trim());
					System.out.println("LOCATION: "+jobSelector.get(j).getElementsByAttribute("td", "class", "erq_searchv4_heading5_text").get(1).getTextContent().trim());
					page = getJobDetails(jobSelector.get(j).getElementsByAttribute("a", "class", "erq_searchv4_big_anchor").get(0),i,pageCount,j,jobSelector.size());
					System.out.println("----------------------------NEW JOB-----------------------------------");
				}
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error("Failed to load page"+e);
		}
	}
	
	private HtmlPage getJobDetails(HtmlElement jobDetailsUrl,int i,int pageCount,int pos,int size) {
		try {
			System.out.println("=============================START DETAILS====================================");
			HtmlPage detailsPage = jobDetailsUrl.click();
	//		HtmlElement details = detailsPage.getBody().getOneHtmlElementByAttribute("td", "class", "erq_searchv4_heading3 targetLink");
			//System.out.println("DETAILS: "+details.asText());
			System.out.println("=============================END DETAILS====================================");
			if (pos == size-1 && i<pageCount-1) {
				detailsPage = detailsPage.getAnchorByText("Return to Search Results").click();
				Thread.sleep(2*1000);
				webClient.waitForBackgroundJavaScript(TIME_4S);
				detailsPage = detailsPage.getAnchorByText("Next").click();
				Thread.sleep(2*1000);
				webClient.waitForBackgroundJavaScript(TIME_4S);
				return detailsPage;
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error("Failed to load page"+e);
		} catch (InterruptedException e) {
			log.error("TimeOut to LOAD Page"+e);
		}
		return null;
	}
}
