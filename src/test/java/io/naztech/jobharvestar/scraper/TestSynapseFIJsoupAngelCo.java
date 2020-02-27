package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

/**
 * SynapseFI Job site parse test in Jsoup <br>
 * https://angel.co/synapsefi/jobs
 * 
 * @author jannatul.maowa
 * @since 2019-03-28
 */
public class TestSynapseFIJsoupAngelCo {
	@Test
	public void testJobListJ() throws IOException {
		String url = "https://angel.co/hometap/jobs";
		Document doc = Jsoup.connect(url).userAgent(
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31")
				.get();
		Elements jobE = doc.select("a[class=u-fontSize18]");

		for (int i = 0; i < jobE.size(); i++) {
			System.out.println(jobE.get(i).getElementsByAttribute("href"));
		}
	}

	@Test
	public void testJobDetailsJ() throws IOException {
		String url = "https://angel.co/synapsefi/jobs/153172-integration-engineer";
		Document doc = Jsoup.connect(url).userAgent(
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31")
				.get();
		Element jobE;
		jobE = doc.selectFirst("h1[class=u-colorGray3]");
		System.out.println(jobE.text());
	}

}
