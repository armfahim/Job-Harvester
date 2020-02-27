package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Applovin job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/AppLovin-Jobs-E576360.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class Applovin  extends AbstractGlassDoor {
	private static final String SITE = ShortName.APPLOVIN;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
