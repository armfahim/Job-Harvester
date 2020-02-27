package io.naztech.jobharvestar.scraper.taleo;

import java.io.IOException;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * All job site of Taleo(Scroll) parsing class. <br>
 * <ul>
 * <li><a href="https://chm.tbe.taleo.net/chm03/ats/careers/v2/searchResults?org=OCHZIFF&cws=37">OchZiffCapitalMgmtGroup</a>
 * <li><a href="https://chp.tbe.taleo.net/chp03/ats/careers/v2/searchResults?org=FANA&cws=41">Fanatics</a>
 * <li><a href="https://chp.tbe.taleo.net/chp02/ats/careers/v2/searchResults?org=VARDE&cws=38">VardePartners</a>
 * </ul>
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-11
 */
@Service
public abstract class AbstractTaleoScroll extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static WebClient CLIENT = null;

	private static ChromeDriver driver;
	private static WebDriverWait wait;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		driver = getChromeDriver();
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled()) log.trace(site.getUrl());
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		driver.get(siteMeta.getUrl());
		wait = new WebDriverWait(driver, 50);
		wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.className("oracletaleocwsv2-accordion-head-info"), 0));
		List<WebElement> location;
		try {
			WebElement el = driver.findElement(By.className("oracletaleocwsv2-panel-number"));
			int totalJobs = Integer.parseInt(el.getText().trim());
			do {
				driver.executeScript("window.scrollBy(0,document.body.scrollHeight)");
				location = driver.findElements(By.className("oracletaleocwsv2-accordion-head-info"));
			} while (location.size() < totalJobs);
		} catch (NoSuchElementException e) {
			for (int i = 0; i < 500; i++) {
				driver.executeScript("window.scrollBy(0,document.body.scrollHeight)");
			}
			location = driver.findElements(By.className("oracletaleocwsv2-accordion-head-info"));
		}

		log.debug("Job found: "+location.size());
		List<WebElement> jobLink = driver.findElements(By.xpath("//div[@class='oracletaleocwsv2-accordion-head-info']/h4/a"));
		expectedJobCount = jobLink.size();
		for(int i=0; i<jobLink.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(jobLink.get(i).getAttribute("href"));
			job.setTitle(jobLink.get(i).getText());
			job.setName(job.getTitle());
			job.setLocation(location.get(i).findElement(By.tagName("div")).getText());
			try {
				saveJob(getJobDetail(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
		driver.quit();
	}
 
	protected Job getJobDetail(Job job){
		try {
		CLIENT = getFirefoxClient();
		HtmlPage page = CLIENT.getPage(job.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_4S);

		job.setSpec(page.getBody().getOneHtmlElementByAttribute("div", "class", "col-xs-12 col-sm-12 col-md-8").asText());
		job.setApplicationUrl(page.getBody().getOneHtmlElementByAttribute("a", "class", "btn btn-primary btn-block btn-lg oracletaleocwsv2-btn-fa btn-primary fa-check").getAttribute("href"));

		return job;
		}catch(ArrayIndexOutOfBoundsException | IOException | ElementNotFoundException e) {
			log.debug("Element Not Found in page: "+job.getUrl());
			return job;
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
