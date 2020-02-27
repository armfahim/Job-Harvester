package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestCoocaa {
	
	private static final String SITE="http://special.zhaopin.com/2016/shz/cw040621/member-tcl-kukai.html?winzoom=1";
	private static Document document;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		document = Jsoup.connect(SITE).get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		document.clearAttributes();
	}

	@Test
	public void testGetJobList() {
		Elements jobList = document.select(".job_bg > ul");
		for (Element element : jobList) {
			System.out.println("JOB URL: "+element.child(0).child(0).attr("href"));
			System.out.println("TITLE: "+element.child(0).text());
			System.out.println("CATEGORY: "+element.child(1).text());
			System.out.println("LOCATION: "+element.child(2).text());
		}
	}

	@Test
	public void testGetJobDetails() throws IOException {
		String link = "http://jobs.zhaopin.com/CC366234122J00157147605.htm?ssidkey=y&ff=02&ss=101";
		document = Jsoup.connect(link).get();
		Elements elements = document.select(".pos-ul *");
		Elements elements2 = document.select(".jjtxt *");
		System.out.println(elements.text().trim()+"\n"+elements2.text().trim());
	}
}
