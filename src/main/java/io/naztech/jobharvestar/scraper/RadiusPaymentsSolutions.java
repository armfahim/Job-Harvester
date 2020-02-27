package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * RadiusPaymentsSolutions<br>
 * URL: https://www.radiuspaymentsolutions.com/careers/opportunities/
 * 
 * @author Md. Sanowar Ali
 * @author bm.alamin
 * @author iftekar.alam
 * @since 28.03.2019
 *
 */
@Service
@Slf4j
public class RadiusPaymentsSolutions extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.RADIUS_PAYMENTS_SOLUTIONS;
	private ChromeDriver driver;
	private int expectedJobCount = 0;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		driver = getChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
		List<Job> jobList = new ArrayList<>();
		Document doc = Jsoup.connect(site.getUrl()).userAgent(USER_AGENT).timeout(TIME_10S).get();
		Elements jobElList = doc.select("div[class = single-job]");
		expectedJobCount = jobElList.size();
		log.info("Total job found: " + expectedJobCount);
		Job job=new Job();
		for (int i = 0; i < jobElList.size(); i++) {
			job.setUrl(jobElList.get(i).selectFirst("div[class = jobtl-col] > p > a").attr("href"));
			jobList.add(job);
			job.setTitle(jobElList.get(i).selectFirst("div[class = jobtl-col] > p").text().trim());
			job.setName(job.getTitle());
			job.setLocation(jobElList.get(i).selectFirst("div[class = jobloc-col]").text().trim());
			if(jobElList.get(i).selectFirst("p[class = job-department]") != null)
			job.setCategory(jobElList.get(i).selectFirst("p[class = job-department]").text().trim());
			if(jobElList.get(i).selectFirst("p[class = job-type]") != null)
			job.setType(jobElList.get(i).selectFirst("p[class = job-type]").text().trim());
			job.setApplicationUrl(jobElList.get(i).selectFirst("p[class = job-apply] >a").attr("href"));
		}
		for (Job job1 : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job1), site);					
			} catch(Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetails(Job job) throws InterruptedException  {
			driver.get(job.getUrl());
			Thread.sleep(TIME_4S);
			try {
				job.setSpec(driver.findElementByXPath("//div[@class = 'flex my-1 xs12']").getText().trim());
			} catch (Exception e) {
			}
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
		driver.quit();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
