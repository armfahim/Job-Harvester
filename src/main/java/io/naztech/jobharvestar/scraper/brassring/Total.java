package io.naztech.jobharvestar.scraper.brassring;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;

/**
 * Peoples United Financial jobs site parse. <br>
 * URL:https://krb-sjobs.brassring.com/TGnewUI/Search/Home/Home?partnerid=30080&siteid=6558#Language=All&keyWordSearch=
 * 
 * @author tanmoy.tushar
 * @since 2019-03-06
 */
@Service
public class Total extends AbstractBrassring {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String SITE = ShortName.TOTAL;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("dd-MMM-yyyy").withLocale(Locale.GERMAN);
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("dd-MMMM-yyyy").withLocale(Locale.GERMAN);
	private static final DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private static final DateTimeFormatter DF4 = DateTimeFormatter.ofPattern("dd-MMM-yyyy").withLocale(Locale.forLanguageTag("pt-PT"));
	private static final DateTimeFormatter DF5 = DateTimeFormatter.ofPattern("dd-MMMM-yyyy").withLocale(Locale.FRENCH);
	private static final DateTimeFormatter DF6 = DateTimeFormatter.ofPattern("dd-MMM-yyyy").withLocale(Locale.FRENCH);
	private static final DateTimeFormatter DF7 = DateTimeFormatter.ofPattern("dd-M-yyyy");

	@Override
	protected Job getJobDetails(String jobUrl, ChromeDriver driver, WebDriverWait wait) {
		try {
			driver.get(jobUrl);
			Job job = new Job(jobUrl);
			List<WebElement> jobDList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='questionClass']/div")));
			if (jobDList.size() > 4) {
				String postDate = jobDList.get(0).getText().trim();
				if (postDate.contains(" 月")) job.setPostedDate(parseDate(postDate.replace(" 月", ""), DF3, DF7, DF));
				else if (postDate.contains("Août")) job.setPostedDate(parseDate(postDate.toLowerCase(), DF5, DF6));
				else if (postDate.contains("Fev") || postDate.contains("Mai") || postDate.contains("Ago")|| postDate.contains("Out") || postDate.contains("Set") || postDate.contains("Dez"))
					job.setPostedDate(parseDate(postDate.toLowerCase(), DF4));
				else job.setPostedDate(parseDate(postDate, DF, DF1, DF2, DF3, DF4, DF5, DF6, DF7));
				job.setTitle(jobDList.get(1).getText().trim());
				job.setName(job.getTitle());
				job.setCategory(jobDList.get(2).getText().trim());
				job.setLocation(jobDList.get(4).getText().trim());
			}
			WebElement jobSpecE = driver.findElement(By.xpath("//div[@class='questionClass']"));
			if (jobSpecE != null) job.setSpec(jobSpecE.getText());
			return job;
		} catch (TimeoutException e) {
			log.warn("Failed to parse job details of " + jobUrl, e);
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
}