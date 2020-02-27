package io.naztech.jobharvestar.scraper;
import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * All Jobsites of https://careers.bbva.com<br>
 * 
 * BBVA ASIA COMPASS URL: https://careers.bbva.com/compass/category-jobs-results/
 * BBVA EUROPE URL: https://careers.bbva.com/europa/jobs-results/
 * BBVA PROVINCIAL URL: https://careers.bbva.com/provincial/jobs-results/
 * BBVA BANCOMER URL: https://careers.bbva.com/bancomer/category-jobs-results/
 * BBVA CONTINENTAL URL: https://careers.bbva.com/continental/jobs-results/
 * BBVA FRANCES URL: https://careers.bbva.com/frances/jobs-results/
 * BBVA URUGUAY URL: https://careers.bbva.com/uruguay/jobs-results/
 * BBVA COLOMBIA URL: https://careers.bbva.com/colombia/jobs-results/
 * BBVA ESPANA URL: https://careers.bbva.com/espana/jobs-results/
 * BBVA PARAGUAY URL: https://careers.bbva.com/paraguay/jobs-results/
 * 
 * @author jannatul.maowa
 * @since 2019-06-12
 */

public class TestAbstractBbvaHtmlUnit extends TestAbstractScrapper {
	private static WebClient client;
	private String url="https://careers.bbva.com/paraguay/jobs-results/";
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		client=getFirefoxClient();
	}

	@Test
	public void testSummaryPage() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage summaryPage = client.getPage(url);
		client.waitForBackgroundJavaScript(TIME_10S*10);
		HtmlElement jobCount = summaryPage.getBody().getFirstByXPath("//div[@id='bloque_contenidoPrincipal']/div/section/div/aside");
		System.out.println(jobCount.getTextContent());
	}

}
