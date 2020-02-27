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

/**
 * rubrik job site scraper. <br>
 * URL: https://www.rubrik.com/company/careers/
 * 
 * @author Asadullah Galib
 * @since 2019-03-19
 */
public class TestRubrik extends TestAbstractScrapper{
	private static final String SITE = "https://www.rubrik.com/company/careers/";
	private static final String Summary_url = "https://www.rubrik.com/company/careers/channel-sales/jobs/channel-sales-engineer-texas-1580908/?gh_jid=1580908&gh_src=lq16hy1";
	
	private static WebClient CLIENT = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		CLIENT = getFirefoxClient();
	}

	@Test
	public void testGetJobList() throws FailingHttpStatusCodeException, MalformedURLException, IOException {

		HtmlPage page = CLIENT.getPage(SITE);
		List<HtmlElement> el = page.getByXPath("//div[@class='col-xs-12 col-sm-6 col-lg-4 item']");


		for (HtmlElement tr : el) {

			HtmlElement url = tr.getElementsByTagName("a").get(0);
			HtmlPage page1=CLIENT.getPage(url.getAttribute("href"));
			List<HtmlElement> ab = page1.getByXPath("//div[@class='job']");
			System.out.println(ab.size());
			for (HtmlElement tb : ab) {
				HtmlElement url1 = tb.getElementsByTagName("a").get(0);
				System.out.println(url1.getAttribute("href"));
			
			}
			}

		}
	
	@Test
	public void testGetsummary() throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		 
		HtmlPage page = CLIENT.getPage(Summary_url);
		System.out.println("Title:"+ page.getBody().getOneHtmlElementByAttribute("h1", "class", "hero__title").asText());
		System.out.println("Location:"+ page.getBody().getOneHtmlElementByAttribute("span", "class", "hero__subtitle").asText());
		System.out.println("Spec:"+ page.getBody().getOneHtmlElementByAttribute("div", "class", "wysiwyg col-md-10 col-lg-8 col-lg-push-1").asText());
	}
		
	}




