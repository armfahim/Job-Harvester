package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Credit Karma job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Credit-Karma-Jobs-E466574.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class CreditKarma  extends AbstractGlassDoor {
	private static final String SITE = ShortName.CREDIT_KARMA;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
