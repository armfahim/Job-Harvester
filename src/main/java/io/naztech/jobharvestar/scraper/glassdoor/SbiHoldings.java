package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * SBI HOLDINGS job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/SBI-Holdings-Jobs-E305998.htm
 * NEED TO CHECK JOB ABILABILITY. NOT GRANTTED TO WORK CORRECTLY OR NOT
 * 
 * @author BM Al-Amin
 * @since: 2019-03-06
 */
@Service
public class SbiHoldings extends AbstractGlassDoor{
	private static final String SITE = ShortName.SBI_HOLDINGS;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
