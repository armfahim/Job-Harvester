package io.naztech.jobharvestar.crawler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.naztech.jobharvestar.crawler.ScraperDefWrapper.ScraperDef;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Start scrapper from HTTP post request. <br>
 * 
 * @author Imtiaz Rahi
 * @author Mahmud Rana
 * @since 2019-02-07
 */
@RestController
@RequestMapping("/onlive")
@Slf4j
@Profile("!test")
public class StartScrapersFromPost extends AbstractScraperLauncher {

	/**
	 * Start scraper modules by sending POST message. <br>
	 * See details in {@link ScraperDefWrapper}.
	 * 
	 * @param defs List of scraper to start
	 * @return {@link ResponseEntity}
	 * @throws InterruptedException
	 * @see ScraperDefWrapper TODO Push the scraper to queue if it doesn't get scope
	 *      to start easily
	 */
	@GetMapping("/scraper/start/{scraperClassName}")
	public ResponseEntity<String> startScrappers(@PathVariable String scraperClassName) throws InterruptedException {
		
		if (log.isTraceEnabled()) log.trace("Recieved Post Request for : "+scraperClassName);

		if (scraperClassName == null || scraperClassName.isEmpty())
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		
		if (!slotAvailable(scraperClassName)) {
			if (log.isTraceEnabled()) log.trace("Waiting for slot: "+scraperClassName);
			AbstractScraperLauncher.latchOfPost = new CountDownLatch(1);
			latchOfPost.await();
		}
		
		Scrapper scraper = scraperFinder.getScrapperInstance(getInstanceName(scraperClassName));
		if(scraper != null) {
			super.incrementCount();
			try {
				scraper.scrapJobs();
			} catch (Exception e) {
				SiteMetaData site = siteRepo.findByScraper(scraperClassName);
				if (site != null) {
					site.setSiteState("?");
					site.setVersion(site.getVersion()+1);
					
					siteRepo.save(site);
				}
				log.error("Error launching=>"+scraperClassName, e);
				super.decrementCount();
			}
		}
		
		return new ResponseEntity<>("Finished running posted scrapers successfully." , HttpStatus.OK);
	}

	// TODO Implement thread stop feature and then check status from there
	// https://stackoverflow.com/questions/35798987/stop-an-async-spring-method-from-caller-class
	// Even then it will not work when scraper classes will run in different
	// workstations.
	// So, will need something like spring registry (netflix eureka service) for
	// discovery
	@PostMapping(value = "/scrapers/stop")
	public ResponseEntity<Object> stopScrappers(@RequestBody ScraperDefWrapper defs) {
		if (log.isTraceEnabled())
			log.trace(defs.toString());
		if (defs == null || defs.getScrapers() == null || defs.getScrapers().isEmpty())
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		List<String> stopList = defs.getScrapers().stream().map(it -> it.getName()).collect(Collectors.toList());

		Map<String, Scrapper> stopThem = runningScrapers.entrySet().stream()
				.filter(it -> stopList.contains(it.getValue().getSiteName()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		stopThem.entrySet().stream().forEach(it -> {
			scrappers.get(it.getKey()).stopIt();
		});
		decrementCount();
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(value = "/scrapers/running")
	public List<ScraperDef> runningScrapers() {
		return runningScrapers.entrySet().stream()
				.map(it -> new ScraperDef(it.getValue().getSiteName(), it.getKey()))
				.collect(Collectors.toList());
	}
}
