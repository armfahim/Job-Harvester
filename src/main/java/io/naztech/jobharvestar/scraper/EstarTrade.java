package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
 * E* Trade Group.<br>
 * URL: https://www.etradecareers.com/job-search-results
 * 
 * @author Armaan Seraj Choudhury
 * @author jannatul.maowa
 * @since 2019-02-20
 */
@Slf4j
@Service
public class EstarTrade extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ESTAR_TRADE_GROUP;
	private String baseUrl;
	private static WebClient webClient = null;
	private static DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	private static DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		webClient = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(ShortName.ESTAR_TRADE_GROUP));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 29);
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(5000);
		int totalPages = getTotalPage(page);
		for (int i = 1; i <= totalPages; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = siteMeta.getUrl()+"/"+"?pg="+i;
			webClient.waitForBackgroundJavaScript(5000);
			getSummaryPage(url, siteMeta);
		}
	}

	private void getSummaryPage(String url, SiteMetaData siteMeta) throws InterruptedException, MalformedURLException, IOException {
		try {
			HtmlPage page = webClient.getPage(url);
			webClient.waitForBackgroundJavaScript(5000);
			Job job = new Job();
			List<HtmlElement> row = page.getByXPath("//*[@id=\"widget-jobsearch-results-list\"]/ol/li");
			for (int i = 0; i <= row.size() - 1; i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				job.setTitle(row.get(i).getElementsByTagName("ul").get(0).getElementsByTagName("li").get(0)
						.getElementsByTagName("div").get(0).getTextContent());
				job.setName(row.get(i).getElementsByTagName("ul").get(0).getElementsByTagName("li").get(0)
						.getElementsByTagName("div").get(0).getTextContent());
				job.setUrl(this.baseUrl + row.get(i).getElementsByTagName("ul").get(0).getElementsByTagName("li").get(0)
						.getElementsByTagName("div").get(0).getElementsByTagName("a").get(0).getAttribute("href"));
				job.setCategory(row.get(i).getElementsByTagName("ul").get(0).getElementsByTagName("li").get(1)
						.getTextContent());
				job.setLocation(row.get(i).getElementsByTagName("ul").get(0).getElementsByTagName("li").get(2)
						.getTextContent());
				try {
					saveJob(getJobDetail(job), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			log.warn("Exception on Job Summary Page" + e);
		}
	}

	private Job getJobDetail(Job job) {
		HtmlPage page;
		try {
			page = webClient.getPage(job.getUrl());
			webClient.waitForBackgroundJavaScript(5000);
			HtmlElement appEl = page.getBody().getOneHtmlElementByAttribute("div", "class", "jobdetail-buttons");
			job.setApplicationUrl(appEl.getElementsByTagName("a").get(0).getAttribute("href"));
			HtmlElement detailsEl = page.getBody().getOneHtmlElementByAttribute("div", "class", "jobdetail-info");
			job.setReferenceId(detailsEl.getElementsByTagName("span").get(3).getTextContent());
			job.setPostedDate(parseDate(detailsEl.getElementsByTagName("span").get(4).getTextContent(), DF1,DF2));
			HtmlElement desEl = page.getBody().getOneHtmlElementByAttribute("div", "class",
					"tabcontainer  top_tab   avia-builder-el-15  el_after_av_textblock  el_before_av_social_share  ");
			job.setSpec(desEl.getTextContent());
			HtmlElement prereqEl = page.getBody().getOneHtmlElementByAttribute("div", "class",
					"av-social-sharing-box  avia-builder-el-16  el_after_av_tab_container  el_before_av_sidebar ");
			job.setPrerequisite(prereqEl.getTextContent());

			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Exception on Job Detail Page" + e);
		}
		return null;
	}

	private int getTotalPage(HtmlPage page) {
		HtmlElement sec = page.getBody().getOneHtmlElementByAttribute("span", "id", "live-results-counter");
		String pageText = sec.getTextContent().trim();
		expectedJobCount = Integer.parseInt(pageText);
		return getPageCount(pageText, 10);
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