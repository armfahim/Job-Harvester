package io.naztech.jobharvestar.scraper;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

public class TestOnex {
	private static final String PDF_PATH = "https://www.onexcompany.com/wp-content/uploads/2018/04/%CE%91%CE%93%CE%93%CE%95%CE%9B%CE%99%CE%91-BUSINESS-INTELLIGENCE-CONSULTANTS.pdf";
	private static final String SITE_URL = "https://www.onexcompany.com/careers/";
	private static final String ROW_EL_PATH = "//*[@id=\"content\"]/div[2]/div[2]/div/div/div/div/h5/a";
	private static WebClient client;
	private static PdfReader reader;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		URL url = new URL(PDF_PATH);
		reader = new PdfReader(url);
		client = new WebClient(BrowserVersion.FIREFOX_52);
		client.getOptions().setTimeout(50*1000);
		client.getOptions().setUseInsecureSSL(true);
		client.setAjaxController(new NicelyResynchronizingAjaxController());
		client.getCookieManager().setCookiesEnabled(true);
		client.getOptions().setThrowExceptionOnFailingStatusCode(false);
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.waitForBackgroundJavaScriptStartingBefore(10*1000);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		reader.close();
		client.close();
	}

	@Test
	public void pdfParse() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		StringBuffer sb = new StringBuffer();
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		TextExtractionStrategy strategy;
		for(int i=1;i<=reader.getNumberOfPages();i++) {
			strategy = parser.processContent(i, new SimpleTextExtractionStrategy());
			sb.append(strategy.getResultantText());
		}
		System.out.println(sb);
	}
	
	@Test
	public void testPageLoad() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = client.getPage(SITE_URL);
		client.waitForBackgroundJavaScript(5*1000);
		List<HtmlElement> rowList = page.getBody().getByXPath(ROW_EL_PATH);
		assertEquals(2, rowList.size());
	}

}
