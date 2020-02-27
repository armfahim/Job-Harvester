package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
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
 * LendingKart job site parser<br>
 * URL: https://www.lendingkart.com/careers/
 * 
 * @author arifur.rahman
 * @since 2019-03-24
 */
@Slf4j
@Service
public class LendingKaft extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.LENDINGKAFT;
	private WebClient client = null;
	private String baseUrl = "https://www.lendingkart.com/";
	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		client = getChromeClient();
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 40);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws MalformedURLException, IOException, InterruptedException {
		try {
			int i;
			Job job = new Job();
			HtmlPage page = client.getPage(siteMeta.getUrl());
			List<HtmlElement> jobList = page.getBody().getByXPath("//div[@class='rbox-opening-li']");
			List<HtmlElement> ob = page.getBody().getByXPath("//div[@class='rbox-job-shortdesc']");
			expectedJobCount = jobList.size();
			for (i = 0; i < jobList.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				job.setUrl(jobList.get(i).getElementsByTagName("a").get(0).getAttribute("href"));
				job.setTitle(jobList.get(i).getElementsByTagName("a").get(0).getTextContent());
				job.setName(job.getTitle());
				job.setType(jobList.get(i).getElementsByTagName("span").get(0).getTextContent());
				String loc[] = ob.get(i).getTextContent().split(":");
				job.setLocation(loc[1]);
				try {
					getJobDetails(job);
					saveJob(job, siteMeta);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			log.warn(getSiteName() + "-> Page Load Failed. Quiting..." + e);
			throw e;
		}
		driver.quit();
	}

	private Job getJobDetails(Job job) throws MalformedURLException, IOException, InterruptedException {
		try {
			driver.get(job.getUrl());
			Thread.sleep(5000);
			List<WebElement> spec = wait.until(
					ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='container-fluid']")));
			job.setSpec(spec.get(1).getText());
			return job;
		} catch (FailingHttpStatusCodeException e) {
			log.warn(" failed to parse detail page of" + job.getUrl(), e);
			return null;
		}
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
