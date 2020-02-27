package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Carval Investors job site parsing class. <br>
 * URL: https://www.glassdoor.com/Jobs/CarVal-Investors-Jobs-E240967.htm
 * 
 * @author muhammad.tarek
 * @since 2019-03-07
 */
@Service
public class CarvalInvestors extends AbstractGlassDoor {
	private static final String SITE = ShortName.CARVAL_INVESTORS;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
