package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class TestBankOAmerica {
	
	private static WebClient client= new WebClient(BrowserVersion.FIREFOX_52);
	//private final String TOTAL_JOB_PATH="//div[@id='tb-head']/p[1]/b[2]";
//	private final String JOB_LOAD_SCOPE="//div[@id='tb-head']/select";
//	private final String JOB_LOAD_OPTION="//div[@id='tb-head']/select/option";
	private final String JOB_LINK_PATH="//section[@id='search-result']/table/tbody/tr";
//	private static String totalJob;
	
	@Before
	public void beforeClass() {
		client.getOptions().setTimeout(1000*30);
		client.getOptions().setThrowExceptionOnScriptError(false);
		client.getOptions().setUseInsecureSSL(true);
	}
	@After
	public void afterClass() {
		client.close();
	}
	@Test
	public void test() throws InterruptedException {
		String url="http://careers.bankofamerica.com/search-jobs.aspx?c=&r=";
		try {
			HtmlPage page= client.getPage(url);
			
		//	DomElement el= page.getBody().getFirstByXPath(TOTAL_JOB_PATH);
			//totalJob= el.getTextContent().trim();
			HtmlAnchor anchor= page.getBody().getFirstByXPath("//tr[@class='gridtext pagination']/td/a");
			List<HtmlElement> list= new ArrayList<HtmlElement>();
			do {
				List<HtmlElement> jobPerPage= page.getBody().getByXPath(JOB_LINK_PATH);
				jobPerPage.remove(0);
				jobPerPage.remove(jobPerPage.size()-1);
				list.addAll(jobPerPage);
				log.info("jobs: {}",list.size());
				page= anchor.click();
				anchor= page.getBody().getFirstByXPath("//tr[@class='gridtext pagination']/td/a");
				log.info("Next button: {}",anchor);
			}while(anchor!=null);
//			HtmlSelect select=page.getBody().getFirstByXPath(JOB_LOAD_SCOPE);
//			HtmlOption option= select.getOptionByValue("10");
//			option.setAttribute("value", totalJob);
//			page=select.setSelectedAttribute(option, true);
//			client.waitForBackgroundJavaScript(1000*10);
//			Thread.sleep(1000*3);
//			el=page.getBody().getFirstByXPath(JOB_LOAD_OPTION);
//			log.info(el.getAttribute("value"));
//			List<HtmlElement> list= page.getBody().getByXPath(JOB_LINK_PATH);
//			System.out.println("Total job list: "+list.size());
			
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.info(e.getMessage());
			e.printStackTrace();
		}
	}
}
