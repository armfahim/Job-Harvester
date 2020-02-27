package io.naztech.jobharvestar.crawler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import io.naztech.jobharvestar.service.ScraperFinderService;
import io.naztech.talent.dao.SiteMetaDataRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Abstract class for the controller classes which start, stops the scraper classes.<br>
 * It also measures the current capacity of the system's hardware.
 *  
 * @author Imtiaz Rahi
 * @author Mahmud Rana
 * @author tanmoy.tushar
 * @since 2019-02-13
 */
@Slf4j
public class AbstractScraperLauncher {

	public static CountDownLatch latch;
	public static CountDownLatch latchOfPost;
	private static AtomicInteger counter = new AtomicInteger(1);
	
	@Value("${naztech.webscrapper.slot-capacity:3}")
	private int slotCapacity;

	/** Maintain list of currently running scraper instances */
	protected static Map<String, Scrapper> runningScrapers = new HashMap<>();

	@Autowired protected Map<String, Scrapper> scrappers;

	@Autowired protected SiteMetaDataRepository siteRepo;

	@Autowired protected Environment springEnv;
	
	@Autowired
	protected ScraperFinderService scraperFinder;

	protected boolean slotAvailable(String scrapperClass) {
		/* counter is reduced by scraper class upon exit */
		int count = counter.get();
		if (log.isTraceEnabled()) log.trace("Checking for slot: "+scrapperClass+" ::>> "
				+ " CurrentCount = "+count+" & Capacity = "+slotCapacity);
		return count <= slotCapacity;
	}

	/**
	 * Decrements the count of total scraper received from queue.<br>
	 * Specially needed by scraper sclass upon finished.
	 */
	public static void decrementCount() {
		counter.getAndDecrement();
		if (latch != null) latch.countDown();
		if (latchOfPost != null) latchOfPost.countDown();
	}
	
	/**
	 * Increments the count of total scraper received from queue.<br>
	 * Need by StartScraperFromPost and StartScraperFromQueue class.
	 */
	protected void incrementCount() {
		counter.getAndIncrement();
	}

	protected boolean hasDevProfile() {
		List<String> profiles = Arrays.asList(springEnv.getActiveProfiles());
		return profiles.contains("dev");
	}

	protected boolean hasTestProfile() {
		List<String> profiles = Arrays.asList(springEnv.getActiveProfiles());
		return profiles.contains("test");
	}
	
	protected boolean hasProdProfile() {
		return Arrays.asList(springEnv.getActiveProfiles()).contains("prod");
	}
	
	protected String getHostname() {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.warn("Hostname or IP was not found", e);
		}
		return "?";
	}
	
	protected String getInstanceName(String className) {
		char char0 = className.charAt(0);
		return className.replace(char0, Character.toLowerCase(char0));
	}
}
