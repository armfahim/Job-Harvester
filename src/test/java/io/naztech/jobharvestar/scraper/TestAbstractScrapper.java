package io.naztech.jobharvestar.scraper;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.utils.WordToNumbers;
import io.naztech.talent.model.Job;

/**
 * Provides common functionality to web site {@link Scrapper} implementations.
 * 
 * @author Imtiaz Rahi
 * @since 2019-01-13
 */
public abstract class TestAbstractScrapper {
	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(getClass());
	protected static final int USER_ID = 100001;
	/** Time units in seconds. Values are defined in mili seconds */
	protected static final int TIME_1S = 1000, TIME_4S = 4000, TIME_5S = 5000, TIME_10S = 10 * TIME_1S;
	protected static final int TIME_1M = 60 * TIME_1S;

	/**
	 * Returns whether a {@link Job} instance is invalid or not. <br>
	 * Every job instance must contain the permanent URL of the job details in the {@link Job#getUrl()} attribute. <br>
	 * Store calculated hash code using {@link #getJobHash(Job)}, if job details permanent URL is not available.
	 * 
	 * @param ob {@link Job} instance
	 * @return <code>true</code> if {@link Job} is invalid, <code>false</code> it contains all required items
	 */
	protected boolean isJobInvalid(Job ob) {
		return ob == null || StringUtils.isEmpty(ob.getUrl())
					|| StringUtils.isEmpty(ob.getTitle()) || StringUtils.isEmpty(ob.getSpec());
	}

	/**
	 * Returns page count from total job count and jobs per page.
	 * 
	 * @param jobCount Job count as string
	 * @param jobPerPage Number of jobs per list page
	 * @return Page count
	 */
	public int getPageCount(String jobCount, int jobPerPage) {
		if (StringUtils.isBlank(jobCount)) return 0;
		int totalJobs = Integer.parseInt(jobCount);
		int totalPage = totalJobs / jobPerPage;
		return totalJobs % jobPerPage > 0 ? ++totalPage : totalPage;
	}

	/**
	 * Calculates MD5 digest created from few attributes of {@link Job} and returns the value as a 32 character hex string.
	 * 
	 * @param ob {@link Job} instance
	 * @return 32 character hex string of the MD5 digest
	 */
	protected String getJobHash(Job ob) {
		String str = ob.getTitle() + ob.getLocation() + ob.getType() + ob.getReferenceId() + ob.getPostedDate();
		return DigestUtils.md5Hex(str).toUpperCase();
	}

	/**
	 * Returns {@link LocalDate} instance from the given date formats.
	 * 
	 * @param val String value of date
	 * @param formats Expected {@link DateTimeFormatter} formats
	 * @return {@link LocalDate} instance
	 */
	protected LocalDate parseDate(String val, DateTimeFormatter... formats) {
		for (DateTimeFormatter fmt : formats) {
			LocalDate ob = parseDate(val, fmt);
			if (ob != null) return ob;
		}
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
		if (StringUtils.isBlank(val)) return null;
		val = val.toLowerCase();

		LocalDate now = LocalDate.now();
		if ("today".equals(val)) return now;
		if ("yesterday".equals(val)) return now.minusDays(1);

		val = val.replace("ago", "").trim();
		String[] parts = val.split(" ");
		parts[0] = parts[0].replace("+", "");

		if ("hours".contains(parts[1])) return now;
		Long number = WordToNumbers.getNumber(parts[0]);
		if ("days".contains(parts[1])) return now.minusDays(number);
		if ("weeks".contains(parts[1])) return now.minusWeeks(number);
		if ("months".contains(parts[1])) return now.minusMonths(number);
		if ("years".contains(parts[1])) return now.minusYears(number);
		return null;
	}

	protected static ChromeDriver getChromeDriver() {
		return getChromeDriver(true);
	}

	/**
	 * Returns selenium {@link WebDriver} instance of Google Chrome browser.
	 * 
	 * @param Path of Google Chrome web driver binary
	 * @return Google Chrome {@link WebDriver} instance
	 */
	protected static ChromeDriver getChromeDriver(boolean isHeadless) {
		ChromeDriverService service = new ChromeDriverService.Builder()
										.usingDriverExecutable(new File("webdrivers/chromedriver.exe"))
										.usingAnyFreePort().build();

		ChromeDriver driver = new ChromeDriver(service, new ChromeOptions().setHeadless(isHeadless));
		/* Developer should increase page load timeout in their scraper class when needed */
		driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
		driver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);
		return driver;
	}

	protected static WebClient getRandomClient() {
		List<WebClient> allClient = getAllClient();
		return allClient.get(RandomUtils.nextInt(0, allClient.size()));
	}
	
	private static List<WebClient> getAllClient() {
		List<WebClient> allClient = new ArrayList<>();
		allClient.add(getChromeClient());
		allClient.add(getFirefoxClient());
		
		return allClient;
	}

	/**
	 * Returns a htmlunit {@link WebClient} instance of Firefox browser.
	 * 
	 * @return Firefox {@link WebClient} instance
	 */
	protected static WebClient getFirefoxClient() {
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
	protected static WebClient getChromeClient() {
		WebClient ob = new WebClient(BrowserVersion.CHROME);
		setWebClientOptions(ob.getOptions());
		setWebClientPreferences(ob);
		return ob;
	}

	private static void setWebClientPreferences(WebClient ob) {
		ob.waitForBackgroundJavaScript(10000);
		ob.setJavaScriptTimeout(15000);

		ob.setAjaxController(new NicelyResynchronizingAjaxController());
		ob.getCookieManager().setCookiesEnabled(true);
	}

	private static void setWebClientOptions(WebClientOptions opts) {
		opts.setDoNotTrackEnabled(true);
		opts.setThrowExceptionOnScriptError(false);
		opts.setThrowExceptionOnFailingStatusCode(false);
		opts.setTimeout(30000);
		opts.setUseInsecureSSL(true);
	}

}
