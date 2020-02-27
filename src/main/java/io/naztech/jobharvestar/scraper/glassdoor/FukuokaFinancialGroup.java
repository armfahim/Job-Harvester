package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * FUKUOKA FINANCIAL GROUP job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Fukuoka-Financial-Group-Jobs-E40233.htm
 * NEED TO CHECK JOB ABILABILITY. NOT GRANTTED TO WORK CORRECTLY OR NOT
 * 
 * @author BM Al-Amin
 * @since: 2019-03-06
 */
@Service
public class FukuokaFinancialGroup extends AbstractGlassDoor{
	private static final String SITE = ShortName.FUKUOKA_FINANCIAL_GROUP;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
