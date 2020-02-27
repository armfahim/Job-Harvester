package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Elefant Inc. job site parsing class<br>
 * url: https://www.glassdoor.com/Jobs/elefant-ro-Jobs-E873258.htm
 * 
 * @author bm.alamin
 *
 * Since: 2019-02-04
 * NEED TO CHECK JOB ABILABILITY. NOT GRANTTED TO WORK CORRECTLY OR NOT
 */
@Service
public class ElefantInc extends AbstractGlassDoor{
	private static final String SITE= ShortName.ELEFANT_INC;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
