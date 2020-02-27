package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
 * Swiss Life Holding Job Site Parser. <br>
 * URL: https://swisslife.prospective.ch/index.cfm?sprCd=en&wlgo=1&seq=
 * 
 * @author Rahat Ahmad
 * @author tanmoy.tushar
 * @since 2019-02-14
 */
@Slf4j
@Service
public class SwissLife extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SWISS_LIFE_HOLDING;
	private static final String ROW_LIST = "//div[@class='table_td funktion']/a";
	private String baseUrl;

	private ChromeDriver driver;
	private WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver(false);
		wait = new WebDriverWait(driver, 20);
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		List<String> jobUrl = new ArrayList<>();
		int i = 1;
		while(true) {
			driver.get(site.getUrl() + i );
			if (isStopped()) throw new PageScrapingInterruptedException();
			List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(ROW_LIST)));
			jobUrl.addAll(browseJobList(jobList));			
			if (checkForLastPage())	break;
			i++;
		}
		expectedJobCount = jobUrl.size();
		for (String url : jobUrl) {
			try {
				saveJob(getJobDetails(url), site);					
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + driver.getCurrentUrl(), e);
			}
		}
		driver.quit();
	}
	
	private List<String> browseJobList(List<WebElement> jobList) throws PageScrapingInterruptedException {
		List<String> jobUrl = new ArrayList<>();
		for (WebElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				String[] url = el.getAttribute("onclick").split("'");
				jobUrl.add(url[1]);
			} catch (Exception e) {
				log.warn("Failed to collect job url", e);
			}
		}
		return jobUrl;
	}

	private Job getJobDetails(String url) {
		Job job = new Job(url);
		driver.get(job.getUrl());
		job.setTitle(wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@id='stickyheaderUp']/div[1]/div[1]/h1"))).getText());
		job.setName(job.getTitle());
		job.setUrl(driver.getCurrentUrl());
		String spec = driver.findElement(By.xpath("//section[@id='mainContent']/div[1]/div[1]")).getText();
		spec = spec + driver.findElement(By.xpath("//section[@id='mainContent']/div[2]/div[1]/div[1]")).getText();
		spec = spec + driver.findElement(By.xpath("//section[@id='mainContent']/div[2]/div[1]/div[2]")).getText();
		job.setSpec(spec);
		return job;
	}

	private boolean checkForLastPage() {
		List<WebElement> pagination = driver.findElementsByXPath("//table[@class='backfor-table']/tbody/tr/td");
		String className = pagination.get(pagination.size() - 1).findElement(By.tagName("span")).getAttribute("class");
		if (className.equals("backfor-more-no"))
			return true;
		return false;
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
