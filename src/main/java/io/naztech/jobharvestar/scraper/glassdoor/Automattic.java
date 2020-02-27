package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;

/**
 * Automattic <br> 
 * URL: https://www.glassdoor.com/Jobs/Automattic-Jobs-E751107.htm
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-11
 */
@Service
public class Automattic extends AbstractGlassDoor {

	private static final String SITE = ShortName.AUTOMATTIC;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
