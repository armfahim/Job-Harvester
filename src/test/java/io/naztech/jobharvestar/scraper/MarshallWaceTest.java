package io.naztech.jobharvestar.scraper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.naztech.jobharvestar.utils.ConnectionProvider;

public class MarshallWaceTest {
	private ConnectionProvider con;
	private static final String SITE_URL = "https://www.mwam.com/roles";
	private static final int MAX_RETRY = 3;

	@BeforeClass
//	public static void setUpBeforeClass() throws Exception {
//		client = new WebClient(BrowserVersion.FIREFOX_52);
//		client.getOptions().setTimeout(30 * 1000);
//		client.getOptions().setUseInsecureSSL(true);
//		client.getCookieManager().setCookiesEnabled(true);
//		client.setJavaScriptTimeout(30 * 1000);
//	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Test
	public void testAllJobUrl() {
		Document doc = con.getConnection(SITE_URL, MAX_RETRY);

		Elements jobTitle = doc.select("ul.Vacancies__list > li.Vacancies__list__item");
		for (Element element : jobTitle) {
			String title = element.text();
			System.out.println("Title : " + title);
		}
	}

}
