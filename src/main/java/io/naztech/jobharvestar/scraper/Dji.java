package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * DJI_innovation job parsing class<br>
 * URL: https://we.dji.com/jobs_en.html
 * 
 * @author Muhammad Bin Farook
 * @author tanmoy.tushar
 * @since 2019-03-19
 */
@Slf4j
@Service
public class Dji extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.DJI_INNOVATIONS;
	private static final String ROW_LIST = "//div[@class='list-group-item']/a";
	private static final String CHECK_DISABLE = "//div[@class='socnext disabled']";
	private static final String NEXT = "//div[@class='socnext']/a";
	private static WebClient CLIENT = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		CLIENT = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 19);
		HtmlPage page = CLIENT.getPage(siteMeta.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		List<String> joblink = new ArrayList<String>();
		while(true) {
			List<HtmlElement> div = page.getByXPath(ROW_LIST);
			for (HtmlElement el : div)
				if (el.getAttribute("href").contains("detail_en.html#"))
					joblink.add(getBaseUrl() + el.getAttribute("href").toString());
			HtmlElement el = page.getFirstByXPath(CHECK_DISABLE);
			if (el != null) break;
			try {
				HtmlElement next = page.getFirstByXPath(NEXT);
				page = next.click();
			} catch (Exception e) {
			}
			Thread.sleep(TIME_4S*2);
		}
		expectedJobCount = joblink.size();
		browseJobList(joblink, siteMeta);
	}

	private void browseJobList(List<String> allJobLink, SiteMetaData siteMeta) throws IOException {
		for (String link : allJobLink) {
			Job job = new Job(link);
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		HtmlPage page = CLIENT.getPage(job.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		job.setTitle(page.getElementsByTagName("h1").get(0).asText().trim());
		job.setName(job.getTitle());
		List<HtmlElement> el = page.getByXPath("//div[@class='col-xs-12 col-sm-4 mb20 col-sm-offset-2']/*");
		String str1 = el.get(1).asText();
		String[] str2 = str1.split("\n");
		job.setLocation(str2[1].trim());
		List<HtmlElement> ell = page.getByXPath("//div[@class='col-xs-12 col-sm-4 mb20']/*");
		String str3 = ell.get(1).asText();
		String[] str4 = str3.split("\n");
		job.setCategory(str4[1].trim());
		List<HtmlElement> des = page.getByXPath("//div[@class='well']/article");
		String spec = "";
		String req = "";
		for (HtmlElement li : des) {
			if (li.getElementsByTagName("h4").get(0).getTextContent().contains("Responsibilities: "))
				spec += li.asText().toString();
			else if (li.getElementsByTagName("h4").get(0).getTextContent().contains("Description: "))
				spec += li.asText().toString();
			else if (li.getElementsByTagName("h4").get(0).getTextContent().contains("Requirements: "))
				req += li.asText().toString() + "\n";
			else if (li.getElementsByTagName("h4").get(0).getTextContent().contains("Preferred: "))
				req += li.asText().toString();
		}
		job.setSpec(spec.trim());
		job.setPrerequisite(req.trim());
		job.setApplicationUrl(getBaseUrl() + page.getAnchorByText("Apply Now").getAttribute("href").trim());

		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}

	@Override
	protected int getExpectedJob() {
		return expectedJobCount;
	}

	@Override
	protected void destroy() {
		CLIENT.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
