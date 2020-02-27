package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
 * Coins.ph job site Parser<br>
 * URL: https://content.coins.ph/careers/
 * 
 * @author iftekar.alam
 * @since 2019-04-01
 */
@Service
@Slf4j
public class CoinsPh extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.COINSPH;
	private String baseUrl;
	private static WebClient webClient = null;
	private WebDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 30);
		startSiteScrapping(getSiteMetaData(ShortName.COINSPH));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		webClient = getChromeClient();
		this.baseUrl = siteMeta.getUrl().substring(0, 33);
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		getSummaryPages(page, siteMeta);
		driver.quit();
	}

	private void getSummaryPages(HtmlPage page, SiteMetaData siteMeta) {
		try {
			Thread.sleep(10000);
			List<HtmlElement> jobList = page.getByXPath("//div[@class='rbox-opening-li']");
			expectedJobCount = jobList.size();
			for (HtmlElement li : jobList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				HtmlElement link = li.getElementsByTagName("a").get(0);
				Job job = new Job();
				job.setTitle(link.asText());
				job.setName(job.getTitle());
				job.setUrl(link.getAttribute("href"));
				try {
				  saveJob(getJobDetail(job), siteMeta);
				}catch(Exception e) {
					exception = e;
				}
			}
		} catch (InterruptedException | FailingHttpStatusCodeException e) {
			log.warn(" failed to parse summary page of " + getSiteName(), e);
		}
	}

	private Job getJobDetail(Job job) {
		try {
			driver.get(job.getUrl());
			String divId[] = job.getUrl().split("#");			
			WebElement jobE = wait.until(
					ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='" + divId[1] + "']/div[3]")));
			job.setType(jobE.getText());                                 
			
			WebElement spec = driver.findElement(By.xpath("//div[@id='" + divId[1] + "']/div[4]"));
			String speci = spec.getText();
			job.setSpec(speci);
			WebElement Loc = driver.findElement(By.xpath("//div[@id='" + divId[1] + "']/div[2]"));
			String Loca = Loc.getText();
			job.setLocation(Loca);
		} catch (FailingHttpStatusCodeException e) {
			log.warn(" failed to parse detail page of" + job.getUrl(), e);
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
		driver.quit();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
