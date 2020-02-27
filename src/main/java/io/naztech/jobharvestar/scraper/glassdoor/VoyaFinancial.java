package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Voya Financial job site parsing class. <br>
 * URL: https://www.glassdoor.com/Jobs/Voya-Financial-Jobs-E816322.htm
 * 
 * @author tanmoy.tushar
 * @since 2019-02-18
 */
@Service
public class VoyaFinancial extends AbstractGlassDoor {
	private static final String SITE = ShortName.VOYA_FINANCIAL;

	@Override
	public String getSiteName() {
		return SITE;
	}
}