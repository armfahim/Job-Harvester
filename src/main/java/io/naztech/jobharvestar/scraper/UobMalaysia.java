package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
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
 * United Overseas Bank Malaysia
 * URL: https://www.jobstreet.com.my/career/uobm.htm
 * 
 * @author Benajir Ullah
 * @author Rahat Ahmad
 * @author tanmoy.tushar
 * @since 2019-02-04
 */
@Slf4j
@Service
public class UobMalaysia extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UNITED_OVERSEAS_BANKING_GROUP_MALAYSIA;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private ChromeDriver driver;
	private WebDriverWait wait;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 20);
		startSiteScrapping(getSiteMetaData(ShortName.UNITED_OVERSEAS_BANKING_GROUP_MALAYSIA));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 28);
		driver.get(siteMeta.getUrl());
		int totalPages = getTotalPage();
		getSummaryPage(siteMeta);
		for (int i = 1; i < totalPages; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			List<WebElement> list = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class = 'rPaging']/a")));
			list.get(list.size()-1).click();
			Thread.sleep(RandomUtils.nextInt(TIME_4S, TIME_5S));
			try {
				getSummaryPage(siteMeta);
			} catch (Exception e) {
				log.warn("Exception on Job Summary Page of " + i, e);
			}
		}
	}

	private void getSummaryPage(SiteMetaData siteMeta) throws InterruptedException {
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table[@class = 'result']/tbody/tr")));
		for (WebElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			if (el.equals(jobList.get(0)) || el.equals(jobList.get(jobList.size() - 1))) continue;
			Job job = new Job(el.findElement(By.tagName("a")).getAttribute("href"));
			job.setTitle(el.findElement(By.tagName("a")).getText());
			job.setName(job.getTitle());
			job.setLocation(el.findElements(By.tagName("td")).get(2).getText());
			job.setCategory(el.findElements(By.tagName("td")).get(3).getText());
			try {
				saveJob(getJobDetail(job),siteMeta);					
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		job.setSpec(Jsoup.connect(job.getUrl()).userAgent("Mozilla").get().getElementById("job_description").text());
		return job;
	}

	private int getTotalPage() {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//form[@name='criteria']/section[1]/span[1]")));
		String totalJob = el.getText().split("of")[1].trim().split(Pattern.quote("job(s)"))[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 20);
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
