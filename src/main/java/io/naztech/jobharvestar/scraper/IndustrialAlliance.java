package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Industrial Alliance jobs site parser <br>
 * URL: https://ia.ca/jobs/jobs-available
 * 
 * @author armaan.choudhury
 * @since 2019-02-18
 */
@Slf4j
@Service
public class IndustrialAlliance extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.INDUSTRIAL_ALLIANCE_INS;
	private static WebClient webClient;
	private String baseUrl;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.INDUSTRIAL_ALLIANCE_INS));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		webClient=getChromeClient();
		this.baseUrl = siteMeta.getUrl().substring(0, 13);
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(5000);
		page = getTotalPage(page);
		getSummaryPage(page, siteMeta);
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) throws InterruptedException {
		try {
			List<HtmlElement> rowList = page.getByXPath("//div[@class='job-list']/table/tbody/tr");
			expectedJobCount = rowList.size();
			System.out.println(expectedJobCount);
			for(HtmlElement row : rowList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(this.baseUrl + row.getElementsByTagName("th").get(0).getElementsByTagName("a").get(0).getAttribute("href"));
				job.setTitle(row.getElementsByTagName("th").get(0).getElementsByTagName("a").get(0).asText());
				job.setName(job.getTitle());
				job.setPostedDate(parseDate(row.getElementsByTagName("td").get(3).getTextContent(),DF));
				job.setCategory(row.getElementsByTagName("td").get(0).getTextContent());
				job.setLocation(row.getElementsByTagName("td").get(1).getTextContent());
				try {
					saveJob(getJobDetail(job), siteMeta);
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse job detail of " + job.getUrl(), e);
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			log.warn(" failed to parse summary page of " + getSiteName(), e);
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc=Jsoup.connect(job.getUrl()).timeout(TIME_10S).get();
		Element spec=doc.selectFirst("div[class=col-med-3-4 l-content-right]");
		job.setSpec(spec.text().trim());
		Element appUrl=doc.selectFirst("a[class=btn-trouver]");
		if(appUrl != null) job.setApplicationUrl(baseUrl+appUrl.attr("href"));
		return job;
	}

	private HtmlPage getTotalPage(HtmlPage page) throws IOException, InterruptedException {
		HtmlElement sec = page.getBody().getOneHtmlElementByAttribute("button", "id", "BoutonAfficherPlus");
		int total = Integer.parseInt(page.getBody().getOneHtmlElementByAttribute("input", "id", "emploisCompte")
				.getAttribute("value").trim());
		int totalClicks = total / 10;
		for (int i = 0; i < totalClicks; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			page = sec.click();
			webClient.waitForBackgroundJavaScript(5000);
		}
		return page;
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
		webClient.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}