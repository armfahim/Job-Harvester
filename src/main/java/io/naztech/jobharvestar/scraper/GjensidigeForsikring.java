package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
 * Gjensidige Forsikring job site parser<br>
 * URL: https://www.gjensidige.no/group/job/vacancies
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-02-14
 */
@Slf4j
@Service
public class GjensidigeForsikring extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.GJENSIDIGE_FORSIKRING;
	private static WebClient webClient = null;
	private String baseUrl;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("d.MM.yyyy");
	private static DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("dd.M.yyyy");
	private static DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("d.M.yyyy");
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 19);
		webClient = getFirefoxClient();
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		webClient.waitForBackgroundJavaScript(TIME_10S);
		try {
			getSummaryPage(page, siteMeta);
		} catch (Exception e) {
			log.warn("Failed to parse list of "+siteMeta.getUrl(),e);
		}
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) throws InterruptedException{
			List<HtmlElement> row = page.getByXPath("//ul[@class = 'normal']/li");
			expectedJobCount = row.size();
			for (int i = 0; i < row.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(row.get(i).getElementsByTagName("a").get(0).getAttribute("href"));
				job.setTitle(row.get(i).getElementsByTagName("a").get(0).getTextContent());
				job.setName(row.get(i).getElementsByTagName("a").get(0).getTextContent());
				job.setLocation(row.get(i).getChildNodes().get(2).asText());
				job.setDeadline(parseDate(row.get(i).getChildNodes().get(0).asText(),DF1,DF,DF2,DF3));
				try {
				saveJob(getJobDetail(job),siteMeta);
				}catch(Exception e) {
					exception = e;
					log.warn("Failed to parse details of "+ job.getUrl(),e);
				}
			}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setSpec(doc.selectFirst("div[id=AdvertisementInnerContent]").text().trim());
		job.setCategory(doc.select("div[class=row]").text().split("Avdeling")[1].trim().split("Arbejdssted")[0].trim());
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
		webClient.close();
	}


	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
