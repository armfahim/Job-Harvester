package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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
 * Admiral Group Job site Parser.<br>
 * URL: https://admiraljobs.co.uk/vacancies/
 * 
 * @author Mahmud Rana
 * @since 2019-02-26
 * 
 * @author tanmoy.tushar
 * @since 2019-04-28
 */
@Service
@Slf4j
public class AdmiralGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.ADMIRAL_GROUP;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 15);
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		try {
			driver.get(siteMeta.getUrl());
			WebElement srchBtn = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("search_submit")));
			srchBtn.click();
			Thread.sleep(RandomUtils.nextInt(TIME_1S * 3, TIME_5S));
			List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//h3[@class='vacancy_title']/a")));
			expectedJobCount = rowList.size();
			for (WebElement row : rowList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetail(row.getAttribute("href")), siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (TimeoutException e) {
			log.warn("Failed to parse job list", e);
			throw e;
		} finally {
			driver.quit();
		}
	}

	private Job getJobDetail(String url) {
		try {
			Document doc = Jsoup.connect(url).get();
			Job job = new Job(url);
			Element jobE = doc.selectFirst("h2");
			job.setTitle(jobE.text().trim());
			job.setName(job.getTitle());
			
			jobE = doc.selectFirst("dd[class=value_Location]");
			if (jobE != null) job.setLocation(jobE.text().trim());
			jobE = doc.selectFirst("dd[class=value_Salary]");
			if (jobE != null) job.setCategory(jobE.text().trim());
			jobE = doc.selectFirst("dd[class=value_Business_Role_Type]");
			if (jobE != null) job.setType(jobE.text().trim());
			jobE = doc.selectFirst("dd[class=value_Role_Start_Date]");
			if (jobE != null) job.setPostedDate(parseDate(jobE.text().trim(), DF));
			
			jobE = doc.selectFirst("dl[class=vacancy_description]");
			job.setSpec(jobE.text().trim());
			jobE = doc.selectFirst("a[class=button-link button-link-white-red-text mini]");
			job.setApplicationUrl(jobE.attr("href"));
			return job;
		} catch (IOException e) {
			log.warn("Failed to parse job details of " + url);
		}
		return null;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
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
