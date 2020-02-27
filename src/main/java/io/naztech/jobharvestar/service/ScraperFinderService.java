package io.naztech.jobharvestar.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.Scrapper;

/**
 * Searches and provides specific bean of {@link Scrapper} using {@link ApplicationContext}
 * 
 * @author Mahmud Rana
 * @since 2019-08-21
 */
@Service
public class ScraperFinderService {

	@Autowired
	ApplicationContext context;
	
	/**
	 * Provides specific scraper instance from bean factory.
	 * 
	 * @param scraperClass {@link String}
	 * @return {@link Scrapper} instance
	 */
	public Scrapper getScrapperInstance(String scraperClass) {
		Map<String, Scrapper> scraperMap = getAllScraperInstances();
		if (scraperMap.containsKey(scraperClass)) return scraperMap.get(scraperClass);
		return null;
	}
	
	private Map<String, Scrapper> getAllScraperInstances() {
		return context.getBeansOfType(Scrapper.class, true, true);
	}
}
