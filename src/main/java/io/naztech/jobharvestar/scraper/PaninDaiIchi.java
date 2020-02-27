package io.naztech.jobharvestar.scraper;

import java.io.IOException;
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
 * Panin Dai Ichi <br>
 * URL: https://www.panindai-ichilife.co.id/en/karir-index/carrer
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-02-25
 */
@Slf4j
@Service
public class PaninDaiIchi extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.PANIN_DAI_ICHI;
	private static WebClient webClient = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.PANIN_DAI_ICHI));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		webClient = getFirefoxClient();
		this.baseUrl = siteMeta.getUrl();
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(5000);
		getSummaryPage(page, siteMeta);
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) throws InterruptedException {
		try {
			Job job = new Job();
			List<HtmlElement> row = page.getByXPath("//div[@class = 'list-box career_list after-clear']/div");
			expectedJobCount = row.size();
			for (int i = 0; i <= row.size() - 1; i++) {
				if (isStopped())
					throw new PageScrapingInterruptedException();
				job.setTitle(row.get(i).getElementsByTagName("div").get(2).getElementsByTagName("h6").get(0)
						.getTextContent());
				job.setName(row.get(i).getElementsByTagName("div").get(2).getElementsByTagName("h6").get(0)
						.getTextContent());
				job.setUrl(row.get(i).getAttribute("onclick").split("href=")[1].replace("'", ""));
				job.setLocation(row.get(i).getElementsByTagName("div").get(2).getElementsByTagName("p").get(0)
						.getTextContent());
				job.setReferenceId(row.get(i).getElementsByTagName("div").get(2).getElementsByTagName("span").get(0)
						.getTextContent());
				job.setApplicationUrl(row.get(i).getElementsByTagName("a").get(0).getAttribute("href"));
				try {
					saveJob(getJobDetail(job), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			log.error(SITE + "Exception on Job Summary Page" + e);
		}
	}

	private Job getJobDetail(Job job) {
		HtmlPage page;
		try {
			page = webClient.getPage(job.getUrl());
			webClient.waitForBackgroundJavaScript(5000);
			HtmlElement desEl = page.getBody().getOneHtmlElementByAttribute("div", "class",
					"inner-content product-detail");
			job.setSpec(desEl.getTextContent().trim());
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error(SITE + "Exception on Job Detail Page" + e);
		}
		return null;
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