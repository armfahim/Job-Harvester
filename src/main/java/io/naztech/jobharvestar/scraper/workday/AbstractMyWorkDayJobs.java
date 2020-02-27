package io.naztech.jobharvestar.scraper.workday;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfAllElementsLocatedBy;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * All job site of MyWorkDayJobs parsing class. <br>
 * <ul>
 * <li><a href=
 * "https://statestreet.wd1.myworkdayjobs.com/Global">StateStreetCorp</a>
 * <li><a href="https://if.wd3.myworkdayjobs.com/Careers">SampoA</a>
 * <li><a href=
 * "https://salesforce.wd1.myworkdayjobs.com/External_Career_Site">SalesForce</a>
 * <li><a href=
 * "https://regions.wd5.myworkdayjobs.com/Regions_Careers">RegionsFinancial</a>
 * <li><a href=
 * "https://qbe.wd3.myworkdayjobs.com/QBE-Careers">QbeInsuranceGroup</a>
 * <li><a href=
 * "https://nvidia.wd5.myworkdayjobs.com/NVIDIAExternalCareerSite">Nvidia</a>
 * <li><a href=
 * "https://nasdaq.wd1.myworkdayjobs.com/US_External_Career_Site">NasDaqUS</a>
 * <li><a href=
 * "https://nasdaq.wd1.myworkdayjobs.com/en-US/Global_External_Site">NasDaqGlobal</a>
 * <li><a href="https://mtb.wd5.myworkdayjobs.com/MTB">MtBankCorp</a>
 * <li><a href=
 * "https://osv-momentapharma.wd1.myworkdayjobs.com/MomentaCareers">Momenta</a>
 * <li><a href=
 * "https://mizuho.wd1.myworkdayjobs.com/mizuhoamericas">MizuhoFinancialGroup</a>
 * <li><a href=
 * "https://mastercard.wd1.myworkdayjobs.com/CorporateCareers">Mastercard</a>
 * <li><a href=
 * "https://markelcorp.wd5.myworkdayjobs.com/GlobalCareers">MarkelCorp</a>
 * <li><a href=
 * "https://lseg.wd3.myworkdayjobs.com/LSEG_Careers">LondonStockExchange</a>
 * <li><a href=
 * "https://loewscorp.wd1.myworkdayjobs.com/loewscorp/3/refreshFacet/318c8bb6f553100021d223d9780d30be">LoewsCorp</a>
 * <li><a href=
 * "https://lbg.wd3.myworkdayjobs.com/lbg_Careers">LloydsBankingGroup</a>
 * <li><a href=
 * "https://juliusbaer.wd3.myworkdayjobs.com/en-US/External">JuliusBaerGroup</a>
 * <li><a href=
 * "https://eatonvance.wd5.myworkdayjobs.com/Professional">EatonVanceCorpNv</a>
 * <li><a href=
 * "https://dlg.wd3.myworkdayjobs.com/DLGCAREERS">DirectLineInsuranceGroup</a>
 * <li><a href=
 * "https://cylance.wd1.myworkdayjobs.com/ExternalSite/jobs">Cylance</a>
 * <li><a href=
 * "https://cboe.wd1.myworkdayjobs.com/External_Career_CBOE">CboeGlobalMarkets</a>
 * <li><a href="https://bbt.wd1.myworkdayjobs.com/Careers">BbtCorp</a>
 * <li><a href="https://aviva.wd1.myworkdayjobs.com/External">Aviva</a>
 * <li><a href=
 * "https://axiscapital.wd1.myworkdayjobs.com/axiscareers">AxisCapital</a>
 * <li><a href=
 * "https://athene.wd5.myworkdayjobs.com/athene_careers">AtheneHldgLtd</a>
 * <li><a href="https://aig.wd1.myworkdayjobs.com/aig">AmericanIntlGroup</a>
 * <li><a href=
 * "https://adobe.wd5.myworkdayjobs.com/external_experienced">AdobeSystems</a>
 * <li><a href=
 * "https://transamerica.wd5.myworkdayjobs.com/AUK_JobSite">Aegon</a>
 * <li><a href=
 * "https://sunlife.wd3.myworkdayjobs.com/Experienced-Jobs">SunLifeFinancial</a>
 * <li><a href=
 * "https://tal.wd3.myworkdayjobs.com/TAL-current-opportunities">TalDaiIchiAustralia</a>
 * <li><a href="https://unum.wd1.myworkdayjobs.com/External">UnumGroup</a>
 * <li><a href=
 * "https://godirect.wd5.myworkdayjobs.com/en-US/voya_jobs">VoyaFinancialWorkDays</a>
 * <li><a href=
 * "https://wellington.wd5.myworkdayjobs.com/External">WellingtonAlternativeInvestments</a>
 * <li><a href="https://modernatx.wd1.myworkdayjobs.com/M_tx">Moderna</a>
 * <li><a href=
 * "https://justfab.wd1.myworkdayjobs.com/justfabcareers">TechStyle</a>
 * <li><a href=
 * "https://smsassist.wd5.myworkdayjobs.com/SMSAssistcareers">SmsAssist</a>
 * </ul>
 * 
 * @author assaduzzaman.sohan
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @author jannatul.maowa
 * @since 2019-02-19
 */
@Service
public abstract class AbstractMyWorkDayJobs extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final String JOB_COUNT_EL_ID = "wd-FacetedSearchResultList-PaginationText-facetSearchResultList.newFacetSearch.Report_Entry";
	private static final String ROW_LIST_ID = "monikerList";
	private List<Integer> clickFailedJobElIndex;
	protected WebDriverWait wait;
	protected ChromeDriver driver;
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
		driver.manage().timeouts().pageLoadTimeout(7, TimeUnit.MINUTES);
		log.info("Page loading for more jobs, it will take time...");
		wait = new WebDriverWait(driver, 60);
		try {
			driver.get(site.getUrl());
			int totalJobs = getTotalJobs();
			expectedJobCount = totalJobs;
			List<WebElement> rowListE = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(ROW_LIST_ID)));
			do {
				if (isStopped()) throw new PageScrapingInterruptedException();
				driver.executeScript("window.scrollBy(0,document.body.scrollHeight)");
				rowListE = driver.findElements(By.id(ROW_LIST_ID));
				if(rowListE.isEmpty()) {
					log.warn("Job row list id chnaged");
					break;
				}
			} while (rowListE.size() < totalJobs);

			Set<String> links = prepareJobLinks(rowListE);
			for (String url : links) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				try {
					saveJob(getJobDetail(url), site);
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse job detail of " + url, e);
				}
			}
			for (Integer it : clickFailedJobElIndex) {
				driver.get(site.getUrl());
				List<WebElement> jobElList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(ROW_LIST_ID)));
				try {
					jobElList.get(it).click();
				} catch (Exception e) {
					JavascriptExecutor executor = (JavascriptExecutor) driver;
					executor.executeScript("arguments[0].click();", jobElList.get(it));
				}
				Thread.sleep(TIME_4S);
				try {
					saveJob(getJobDetail(null), site);
				} catch (Exception e) {
					exception = e;
					log.warn("Failed to parse job detail of " + driver.getCurrentUrl(), e);
				}
			}
		} catch (TimeoutException e) {
			log.error("Site Layout Changed", e);
			throw e;
		} finally {
			driver.quit();
		}
	}

	protected Set<String> prepareJobLinks(List<WebElement> rowListE) throws PageScrapingInterruptedException, InterruptedException {
		clickFailedJobElIndex = new ArrayList<>();
		Actions action = new Actions(driver);
		Set<String> links = new HashSet<>();
		for (int i = 0; i < rowListE.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			int count = 0;
			int flag = 0;
			while (flag == 0) {
				try {
					WebElement el = rowListE.get(i);
					action.moveToElement(el);
					action.contextClick().build().perform();
					Thread.sleep(TIME_1S);
					WebElement linkE = wait.until(presenceOfElementLocated(By.cssSelector("div[data-automation-id='copyUrl']")));
					if (linkE == null) continue;
					String url = linkE.getAttribute("data-clipboard-text");
					if(links.contains(url)) clickFailedJobElIndex.add(i);
					links.add(url);
					flag = 1;
				} catch (StaleElementReferenceException e) {
					count++;
					if (count == 3) {
						flag = 1;
					}
				}
			}
		}
		return links;
	}

	protected int getTotalJobs() {
		wait.until(presenceOfElementLocated(By.id(getJobCountElementId())));
		String[] part = driver.findElement(By.id(getJobCountElementId())).getText().split(" ");
		return Integer.parseInt(part[0].trim());
	}

	protected Job getJobDetail(String link) {
		if (link == null) link = driver.getCurrentUrl();
		else driver.get(link);
		wait.until(presenceOfAllElementsLocatedBy(By.cssSelector("button[title='Apply']")));
		List<WebElement> titleE = driver.findElementsByClassName("GWTCKEditor-Disabled");
		Job job = new Job(link);
		job.setTitle(titleE.get(0).getText());
		job.setName(job.getTitle());
		job.setSpec(titleE.get(1).getText());
		
		List<WebElement> list = wait.until(presenceOfAllElementsLocatedBy(By.xpath("//div[@data-automation-id='responsiveMonikerInput']")));
		job.setLocation(list.get(0).getText());
		for (WebElement el : list) {
			if (el == list.get(0)) continue; 
			if (el.getText().contains("Posted")) job.setPostedDate(parseAgoDates(el.getText().replace("Posted ", "").trim()));
			if (el.getText().contains("time")) job.setType(el.getText().trim());
		}
		String jobUrl = job.getUrl();
		if (jobUrl.contains("_") && getRefId(jobUrl) != null) job.setReferenceId(getRefId(jobUrl));
		return job;
	}
	
	private String getRefId(String ref) {
		String refId = "";
		String[] parts = ref.split("_");
		if (parts.length > 0) {
			refId = parts[parts.length - 1];
			if (refId.contains("-")) {
				String[] refParts = refId.split("-");
				if (refParts.length == 2 && refParts[1].length() == 1)
					return refId.replace("-" + refParts[1], "");
				if (refParts.length > 2 && refParts[refParts.length - 1].length() == 1)
					return refId.replace("-" + refParts[refParts.length - 1], "");
				else return refId;
			}
			else return refId;
		}
		return null;
	}
	
	/**
	 * Returns the element id of total job count. <br>
	 * Override and return separate element id when needed.
	 * 
	 * @return Element id of total job count
	 */
	protected String getJobCountElementId() {
		return JOB_COUNT_EL_ID;
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
