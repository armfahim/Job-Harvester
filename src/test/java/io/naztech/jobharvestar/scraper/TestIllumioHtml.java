package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

	public class TestIllumioHtml extends TestAbstractScrapper{

		private static WebClient client;
		
		@BeforeClass
		public static void setUpBeforeClass() throws Exception {
			client =getFirefoxClient();	
		}

		@Test
		public void testgetScrapedJobs() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
			HtmlPage page=client.getPage("https://boards.greenhouse.io/embed/job_board?for=illumio&b=https%3A%2F%2Fwww.illumio.com%2Fcareer-openings");
			client.waitForBackgroundJavaScript(30 * 1000);
			List<HtmlElement> list = page.getBody().getByXPath("//section[@class='level-0']/div/a");
			List<HtmlElement> dept_id_list = page.getBody().getByXPath("//section[@class='level-0']/div");
			System.out.println(list.size());
			System.out.println(dept_id_list.size());
		}

	}
