package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Sofi job site scraper. <br>
 * URL: https://www.sofi.com/careers/
 * 
 * @author Shadman Shahriar
 * @author tanmoy.tushar
 * @since 2019-03-14
 */
@Slf4j
@Service
public class Sofi extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SOCIAL_FINANCE_SOFI;
	private static final String jobApplyHeadUrl = "https://jobs.jobvite.com";
	private ChromeDriver driver;
	private WebDriverWait wait;
	private WebClient webClient;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		wait = new WebDriverWait(driver, 15);
		webClient = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver.get(siteMeta.getUrl());
		try {
			List<WebElement> rowList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='listings']/div/a")));
			expectedJobCount = rowList.size();
			browseJobList(rowList, siteMeta);
		} catch (Exception e) {
			log.error("Failed to parse job. Site exiting...", e);
			throw e;
		}
	}

	private void browseJobList(List<WebElement> rowList, SiteMetaData siteMeta) throws InterruptedException {
		for (WebElement row : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(row.getAttribute("data-link"));
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job details of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws FailingHttpStatusCodeException, IOException {
		HtmlPage page = webClient.getPage(job.getUrl());
		webClient.getOptions().setJavaScriptEnabled(false);
		List<DomElement> detailsPage = page.getByXPath("//div[@class='jv-wrapper']");
		String[] part = detailsPage.get(1).getElementsByTagName("p").get(0).getTextContent().trim().split("\n");
		String location = "";
		for (int j = 1; j < part.length; j++)
			location += part[j].trim() + " ";
		job.setApplicationUrl(jobApplyHeadUrl
				+ page.getBody().getElementsByAttribute("a", "class", "jv-button jv-button-primary jv-button-apply")
						.get(0).getAttribute("href"));
		job.setTitle(detailsPage.get(1).getElementsByTagName("h2").get(0).getTextContent().trim());
		job.setName(job.getTitle());
		job.setCategory(part[0].trim());
		job.setLocation(location);
		job.setSpec(page.getBody().getOneHtmlElementByAttribute("div", "class", "jv-job-detail-description").asText()
				.trim());
		return job;
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
		webClient.close();
		driver.quit();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
