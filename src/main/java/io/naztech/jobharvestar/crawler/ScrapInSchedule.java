package io.naztech.jobharvestar.crawler;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.naztech.jobharvestar.service.ScraperFinderService;
import lombok.extern.slf4j.Slf4j;

/**
 * Start scraping site as scheduled in the background. <br>
 * Add {@code dev} in spring.profiles.active to use this feature.
 * 
 * @author Imtiaz Rahi
 * @author Mahmud Rana
 * @since 2019-01-16
 */
@Component
@Profile("!prod")
@Slf4j
public class ScrapInSchedule extends AbstractScraperLauncher {

	@Value("${naztech.webscrapper.enabledScrappers:}")
	private String[] enabledScrappers;
	
	@Autowired
	private ScraperFinderService scraperFinder;
	
	/** Launch active scrapers parallelly */
	@Scheduled(fixedRate = 45632000, initialDelay = 500)
	public void launchScrappers() {
		if (!hasDevProfile() && hasTestProfile()) return;

		List<String> enabled = Arrays.asList(enabledScrappers);
		if (enabled.isEmpty()) return;
		
		enabled.forEach(it->{
			Scrapper scraper = scraperFinder.getScrapperInstance(it);
			if (scraper!=null) {
				try {
					scraper.scrapJobs();
				} catch (Exception e) {
					log.error("Error launching=>"+it, e);
				}
			}
		});
	}

}
