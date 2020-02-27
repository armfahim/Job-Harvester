package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * HCL Technology Job Site Parser<br>
 * URL: https://www.hcltech.com/careers#careers_opportunities
 * 
 * @author fahim.reza
 * @since 2019-10-16
 */
@Slf4j
@Service
public class HclTechnology extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.HCL_TECHNOLOGIES;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM dd, yyyy");
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static WebClient client = null;

	@Override
	public void scrapJobs() throws Exception {
		client = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 23);
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		Elements continentList = doc.select("ul[class=nav nav-tabs nav-justified] > li");
		continentList.remove(0);
		for (Element el : continentList) {
			String continentalUrl = el.getElementsByTag("a").attr("href");
			try {
				browseContinentalList(continentalUrl, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse continental list page of " + continentalUrl, e);
			}
		}
	}

	private void browseContinentalList(String continentUrl, SiteMetaData siteMeta) throws IOException {
		HtmlPage page = client.getPage(continentUrl);
		try {
			HtmlElement button = page.getFirstByXPath("//a[@class='btn btn-warning']");
			if (button.getAttribute("href") != null) {
				browseJobList(button.getAttribute("href"), siteMeta);
			} 
			else {
				browseJobList(continentUrl, siteMeta);
			}
		} catch (Exception e) {
			log.warn("Button not available", continentUrl);
			browseJobList(continentUrl, siteMeta);
		}

	}

	private void browseJobList(String url, SiteMetaData siteMeta) throws IOException {
		Document doc = Jsoup.connect(url).get();
		getSummaryPages(doc, siteMeta);
		while (true) {
			if (getNextPageUrl(doc) == null)
				break;
			String nextPageUrl = getBaseUrl() + getNextPageUrl(doc);
			try {
				doc = Jsoup.connect(nextPageUrl).get();
				Thread.sleep(TIME_1S * 45);
				getSummaryPages(doc, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + nextPageUrl, e);
			}
		}
	}

	private String getNextPageUrl(Document doc) throws IOException {
		Element button = doc.selectFirst("li[class=pager__item]>a");
		if (button == null)
			return null;
		return button.attr("href");
	}

	private void getSummaryPages(Document doc, SiteMetaData siteMeta) throws IOException {
		Elements title = doc.select("td[class=views-field views-field-field-kenexa-jobs-designation] > a");
		Elements posted = doc.select("td[class=views-field views-field-field-kenxa-jobs-updated-date]");
		Elements location = doc.select("td[class=views-field views-field-field-kenexa-jobs-location]");
		expectedJobCount += title.size();
		for (int i = 0; i < title.size(); i++) {
			Job job = new Job();
			job.setUrl(getBaseUrl() + title.get(i).attr("href"));
			job.setTitle(title.get(i).text().trim());
			job.setName(job.getTitle());
			job.setLocation(location.get(i).text().trim());
			job.setPostedDate(parseDate(posted.get(i).text().trim(), DF));
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Element spec = doc.selectFirst("div[class=jobs-qualification]");
		Element pre = doc.selectFirst("div[class=jobs-skills]");
		Element applyUrl = doc.selectFirst("div[class=jobs-link] > a");
		job.setSpec(spec.text().trim());
		job.setPrerequisite(pre.text().split(":")[1].trim());
		job.setApplicationUrl(applyUrl.attr("href"));
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
	}

	@Override
	protected int getExpectedJob() {
		return expectedJobCount;
	}

	@Override
	protected void destroy() {
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}

}