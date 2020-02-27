package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Kabbage job site scrapper.<br>
 * URL: https://www.glassdoor.com/Jobs/Kabbage-Jobs-E606681.htm
 * 
 * @author shadman.shahriar
 * @since 2019-03-24
 * 
 * @author tanmoy.tushar
 * @since 2019-04-25
 */
@Service
public class Kabbage extends AbstractGlassDoor {
	private static final String SITE = ShortName.KABBAGE;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
