package io.naztech.jobharvestar.scraper;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PreDestroy;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.transaction.annotation.Transactional;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import io.naztech.jobharvestar.crawler.AbstractScraperLauncher;
import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.service.ActionType;
import io.naztech.jobharvestar.service.JobService;
import io.naztech.jobharvestar.utils.WordToNumbers;
import io.naztech.talent.dao.IssueRepository;
import io.naztech.talent.dao.JobRepository;
import io.naztech.talent.dao.OrganizationRepository;
import io.naztech.talent.dao.SiteMetaDataRepository;
import io.naztech.talent.model.Issue;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * Provides common functionality to web site {@link Scrapper} implementations.
 * 
 * @author Imtiaz Rahi
 * @author Mahmud Rana
 * @author Tanbirul Hashan
 * @author Md. Kamruzzaman
 * @author tanmoy.tushar
 * @since 2019-01-13
 */
public abstract class AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	protected static final int USER_ID = 100001;
	/** Time units in seconds. Values are defined in mili seconds */
	protected static final int TIME_1S = 1000, TIME_4S = 4000, TIME_5S = 5000, TIME_10S = 10 * TIME_1S;
	protected static final int TIME_1M = 60 * TIME_1S;
	
	private static final String SUC = "SUCCESSFUL";
	private static final String FLD = "FAILED";
	private static final String CRS = "CRASHED";
	private static final String BLD = "BLIND";
	private static final String INP = "IN-PROGRESS";
	private int runEventKey;
	
	/**
	 * Flag to denote whether scraper thread should be running or not. Set
	 * <code>false</code> by external force.
	 */
	private boolean runIt = false;
	
	private boolean usedChrome = false;


	/** Job parse count */
	private int jobCount = 0;

	@Autowired
	private Environment environment;
	@Autowired
	private AmqpTemplate amqp;

	@Value("${naztech.webscrapper.job-list-buffer:10}")
	private int jobListBufferSize;
	/** Whether job tagging (skill and location) is enabled in configuration */
	@Value("${naztech.webscrapper.enable-tagging:false}")
	private boolean isTaggingEnabled;
	/** Whether elastic search system is enabled or not in configuration */
	@Value("${naztech.webscrapper.enable-elasticsearch:true}")
	private boolean isElasticSearchEnabled;
	@Value("${selenium.webdriver.chrome-driver:webdrivers/chromedriver-76.exe}")
	protected String chromeExePath;

	@Autowired
	protected SiteMetaDataRepository siteRepo;
	@Autowired
	protected OrganizationRepository orgRepo;
	@Autowired
	protected JobService jobService;
	@Autowired
	private JobRepository jobRepo;
	@Autowired
	private Queue jobQueue;
	@Autowired
	private Exchange jobExchange;
	@Autowired
	private IssueRepository issueRepo;

	/**
	 * Site scraping operation entry point to general common tasks needed for all
	 * sites.
	 * 
	 * @param site {@link SiteMetaData} instance
	 */
	protected void startSiteScrapping(SiteMetaData site) {
		log.info("Launched scrapper: " + site.getComment() + " (" + site.getOrgShortName() + ")");
		boolean normalExit = true;
		Exception exp = null;

		try {
			startSiteRun(site);
			runIt = true;
			jobCount = 0;
			getScrapedJobs(site);
		} catch (PageScrapingInterruptedException e) {
			log.info(e.getMessage());
			normalExit = false;
			exp = e;
		} catch (Exception e) {
			log.error(site.getScraper() + " exited abnormally", e);
			normalExit = false;
			exp = e;
		} finally {
			finishSiteRun(getSiteName(), "OK. Job count: " + getJobCount(), normalExit, exp);
			calculateAvgParsedJobRuntime(site.getOrgShortName());
			destroy();
			runIt = false;
			if (normalExit) runSiteImp((long) runEventKey, site.getOrgShortName());

			if (normalExit) processWaiting();
			log.info("Exiting " + site.getScraper() + " ::: Parsed " + getJobCount() + " jobs");
			AbstractScraperLauncher.decrementCount();
		}
		
		Thread.currentThread().interrupt(); //TODO: CHECK IT CLEANS THE THREAD FROM THE POOL 
	}

	/**
	 * Update site run start time in {@link SiteMetaData} table with current time
	 * and empties out last run end time. <br>
	 * To be done when new site run has started.
	 * 
	 * @param site {@link SiteMetaData} instance
	 * @return Updated {@link SiteMetaData} instance
	 * @throws Exception 
	 */
	//TODO: Maintaining RunInfo is deprected (will be removed.)
	@Transactional("transactionManager")
	protected SiteMetaData startSiteRun(SiteMetaData site) throws Exception {
		site.setScraper(this.getClass().getSimpleName());
		site.setLastRunStart(LocalDateTime.now());
		site.setHost(getHostname());
		site.setParsedJobCount(0);
		site.setExpectedJobCount(0);
		site.setVersion(site.getVersion()+1);
		runEventKey = jobService.getRunEventKey(site.getOrgShortName());
		site.setRunEventKey(runEventKey);
		site.setSiteState(INP);
		site.setChromeUsed(getChromeUsed());
		return siteRepo.save(site);
	}

	private String getHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.warn("Hostname or IP was not found", e);
		}
		return "?";
	}
	
	private void processWaiting() {
		try {
			log.info("Thread is waiting for finalized...");
			Thread.sleep(RandomUtils.nextInt(TIME_1M, TIME_1M * 2));
		} catch (InterruptedException e) {
			log.warn("Failed to wait for the process terminate. ", e);
		}
	}
	
	/**
	 * Update site run end time in {@link SiteMetaData} table with current time and
	 * update status field. <br>
	 * To be done when site run has finished.
	 * 
	 * @param shortName Site short name
	 * @param status    Status to put in; e.g. Parsed 10 jobs.
	 * @return Updated {@link SiteMetaData} instance
	 */
	//TODO Refactor SiteMetaData Acquisition
	@Transactional("transactionManager")
	protected SiteMetaData finishSiteRun(String shortName, String status, boolean normalExit, Exception e) {
		
		if(isStopped()) return null;
		
		SiteMetaData site = getSiteMetaData(shortName);
		site.setLastRunEnd(LocalDateTime.now());
		if (site.getExpectedJobCount() != getExpectedJob()) site.setExpectedJobCount(getExpectedJob());
		if (getExpectedJob() < getJobCount()) site.setExpectedJobCount(getJobCount());
		site.setLastRunStatus(status);
		site.setLastRun(LocalDateTime.now());
		site.setParsedJobCount(getJobCount());
		String runStatus = getRunStatus(shortName, normalExit, e);
		site.setSiteState(runStatus);
		site.setVersion(site.getVersion()+1);
		site.setRunTime(ChronoUnit.SECONDS.between(site.getLastRunStart(), site.getLastRunEnd()));
		
		if( runStatus.equals(FLD) && getFailedException()!=null ) saveIssue(shortName, getFailedException());
		if( runStatus.equals(CRS) && e!=null ) saveIssue(shortName, e);

		return siteRepo.save(site);
	}

	/**
	 * Calculate Parsed and Expected Job and returns the final status of the latest run of the site scraper; 
	 * 
	 * @param shortName {@link String}
	 * @param normalExit {@link String}
	 * @param e {@link Exception}
	 * @return RunStatus {@link String} - The status of last Run
	 */
	private String getRunStatus(String shortName, boolean normalExit, Exception e) {
		int expectedJob = getExpectedJob();
		int parsedJob = getJobCount();
		if (expectedJob != 0 && parsedJob != 0 &&  parsedJob >= (expectedJob * 80) / 100) return SUC;
		else if (expectedJob == 0) return BLD;
		else if (!normalExit) return CRS;
		else return FLD;
	}

	private void saveIssue(String shortName, Exception e) {
		Issue issue = issueRepo.findByShortName(shortName);
		
		try {
			String issueDesc = ExceptionUtils.getStackTrace(e);
			String issueSumMsg = e.getLocalizedMessage();
			if (issue == null) issue = new Issue(shortName, issueSumMsg, issueDesc, this.getClass().getSimpleName());
			else {
				issue.setIssueSummaryMsg(issueSumMsg);
				issue.setIssue(issueDesc);
			}
		
			issueRepo.save(issue);
		} catch (Exception ex) {
			log.error(shortName+":: Error on saving issue.", ex);
		}
		
	}
	
	/**
	 * Returns the {@link SiteMetaData} instance containing configuration for the
	 * site.
	 * 
	 * @param site Site short name; e.g. JPM, BCL etc.
	 * @return {@link SiteMetaData} instance
	 */
	protected SiteMetaData getSiteMetaData(String site) {
		return siteRepo.findByOrgShortName(site);
	}

	/**
	 * Save {@link Job} instance into database.
	 * 
	 * @param ob   {@link Job} instance
	 * @param site {@link SiteMetaData} instance
	 * @return <code>true</code> when job saved successfully, <code>false</code>
	 *         otherwise
	 */
	protected boolean saveJob(Job job, SiteMetaData site) {
		if (isJobInvalid(job)) return false;
		incrementJobCount();

		job.setSiteMetaKey(site.getKey());
		job.setUserModId(USER_ID);
		job.onInsert();
		job.setOrgShortName(site.getOrgShortName());
		job.setRunEventKey(runEventKey);
		
		jobService.insert(job);
		
		if (getJobCount() % jobListBufferSize == 0) {
			if(updateOf(site) != null) log.info("Parsed and Saved " + getJobCount() + " jobs");
		}

		Job jobdb = jobRepo.findByUrl(job.getUrl());
		if (isTaggingEnabled) processJob(jobdb);
		return true;
	}

	private SiteMetaData updateOf(SiteMetaData site) {
		int expectedCount = getExpectedJob();
		if (expectedCount < getJobCount()) site.setExpectedJobCount(getJobCount());
		else site.setExpectedJobCount(expectedCount);
		site.setParsedJobCount(getJobCount());
		site.setRunTime(ChronoUnit.SECONDS.between(site.getLastRunStart(), LocalDateTime.now()));
		return siteRepo.save(site);
	}

	/**
	 * Send job to the job process queue
	 * 
	 * @param ob {@link Job} instance
	 * @return <code>true</code> when job is sent to {@code job-queue},
	 *         <code>false</code> otherwise
	 */
	private boolean processJob(Job ob) {
		try {
			amqp.convertAndSend(jobExchange.getName(), jobQueue.getName(), ob);
			return true;
		} catch (Exception e) {
			if (log.isTraceEnabled()) log.trace("Error sending Job "+ob.getJobId()+" of "+ob.getOrgShortName()+" is failed to be sent to Queue");
			return false;
		}
	}

	/**
	 * Returns whether a {@link Job} instance is invalid or not. <br>
	 * Every job instance must contain the permanent URL of the job details in the
	 * {@link Job#getUrl()} attribute. <br>
	 * Store calculated hash code using {@link #getJobHash(Job)}, if job details
	 * permanent URL is not available.
	 * 
	 * @param ob {@link Job} instance
	 * @return <code>true</code> if {@link Job} is invalid, <code>false</code> it
	 *         contains all required items
	 */
	protected boolean isJobInvalid(Job ob) {
		return ob == null || StringUtils.isEmpty(ob.getUrl()) || StringUtils.isEmpty(ob.getTitle())
				|| StringUtils.isEmpty(ob.getSpec());
	}

	/**
	 * 
	 * @param key siteId
	 * @param id  requestId
	 * @throws Exception
	 */
	private void runSiteImp(Long runEventKey, String orgShortName) {
		Map<String, Object> arg = new HashMap<>();
		arg.put("@id_run_event_key", runEventKey);
		arg.put("@tx_org_short_name", orgShortName);

		try {
			jobService.action(arg, ActionType.EMPTY.toString());
		} catch (Exception e) {
			log.error("Error on running IMP for site: "+orgShortName+", RunEventKey: "+runEventKey);
		}
	}
	

	private void calculateAvgParsedJobRuntime(String orgShortName) {
		Map<String, Object> arg = new HashMap<>();
		arg.put("@tx_org_short_name", orgShortName);
		
		try {
			jobService.actionCalAvg(arg, ActionType.EMPTY.toString());
		} catch(Exception e) {
			log.error("Error on running CAL_avg_parsedjob_runtime for site: "+orgShortName+", RunEventKey: "+runEventKey);
		}
	}
	/**
	 * Returns page count from total job count and jobs per page.
	 * 
	 * @param jobCount   Job count as string
	 * @param jobPerPage Number of jobs per list page
	 * @return Page count
	 */
	public int getPageCount(String jobCount, int jobPerPage) {
		if (StringUtils.isBlank(jobCount))
			return 0;
		try {
			int totalJobs = Integer.parseInt(jobCount);
			int totalPage = totalJobs / jobPerPage;
			return totalJobs % jobPerPage > 0 ? ++totalPage : totalPage;
		} catch (NumberFormatException e) {
			log.warn("{} failed to parse job count from value {}", getSiteName(), jobCount);
		}
		return 0;
	}

	/**
	 * Calculates MD5 digest created from few attributes of {@link Job} and returns
	 * the value as a 32 character hex string.
	 * 
	 * @param ob {@link Job} instance
	 * @return 32 character hex string of the MD5 digest
	 */
	protected String getJobHash(Job ob) {
		String str = ob.getOrgShortName() + ob.getTitle() + ob.getLocation() + ob.getType() + ob.getReferenceId()
				+ ob.getPostedDate() + ob.getSpec();
		return DigestUtils.md5Hex(str).toUpperCase();
	}

	/**
	 * Returns {@link LocalDate} instance from the given date formats.
	 * 
	 * @param val     String value of date
	 * @param formats Expected {@link DateTimeFormatter} formats
	 * @return {@link LocalDate} instance
	 */
	protected LocalDate parseDate(String val, DateTimeFormatter... formats) {
		for (DateTimeFormatter fmt : formats) {
			LocalDate ob = parseDate(val, fmt);
			if (ob != null)
				return ob;
		}
		log.warn(getSiteName() + " failed to parse date " + val);
		return null;
	}

	private LocalDate parseDate(String val, DateTimeFormatter df) {
		try {
			return LocalDate.parse(val, df);
		} catch (DateTimeParseException e) {
			// NOTE: do not put log here. intentionally left blank
		}
		return null;
	}

	/**
	 * Returns {@link LocalDate} instance from string value. <br>
	 * e.g.
	 * 
	 * <pre>
	 * "one day ago", "1 day ago", "Two days ago", "two day ago", "2 days ago", "Seven days ago",
	 * "one month ago", "1 month ago", "Two months ago", "two month ago", "2 months ago", "Seven months ago"
	 * </pre>
	 * 
	 * @param val String to convert
	 * @return {@link LocalDate} instance
	 */
	protected LocalDate parseAgoDates(String val) {
		if (StringUtils.isBlank(val))
			return null;
		val = val.toLowerCase();

		LocalDate now = LocalDate.now();
		if ("today".equals(val))
			return now;
		if ("yesterday".equals(val))
			return now.minusDays(1);

		val = val.replace("ago", "").trim();
		String[] parts = val.split(" ");
		parts[0] = parts[0].replace("+", "");

		if ("hours".contains(parts[1]))
			return now;
		Long number = WordToNumbers.getNumber(parts[0]);
		if ("days".contains(parts[1]))
			return now.minusDays(number);
		if ("weeks".contains(parts[1]))
			return now.minusWeeks(number);
		if ("months".contains(parts[1]))
			return now.minusMonths(number);
		if ("years".contains(parts[1]))
			return now.minusYears(number);
		return null;
	}

	protected String getMonthTitleCase(String pattern, String val) {
		if (pattern == null || pattern.isEmpty())
			return null;
		Pattern pt = Pattern.compile(pattern);
		Matcher mt = pt.matcher(val);
		mt.find();
		return mt.replaceFirst(mt.group().toUpperCase());
	}

	/** Shutdown hook for Spring application context */
	@PreDestroy
	@Profile("!test")
	public void onDestroy() {
		if (getChromeUsed()) destroy();
		if (environment.acceptsProfiles(Profiles.of("test"))) return;
		finishSiteRun(getSiteName(), "Exited abnormally. Parsed " + getJobCount() + " jobs", false, null);
		if (isStopped()) return;
		log.info("Shutting down " + getSiteName() + ". Processed " + getJobCount() + " jobs.");
	}

	public boolean isStopped() {
		return !runIt;
	}

	@Override
	public void stopIt() {
		log.info("Received stop signal");
		this.runIt = false;
	}
	
	protected ChromeDriver getChromeDriver() {
		return getChromeDriver(true);
	}

	/**
	 * Returns selenium {@link WebDriver} instance of Google Chrome browser.
	 * 
	 * @param Path of Google Chrome web driver binary
	 * @return Google Chrome {@link WebDriver} instance
	 */
	protected ChromeDriver getChromeDriver(boolean isHeadless) {
		usedChrome = true;
		ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(new File(chromeExePath))
				.usingAnyFreePort().build();

		ChromeOptions opts = new ChromeOptions().addArguments("--no-sandbox")
				.addArguments("--disable-dev-shm-usage", "--disable-extensions", "disable-infobars").setHeadless(isHeadless)
				.setExperimentalOption("useAutomationExtension", false);
		ChromeDriver driver = new ChromeDriver(service, opts);
		/*
		 * Developer should increase page load timeout in their scraper class when
		 * needed
		 */
		driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
		return driver;
	}

	/**
	 * Returns htmlUnit {@link WebClient} instance of Firefox and Google Chrome
	 * browser.
	 * 
	 * @return Firefox {@link WebClient} instance
	 * 
	 * @return Google Chrome {@link WebClient} instance
	 */
	protected WebClient getRandomClient() {
		List<WebClient> clientList = getClientList();
		return clientList.get(RandomUtils.nextInt(0, clientList.size()));
	}

	protected List<WebClient> getClientList() {
		List<WebClient> clientList = new ArrayList<>();
		clientList.add(getChromeClient());
		clientList.add(getFirefoxClient());
		return clientList;
	}

	/**
	 * Returns a htmlunit {@link WebClient} instance of Firefox browser.
	 * 
	 * @return Firefox {@link WebClient} instance
	 */
	protected WebClient getFirefoxClient() {
		WebClient ob = new WebClient(BrowserVersion.FIREFOX_52);
		setWebClientOptions(ob.getOptions());
		setWebClientPreferences(ob);
		return ob;
	}

	/**
	 * Returns a htmlunit {@link WebClient} instance of Google Chrome browser.
	 * 
	 * @return Google Chrome {@link WebClient} instance
	 */
	protected WebClient getChromeClient() {
		WebClient ob = new WebClient(BrowserVersion.CHROME);
		setWebClientOptions(ob.getOptions());
		setWebClientPreferences(ob);
		return ob;
	}

	private void setWebClientPreferences(WebClient ob) {
		ob.waitForBackgroundJavaScript(10000);
		ob.setJavaScriptTimeout(15000);

		ob.setAjaxController(new NicelyResynchronizingAjaxController());
		ob.getCookieManager().setCookiesEnabled(true);
	}

	private void setWebClientOptions(WebClientOptions opts) {
		opts.setDoNotTrackEnabled(true);
		opts.setThrowExceptionOnScriptError(false);
		opts.setThrowExceptionOnFailingStatusCode(false);
		opts.setTimeout(30000);
		opts.setUseInsecureSSL(true);
	}

	/**
	 * Returns the text of a PDF document after parsing it.
	 * 
	 * @param url URL of PDF document
	 * @return Text of whole PDF document
	 */
	protected String getTextFromPdf(String url) {
		PdfReader reader = null;
		try {
			// TODO check whether url need to encode or not
			if (!urlEncoded(url))
				url = URLEncoder.encode(url, "UTF-8");
			reader = new PdfReader(new URL(url));
			StringBuffer sb = new StringBuffer();
			PdfReaderContentParser parser = new PdfReaderContentParser(reader);
			TextExtractionStrategy strategy = new SimpleTextExtractionStrategy();
			for (int i = 1; i <= reader.getNumberOfPages(); i++)
				sb.append(parser.processContent(i, strategy).getResultantText());
			return sb.toString();
		} catch (MalformedURLException e) {
			log.warn("Failed to load PDF " + url, e);
		} catch (IOException e) {
			log.warn("Failed to parse PDF " + url, e);
		} finally {
			if (reader != null)
				reader.close();
		}
		return url;
	}

	private boolean urlEncoded(String url) {
		return !url.contains(" ");
	}

	/**
	 * Increment the total job parsed counter and returns the incremented value.
	 * 
	 * @return Total job count
	 */
	private int incrementJobCount() {
		return ++jobCount;
	}

	/**
	 * Returns total number of job objects processed (parsed) so far.
	 * 
	 * @return Number of jobs processed
	 */
	public int getJobCount() {
		return jobCount;
	}
	
	public Boolean getChromeUsed() {
		return usedChrome;
	}

	/**
	 * Returns the job site short name.
	 * 
	 * @return Site short name;
	 */
	@Override
	public abstract String getSiteName();

	/**
	 * Returns the host name or the base URL of the job site. <br>
	 * Set this from the site URL returned from {@link SiteMetaData#getUrl()}.
	 * 
	 * <pre>
	 * URL from config: https://jobs.jpmorganchase.com/ListJobs/All
	 * Site host should be: https://jobs.jpmorganchase.com
	 * </pre>
	 * 
	 * @return Base URL
	 */
	protected abstract String getBaseUrl();

	/**
	 * Returns expected job count form job site. <br>
	 * 
	 * @return expected job
	 */
	protected abstract int getExpectedJob();

	/**
	 * You can implement your own destruction. <br>
	 * For selenium driver instance use driver.quit();
	 */
	protected abstract void destroy();
	
	/**
	 * Implemented on scraper classes. It passes the exception from Job Detail Parsing.
	 * 
	 * @return exception instance
	 */
	protected abstract Exception getFailedException();
}
