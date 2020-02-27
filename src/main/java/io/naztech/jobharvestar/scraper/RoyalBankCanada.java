package io.naztech.jobharvestar.scraper;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
 * ROYAL BANK OF CANADA job site parsing class. <br>
 * URL: https://jobs.rbc.com/ca/en/search-results?keywords=
 * 
 * @author assaduzzaman.sohan
 * @author tanmoy.tushar
 * @since 2019-03-10
 */
@Slf4j
@Service
public class RoyalBankCanada extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ROYAL_BANK_OF_CANADA;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static WebClient CLIENT = null;
	protected WebDriverWait wait;
	protected ChromeDriver driver;
	private int expectedJobCount;
	private String baseUrl;
	private Exception exception;
	private static final int JOBS_PER_PAGE = 50;

	@Override
	public void scrapJobs() throws Exception {
		CLIENT = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		this.baseUrl = siteMeta.getUrl().substring(0, 20);
		HtmlPage page = CLIENT.getPage(siteMeta.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_1M);
		int totalPage = getTotalPage(page);
		List<HtmlElement> jobList = page.getBody().getElementsByAttribute("a", "ph-tevent", "job_click");
		browseJobList(jobList, siteMeta);
		for (int i = 1; i < totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			HtmlElement el = page.getBody().getOneHtmlElementByAttribute("a", "data-ph-at-id", "pagination-next-link");
			String url = el.getAttribute("href");
			try {
				page = CLIENT.getPage(url);
				CLIENT.waitForBackgroundJavaScript(TIME_1M);
				jobList = page.getBody().getElementsByAttribute("a", "ph-tevent", "job_click");
				browseJobList(jobList, siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private int getTotalPage(HtmlPage page) throws IOException {
		String totalJob = page.getBody().getOneHtmlElementByAttribute("span", "class", "result-count").asText().trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, JOBS_PER_PAGE);
	}

	private void browseJobList(List<HtmlElement> jobList, SiteMetaData site) throws IOException, InterruptedException {
		for (HtmlElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.getAttribute("href"));
			job.setTitle(el.asText().trim());
			job.setName(job.getTitle());
			job.setReferenceId(el.getAttribute("data-ph-at-job-id-text"));
			job.setLocation(el.getAttribute("data-ph-at-job-location-text"));
			job.setCategory(el.getAttribute("data-ph-at-job-category-text"));
			job.setType(el.getAttribute("data-ph-at-job-type-text"));
			job.setPostedDate(parseDate(el.getAttribute("data-ph-at-job-post-date-text").substring(0, 10), DF));
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		driver.get(job.getUrl());
		wait = new WebDriverWait(driver, 90);
		wait.until(presenceOfElementLocated(By.className("job-description")));
		job.setSpec(driver.findElement(By.className("job-description")).getText().trim());
		WebElement nextE=wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@data-ph-id='ph-page-element-page36-as0OPa']")));
		job.setDeadline(parseDate(nextE.getText(), DF1));
		System.out.println(job.getDeadline());
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
		driver.quit();
		CLIENT.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
