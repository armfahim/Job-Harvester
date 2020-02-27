package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
 * UOB China<br>
 * URL: https://jobs.51job.com/all/co578360.html
 * 
 * @author tohedul.islum
 * @author rahat.ahmad
 * @since 2019-02-06
 */
@Service
@Slf4j
public class UobChina extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UNITED_OVERSEAS_BANKING_GROUP_CHINA;
	private String baseUrl;
	private ChromeDriver driver;
	private WebClient client = null;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		client = getFirefoxClient();
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);
		startSiteScrapping(getSiteMetaData(ShortName.UNITED_OVERSEAS_BANKING_GROUP_CHINA));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 22);
		driver.get(siteMeta.getUrl());
		while (true) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			getSummaryPage(siteMeta);
			try {
				driver.findElements(By.xpath("//li[@class='bk']")).get(1).findElement(By.tagName("a")).click();
				Thread.sleep(TIME_1S * 3);
			} catch (WebDriverException e) {
				break;
			}
		}
	}

	private void getSummaryPage(SiteMetaData siteMeta) throws InterruptedException {
		List<WebElement> rowList = driver.findElements(By.xpath("//div[@id='joblistdata']/div[@class='el']"));
		expectedJobCount += rowList.size();
		for (WebElement row : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(row.findElement(By.tagName("a")).getAttribute("href"));
			job.setTitle(row.findElement(By.tagName("a")).getText());
			job.setName(job.getTitle());
			job.setPrerequisite(row.findElements(By.tagName("span")).get(0).getText());
			job.setLocation(row.findElements(By.tagName("span")).get(1).getText());
			try {
				saveJob(getJobDetail(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) {

		try {
			HtmlPage page = client.getPage(job.getUrl());
			List<HtmlElement> specData = page.getByXPath("//div[@class='bmsg job_msg inbox']");
			job.setSpec(specData.get(0).asText());

		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn(SITE + " failed to parse job", e);
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
