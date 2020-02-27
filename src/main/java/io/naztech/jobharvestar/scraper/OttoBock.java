package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Otto Bock job site parser<br>
 * URL: https://stellenangebote.ottobock.de/cgi-bin/appl/selfservice.pl?action=search;page=
 * 
 * @author Armaan Seraj Choudhury
 * @author tanmoy.tushar
 * @since 2019-03-13
 */
@Service
@Slf4j
public class OttoBock extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.OTTO_BOCK_HEALTHCARE;
	private ChromeDriver driver;
	private WebDriverWait wait;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.OTTO_BOCK_HEALTHCARE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 15);
		driver.get(site.getUrl());
		List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//tr[@class='bordered_dashed']")));
		WebElement el = driver.findElement(By.xpath("//div[@class='frame_article']/p"));
		int totalPage = getTotalPage(el);
		browseJobList(rowList, site);
		for (int i = 2; i <= totalPage; i++) {
			String url = site.getUrl() + i;
			try {
				driver.get(url);
				rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//tr[@class='bordered_dashed']")));
				browseJobList(rowList, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}		
	}
	
	private void browseJobList(List<WebElement> rowList, SiteMetaData site) {
		for (WebElement el : rowList) {
			WebElement title = el.findElement(By.cssSelector("td>strong>a"));
			Job job = new Job(title.getAttribute("href"));
			job.setTitle(title.getText().trim());
			job.setName(job.getTitle());
			job.setCategory(el.findElement(By.cssSelector("td")).getText().replace(job.getTitle(), "").trim());
			job.setLocation(el.findElements(By.cssSelector("td")).get(1).getText().trim());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}
	
	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).timeout(TIME_10S).get();
		Elements jobInfo = doc.select("div[class=frame_article job]>ul");
		if (jobInfo.size() == 0) {
			job.setSpec(doc.selectFirst("div[class=frame_article job]").text().trim());
		}
		else if (jobInfo.size() == 1) {
			job.setSpec(jobInfo.get(0).text().trim());
		}
		else {
			job.setSpec(jobInfo.get(0).text().trim());
			job.setPrerequisite(jobInfo.get(1).text().trim());
		}		
		return job;
	}
	
	private int getTotalPage(WebElement el) {
		String totalJob = el.getText().trim().split(" ")[0].trim();
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
