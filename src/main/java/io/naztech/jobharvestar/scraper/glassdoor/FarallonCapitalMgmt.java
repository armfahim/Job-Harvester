package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Farallon Capital Mgmt job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Farallon-Capital-Management-Jobs-E260058.htm
 * NEED TO CHECK JOB ABILABILITY. NOT GRANTTED TO WORK CORRECTLY OR NOT
 * 
 * @author BM Al-Amin
 * @since: 2019-03-06
 */
@Service
public class FarallonCapitalMgmt extends AbstractGlassDoor {
	private static final String SITE = ShortName.FARALLON_CAPITAL_MGMT;

	@Override
	public String getSiteName() {
		return SITE;
	}

}
