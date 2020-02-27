package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * MS&AD Insurance Group job parsing class
 * URL: https://www.glassdoor.com/Jobs/MS-and-AD-Holdings-Jobs-E354600.htm
 * 
 * @author rafayet.hossain
 * @since 2019-03-05
 */
@Service
public class MsnadInsuranceGroup extends AbstractGlassDoor {
	private static final String SITE = ShortName.MSNAD_INSURANCE_GROUP;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}