package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Theo job site parsing class. <br>
 * URL: https://www.glassdoor.com/Jobs/Theo-Chocolate-Jobs-E312146.htm
 * 
 * @author marjana.akter 
 * @since 2019-03-19
 */
@Service
public class Theo extends AbstractGlassDoor {
	private static final String SITE = ShortName.THEO;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}