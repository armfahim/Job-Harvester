package io.naztech.jobharvestar.crawler;

import java.util.concurrent.CountDownLatch;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Receives scraper from scraper-queue and start if capacity level exists,<br>
 * nor, causes the listener thread to wait until another scraper finishes
 * process.
 * 
 * @author Mahmud Rana
 * @since 2019-04-01
 */
@Service
@Slf4j
@Profile("prod")
public class StartScraperFromQueue extends AbstractScraperLauncher {
	
	/**
	 * Scraper-Queue Listener: Recieves scraper class name from scraper-queue and starts<br>
	 * executing received class to scrape jobs.
	 * 
	 * @param scraperClass
	 * @throws InterruptedException
	 */
	public void executeScraper(String scraperClass) throws InterruptedException {
		if (log.isTraceEnabled()) log.trace("Received from Q : " + scraperClass);
		
		if (!slotAvailable(scraperClass)) {
			if (log.isTraceEnabled()) log.trace("Waiting for slot: "+scraperClass);
			AbstractScraperLauncher.latch = new CountDownLatch(1);
			latch.await();
		}
		
		Scrapper scraper = scraperFinder.getScrapperInstance(getInstanceName(scraperClass));
		if (scraper!=null) {
			super.incrementCount();
			try {
				scraper.scrapJobs();
			} catch (Exception e) {
				SiteMetaData site = siteRepo.findByScraper(scraperClass);
				if (site != null) {
					site.setSiteState("?");
					site.setVersion(site.getVersion()+1);
					
					siteRepo.save(site);
				}
				log.error("Error launching=>"+scraperClass, e);
				super.decrementCount();
			}
		}
	}


}
