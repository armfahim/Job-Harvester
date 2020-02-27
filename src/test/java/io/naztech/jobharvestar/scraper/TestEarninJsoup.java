package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

/**
 * Earnin Job site parse test in Jsoup <br>
 * https://www.earnin.com/careers
 * 
 * @author jannatul.maowa
 * @since 2019-04-01
 */
public class TestEarninJsoup {
	private static String url = "https://www.earnin.com/careers";

	@Test
	public void testJobCategoryList() throws IOException {
		Document doc = Jsoup.connect(url).get();
		Elements jobCategoryList = doc.select("div[class=vacancies-department-wrapper]");
		System.out.println(jobCategoryList.size());
		for (int i = 0; i < jobCategoryList.size(); i++) {
			Elements jobE = jobCategoryList.get(i).select("div[class=vacancy-apply]>a");
			System.out.println(jobE.size());
			for (int j = 0; j < jobE.size(); j++) {
				System.out.println(jobCategoryList.get(i).selectFirst("div[class='vacancy-department']").text());
				System.out.println(jobE.get(j).attr("href"));
				String baseurl = url.substring(0, 22);
				System.out.println(baseurl);
				String joburl = baseurl + jobE.get(j).attr("href");
				System.out.println(joburl);
			}
		}
	}

}
