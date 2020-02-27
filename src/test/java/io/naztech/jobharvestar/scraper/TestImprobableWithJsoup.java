package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.naztech.talent.model.Job;

public class TestImprobableWithJsoup {

	private static String baseurl = "https://improbable.io";
	private static String url = "https://improbable.io/careers/teams";
	private static Document document;
	int totalJob = 0;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		document = Jsoup.connect(url).get();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		document.clearAttributes();
	}

	@Test
	public void totalJob() {
		Elements jobDivEl = document.getElementsByClass("improbablev3/careers/teams_team-card");
		for (int i = 0; i < jobDivEl.size(); i++) {
			String text[] = document.getElementsByClass(
					"improbablev3/partials/components/card_footer-info improbablev3/partials/components/card_footer-info--highlight")
					.get(i).text().split(" ");
			String checkJob = text[0].trim();
			int convertCheckJob = Integer.parseInt(checkJob);
			totalJob = convertCheckJob + totalJob;
		}
		System.out.println("Total Jobs: " + totalJob);
	}

	@Test
	public void testNextPage() throws IOException {
		Elements jobDivEl = document.getElementsByClass("improbablev3/careers/teams_team-card");
		for (Element element : jobDivEl) {
			String jobUrl = baseurl + element.getElementsByTag("a").attr("href");
			System.out.println(jobUrl);
			document = Jsoup.connect(jobUrl).get();
			Elements jobRow = document.getElementsByClass("improbablev3/partials/careers/category_card");
			for (Element element2 : jobRow) {
				System.out.println("\n" + "Job Title: "
						+ element2.getElementsByClass("improbablev3/partials/components/card_title").text());
				System.out.println("\n" + "Job Location: "
						+ element2.getElementsByClass("improbablev3/partials/components/card_footer-info ").text());
				String jobDetails = baseurl + element2.getElementsByTag("a").attr("href");
				System.out.println(jobDetails);
			}
		}
	}

	@Test
	public void testGetJobDetails() throws IOException {
		Elements jobDivEl = document.getElementsByClass("improbablev3/careers/teams_team-card");
		for (Element element : jobDivEl) {
			String jobUrl = baseurl + element.getElementsByTag("a").attr("href");

			document = Jsoup.connect(jobUrl).get();

			Elements jobDetails = document.getElementsByClass("improbablev3/partials/careers/category_card");
			for (Element element2 : jobDetails) {
				Job job = new Job();

				String jobDetailsUrl = baseurl + element2.getElementsByTag("a").attr("href");
				/*
				 * String jobTitle =
				 * element2.getElementsByClass("improbablev3/partials/components/card_title").
				 * text(); String jobLocation = element2.
				 * getElementsByClass("improbablev3/partials/components/card_footer-info ")
				 * .text();
				 */

				document = Jsoup.connect(jobDetailsUrl).get();
				Elements spec = document.getElementsByClass("improbablev3/careers/job_content");
				job.setSpec(spec.text());
				System.out.println(spec.text() + "\n");
			}
		}
	}
}
