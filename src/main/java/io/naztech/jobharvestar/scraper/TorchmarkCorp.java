package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
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
 * TORCHMARK CORP job site parsing class. <br>
 * URL: https://www.torchmarkcorp.com/careers/
 * 
 * @author assaduzzaman.sohan
 * @author tanmoy.tushar
 * @since 2019-02-13
 */
@Slf4j
@Service
public class TorchmarkCorp extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TORCHMARK_CORP;
	private static WebClient CLIENT = null;
	private String baseUrl;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("M/dd/yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		CLIENT = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(ShortName.TORCHMARK_CORP));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 29);
		HtmlPage page = CLIENT.getPage(siteMeta.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		String allJobLink = siteMeta.getUrl()
				+ page.getBody().getOneHtmlElementByAttribute("div", "class", "col-md-9 col-sm-12")
						.getElementsByTagName("div").get(0).getElementsByTagName("p").get(0).getElementsByTagName("a")
						.get(0).getAttribute("href").substring(1).trim();
		page = CLIENT.getPage(allJobLink);
		CLIENT.waitForBackgroundJavaScript(TIME_5S * 3);
		getSummaryPage(page, siteMeta);
		CLIENT.close();
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) throws InterruptedException {
		List<HtmlElement> allJob = page.getByXPath("//a[@class= 'careerjobs ng-binding']");
		expectedJobCount = allJob.size();
		for (HtmlElement el : allJob) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = "";
			try {
				url = el.getAttribute("href").trim();
			} catch (ElementNotFoundException e) {
				continue;
			}
			Job job = new Job(baseUrl + "/careers/" + url);
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job details of " + job.getUrl(), e);
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = CLIENT.getPage(job.getUrl());
		HtmlElement jobE = page.getFirstByXPath("//div[@id='job-detail']");
		job.setTitle(jobE.asText());
		job.setName(job.getTitle());
		jobE = page.getFirstByXPath("//div[@class='job-detailnumber ng-binding']");
		job.setLocation(jobE.asText());
		jobE = page.getFirstByXPath("//div[@class='job-detailnumber2 ng-binding']");
		job.setReferenceId(jobE.asText().split(":")[1].trim());
		jobE = page.getFirstByXPath("//div[@class='jobBody']");
		job.setSpec(jobE.asText());
		jobE = page.getFirstByXPath("//div[@class='jobBody']/p[@class='ng-binding']");
		job.setPostedDate(parseDate(jobE.asText().substring(13).trim().split(" ")[0].trim(), DF, DF2));
		jobE = page.getFirstByXPath("//div[@class='aplly-info']/a");
		job.setApplicationUrl(jobE.getAttribute("href"));
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
