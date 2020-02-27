package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Bank New York Mellon job site parser. <br>
 * URL: https://jobs.bnymellon.com/jobs?keywords=&page=1
 * 
 * @author tohedul.islum
 * @author tanmoy.tushar
 * @since 2019-01-18
 */
@Service
@Slf4j
public class BankNewYorkMellon extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.BANK_NEW_YORK_MELLON;
	private String baseUrl;
	private static final String TAILURL = "/jobs?keywords=&page=";
	private static final int JOBPERPAGE = 10;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception {
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 30);
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 26);
		driver.get(site.getUrl());
		int totalPage = getTotalPage();
		browseJobList(site);
		for (int i = 2; i <= totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + TAILURL + i;
			driver.get(url);
			try {
				browseJobList(site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
		driver.quit();
	}

	private void browseJobList(SiteMetaData siteMeta) throws InterruptedException {
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='job-title-link']")));
		for (WebElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(el.getAttribute("href"));
			job.setTitle(el.getText().trim());
			job.setName(job.getTitle());
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Element jobE = doc.selectFirst("article[itemprop=description]");
		job.setSpec(jobE.text().trim());
		jobE = doc.selectFirst("li[itemprop=jobLocation]");
		job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("li[itemprop=occupationalCategory]");
		job.setCategory(jobE.text().trim());
		jobE = doc.selectFirst("li[itemprop=reqId]");
		job.setReferenceId(jobE.text().trim());
		jobE = doc.selectFirst("a[class=apply __button btn-primary]");
		job.setApplicationUrl(getBaseUrl() + jobE.attr("href"));
		return job;
	}
	
	private int getTotalPage() {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h2[@id='search-results-indicator']")));
		String totalJob = el.getText().trim().split(" ")[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, JOBPERPAGE);
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
