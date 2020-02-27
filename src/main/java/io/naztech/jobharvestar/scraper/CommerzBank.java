package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
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
 * Commerz Bank Job site Parser</a> URL:
 * https://jobs.commerzbank.com/?ac=start&language=2
 * 
 * @author Mahmud Rana
 * @author BM Al-Amin
 * @author tanmoy.tushar
 * @since 2019-01-30
 */
@Slf4j
@Service
public class CommerzBank extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.COMMERZBANK;
	private static final String JOB_ROW_EL_PATH = "//tbody[@class='jb-dt-list-body']/tr";
	private static final String JOB_TITLE_EL_PATH = JOB_ROW_EL_PATH + "/td[@class='job']/a";
	private static final String JOB_LOC_EL_PATH = JOB_ROW_EL_PATH + "/td[@class='']/span";
	private static final String JOB_CATEGORY_EL_PATH = JOB_ROW_EL_PATH + "/td[@class='hidden-xs']/span";
	private static final String JOB_DATE_EL_PATH = JOB_ROW_EL_PATH + "/td[@class='hidden-xs']/time";
	private static final String NEXT_BTN_EL = "//a[@class='next']";

	private DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private WebClient client;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		client = getFirefoxClient();
		client.getOptions().setTimeout(TIME_1M);
		client.getOptions().setUseInsecureSSL(true);
		client.setJavaScriptTimeout(TIME_1M);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		HtmlPage page = client.getPage(site.getUrl());
		client.waitForBackgroundJavaScript(TIME_1M);
		HtmlElement nextBtn = page.getBody().getFirstByXPath(NEXT_BTN_EL);
		int totalPage = getTotalPage(page);
		for (int i = 0; i < totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			int rowSize = page.getBody().getByXPath(JOB_ROW_EL_PATH).size();
			for (int j = 0; j < rowSize; j++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					browseJobList(page, j, site);
				} catch (Exception e) {
					log.warn("Failed to parse a job from job List");
				}
			}
			if (i == totalPage - 1)	break;
			nextBtn = page.getBody().getFirstByXPath(NEXT_BTN_EL);
			page = nextBtn.click();
			Thread.sleep(RandomUtils.nextInt(3200, 4000));
			client.waitForBackgroundJavaScript(3 * TIME_10S);
		}
	}

	private void browseJobList(HtmlPage page, int j, SiteMetaData site) {
		HtmlElement el = (HtmlElement) page.getBody().getByXPath(JOB_TITLE_EL_PATH).get(j);
		Job job = new Job(el.getAttribute("href"));
		job.setTitle(el.getTextContent().trim());
		job.setName(job.getTitle());
		el = (HtmlElement) page.getBody().getByXPath(JOB_LOC_EL_PATH).get(j);
		job.setLocation(el.getTextContent());
		el = (HtmlElement) page.getBody().getByXPath(JOB_CATEGORY_EL_PATH).get(j);
		job.setCategory(el.getTextContent());
		el = (HtmlElement) page.getBody().getByXPath(JOB_DATE_EL_PATH).get(j);
		job.setPostedDate(parseDate(el.getTextContent(), DF));
		job.setReferenceId(job.getTitle().substring(0, 4) + RandomUtils.nextInt(2456, 333256));
		try {
			saveJob(getJobDetail(job), site);
		} catch (Exception e) {
			exception = e;
			log.warn("Failed to parse job detail of " + job.getUrl(), e);
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		job.setSpec(Jsoup.connect(job.getUrl()).get().getElementById("jobad").text().trim());
		return job;
	}

	private int getTotalPage(HtmlPage page) {
		HtmlElement el = page.getFirstByXPath("//h1[@class='col-xs-12']/span");
		String totalJob = el.asText().split(" ")[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 10);
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
