package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
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
 * TECH MAHINDRA <br>
 * URL: https://careers.airbnb.com/positions/
 * 
 * @author iftekar.alam
 * @since 2019-10-20
 *
 */
@Slf4j
@Service
public class TechMahindha extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.TECH_MAHINDRA;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("d/MM/yyyy");
	private ChromeDriver driver;
	private WebDriverWait wait;

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(100, TimeUnit.SECONDS);
		wait = new WebDriverWait(driver, 50);
		driver.get(siteMeta.getUrl());
		this.baseUrl = siteMeta.getUrl().substring(0, 33);		
		int totalPage = getTotalPage();
		for (int i = 0; i < totalPage; i++) {
		if (isStopped()) throw new PageScrapingInterruptedException();
		List<WebElement> jobList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//table[@id='example']/tbody/tr")));
		for (WebElement el : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				Job job = new Job(el.findElements(By.tagName("td")).get(7).findElements(By.tagName("a")).get(0).getAttribute("href"));
				try {
					saveJob(getJobDetails(job), siteMeta);
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse job detail of " + job.getUrl(), e);
				}
			} catch (Exception e) {
				log.warn("Failed to parse list of ", siteMeta.getUrl());
			}
		}
		if (i == totalPage - 1) break;
		WebElement click = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@class='paginate_button next']")));
		click.click();
		Thread.sleep(TIME_10S);
	}
		

	}

	private int getTotalPage() throws IOException, InterruptedException {
		WebElement totalJob = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@class='dataTables_info']")));
		String totalJ = totalJob.getText().split("of")[1].split("entries")[0].trim();
		expectedJobCount = Integer.parseInt(totalJ);
		return getPageCount(totalJ, 10);
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Elements jobE = doc.select("div[class=row]");
		job.setTitle(jobE.get(0).select("div>label").text());
		job.setName(job.getTitle());
		job.setLocation(jobE.get(4).getElementsByTag("div").get(2).text().split("\\[")[0].trim());
		job.setPostedDate(parseDate(jobE.get(6).getElementsByTag("div").get(2).text().trim(), DF, DF1));
		job.setCategory(jobE.get(5).getElementsByTag("div").get(2).text().trim());
		job.setSpec(jobE.get(8).text().trim());
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
		driver.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
