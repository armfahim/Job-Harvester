package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Hujiang job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Hujiang-Jobs-E1368795.htm
 * 
 * NEED TO CHECK JOB ABILABILITY. NOT GRANTTED TO WORK CORRECTLY OR NOT
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class Hujiang  extends AbstractGlassDoor {
	private static final String SITE = ShortName.HUJIANG_;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
