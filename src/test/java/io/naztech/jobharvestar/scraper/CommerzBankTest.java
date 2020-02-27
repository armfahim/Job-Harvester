package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class CommerzBankTest {

	@Test
	public void test() throws InterruptedException {
		
		try {
			WebClient client=new WebClient(BrowserVersion.FIREFOX_52);
			HtmlPage page=client.getPage("https://newton.newtonsoftware.com/career/JobIntroduction.action?clientId=8aa005063062881c01309410255e6bdb&id=8a7886a35bf05069015c0dbab96e4228&source=BHHC.com&code=&fromAggregate=false");
			client.getOptions().setThrowExceptionOnScriptError(false);
			//List<HtmlElement> catList= page.getBody().getByXPath("//div[@id='CareerResults']");
			DomElement catList1= page.getElementById("gnewtonJobDescriptionText");
			System.out.println(catList1.getTextContent());
			HtmlPage currentPage= (HtmlPage)client.getWebWindowByName("icims_content_iframe").getEnclosedPage();
			System.out.println(currentPage.asText());
			HtmlElement iEl= currentPage.getBody().getFirstByXPath("//div[@class='iCIMS_Expandable_Text']");
			System.out.println(iEl);
			client.close();
		} catch (FailingHttpStatusCodeException | IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
