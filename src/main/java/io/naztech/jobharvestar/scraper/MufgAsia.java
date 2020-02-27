package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

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
 * MUFG Asia<br>
 * URL: https://mufgcareers.resourcesolutions.com/gold/iapply/index.cfm?event=jobs.search
 * 
 * @author Benajir Ullah
 * @author tanmoy.tushar
 * @since 2019-01-27
 */
@Slf4j
@Service
public class MufgAsia extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MUFG_ASIA;
	private String baseUrl;
	private static WebClient web = null;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private int expectedJobCount;
	private Exception exception;
	private int maxRetry = 0;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		web = getChromeClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 54);
		HtmlPage pageH = web.getPage(siteMeta.getUrl());
		pageH = pageH.getHtmlElementById("Search").click();
		Thread.sleep(RandomUtils.nextInt(TIME_1S * 2, TIME_5S));
		List<HtmlElement> list = pageH.getByXPath("//table[@id = 'jobSearchResult']/tbody/tr");
		expectedJobCount = list.size();
		for (HtmlElement el : list) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.getElementsByTagName("td").get(0).getElementsByTagName("a").get(0).getAttribute("href"));
			job.setTitle(el.getElementsByTagName("td").get(1).getTextContent());
			job.setName(job.getTitle());
			job.setReferenceId(el.getElementsByTagName("td").get(0).getElementsByTagName("a").get(0).getTextContent());
			job.setCategory(el.getElementsByTagName("td").get(2).getTextContent());
			job.setType(el.getElementsByTagName("td").get(3).getTextContent());
			job.setLocation(el.getElementsByTagName("td").get(4).getTextContent());
			job.setPostedDate(parseDate(el.getElementsByTagName("td").get(5).getTextContent(), DF));
			try {
				saveJob(getJobDetail(job), siteMeta);
			}/**
			 * Sometime detail page got SocketTimeoutException. But If reload the page in
			 * browser, then it's working. To handle socketTimeoutException ,blindly reload
			 * this page 3 times.
			 */
			catch (SocketTimeoutException e) {
				maxRetry++;
				if (maxRetry < 3) {
					log.info("Failed to load the detail page.. Reloading once again... " + job.getUrl(), e);
					saveJob(getJobDetail(job), siteMeta);
				} else {
					log.warn("Failed to parse job detail of " + job.getUrl(), e);
				}
			}catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc=Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		try {
			job.setSpec(doc.selectFirst("div[class=WordSection1]").text().trim());
		} catch (Exception e) {
			job.setSpec(doc.selectFirst("div[class=content]").text());
		}
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
		web.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
