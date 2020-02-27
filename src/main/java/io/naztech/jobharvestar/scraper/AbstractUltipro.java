package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Abstract Scraper for recruiting.ultipro sites. <br>
 * Implementing class: PROTECTIVE LIFE CORPORATION, ANNALY CAPITAL MGMT
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-02-26
 */
@Service
public abstract class AbstractUltipro extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	protected ChromeDriver driver;
	protected WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled()) log.trace(site.getUrl());
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.MINUTES);
		wait = new WebDriverWait(driver, 15);
		driver.get(site.getUrl());
		int totalPage = getTotalPage();
		if (totalPage == 0) log.info("No job found, site exiting....");
		else {
			WebElement loadMoreE = driver.findElement(By.id("LoadMoreJobs"));
			for (int i = 1; i < totalPage; i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				loadMoreE.click();
				Thread.sleep(RandomUtils.nextInt(8000, 10000));
			}
			List<WebElement> rowList = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath("//a[@class='opportunity-link break-word']"), 0));
			browseJobList(rowList, site);
		}
		driver.quit();
	}
	
	private void browseJobList(List<WebElement> rowList, SiteMetaData site) throws PageScrapingInterruptedException {
		int rowListSize = rowList.size();
		if(rowListSize > getExpectedJob()) rowListSize = getExpectedJob();
		for (int i = 0; i < rowListSize; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetail(rowList.get(i).getAttribute("href")), site);					
			} catch(Exception e) {
				exception = e;
			}
		}
	}
	
	private int getTotalPage() {
		WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("count")));
		String totalJob = el.getText().split("of")[1].split("opportunities")[0].trim();
		expectedJobCount = Integer.parseInt(totalJob);
		return getPageCount(totalJob, 50);
	}

	protected Job getJobDetail(String url) {
		try (WebClient client = getChromeClient()) {
			HtmlPage page = client.getPage(url);
			client.waitForBackgroundJavaScript(8 * 1000);

			Job job = new Job(url);
			HtmlElement el = page.getBody().getOneHtmlElementByAttribute("h2", "class", "heading-with-subtitle");
			job.setTitle(el.getTextContent().trim());
			job.setName(job.getTitle());
			el = page.getBody().getOneHtmlElementByAttribute("span", "data-automation", "job-category");
			job.setCategory(el.getTextContent());
			el = page.getBody().getOneHtmlElementByAttribute("span", "data-automation", "requisition-number");
			job.setReferenceId(el.getTextContent());

			List<HtmlElement> dateE = page.getByXPath("//*[@id=\"opportunityDetailView\"]/div[2]/div/div/div/div[1]/ul/li");
			job.setType(dateE.get(1).getTextContent().trim());

			String dateVal = dateE.get(0).getElementsByTagName("ul").get(0).getElementsByTagName("li").get(0).getElementsByTagName("span").get(1).getTextContent();
			job.setPostedDate(parseDate(dateVal, DF1, DF2));

			el = page.getBody().getOneHtmlElementByAttribute("p", "class", "opportunity-description");
			job.setSpec(el.getTextContent());
			return job;
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.warn("Failed to get jobs details of " + url, e);
			return null;
		} catch (NullPointerException e) {
			log.warn("The given job is no longer available " + url);
			return null;
		}
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
