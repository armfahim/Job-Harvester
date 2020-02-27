package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.talent.model.Job;

/**
 * Mapfre Jobsite parser<br>
 * URL:
 * https://jobs.mapfre.com/search/?q=&sortColumn=referencedate&sortDirection=desc&startrow=
 * 
 * @author sohid.ullah
 * @since 2019-04-18
 */

public class TestMapfreHTMLUnit extends TestAbstractScrapper {

	private static final String BASE_URL = "https://jobs.mapfre.com/search/?q=&sortColumn=referencedate&sortDirection=desc&startrow=0";
	private static HtmlPage page;
	private static WebClient webClient;
	
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private static final String ROW_EL_PATH = "//*[@id=\"searchresults\"]/tbody/tr/td[1]/span/a";
	private static final String DAT_EL_PATH = "//*[@id=\"job-date\"]/span";
	private static final String LOC_EL_PATH = "//*[@id=\"job-location\"]/span";
	private static final String SPEC_EL_PATH = "//*[@id=\"content\"]/div/div[2]/div/div[2]/span/div[1]";
	private static final String TOTAL_JOB_EL_PATH = "//*[@id=\"content\"]/div/div[5]/div/div/div/span[1]/b[2]";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		webClient = getFirefoxClient();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		webClient.close();
	}

	@Test
	public void testJobSummaryPage() throws InterruptedException {
		try {
			page = webClient.getPage(BASE_URL);

			webClient.waitForBackgroundJavaScript(40000);
			
			List<HtmlElement> numberOfJobEl = page.getBody().getByXPath(TOTAL_JOB_EL_PATH);
			double totalPage = (Double.parseDouble(numberOfJobEl.get(0).asText().trim())) / 25;
			totalPage = Math.ceil(totalPage);
			System.out.println(totalPage);
			

		} catch (FailingHttpStatusCodeException | IOException e) {
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void testJobDetailElement()
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

		WebClient client = getFirefoxClient();

		try {
			String url = "https://jobs.mapfre.com/job/GIRONA-AGENTE-COMERCIAL-NEGOCIO-VIDA-GI/515578201/";
			HtmlPage page = client.getPage(url);
			client.waitForBackgroundJavaScript(7 * 1000);
			HtmlElement el = (HtmlElement) page.getElementById("job-title");
			Job job = new Job(url);
			
			job.setName(el.getTextContent());
			job.setTitle(job.getName());
			el = page.getBody().getFirstByXPath(DAT_EL_PATH);
			System.out.println(el.getTextContent().trim());
			job.setPostedDate(parseDate(el.getTextContent(), DF));
			el = page.getBody().getFirstByXPath(LOC_EL_PATH);
			job.setLocation(el.getTextContent());
			el = page.getBody().getFirstByXPath(SPEC_EL_PATH);
			job.setSpec(el.getTextContent());
		} catch (FailingHttpStatusCodeException | IOException | NullPointerException e) {
			
		}
	}
}
