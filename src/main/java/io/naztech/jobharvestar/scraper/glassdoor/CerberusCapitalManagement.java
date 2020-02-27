package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Cerberus Capital Management job site parsing class. <br>
 * URL:
 * https://www.glassdoor.com/Jobs/Cerberus-Capital-Management-Jobs-E19536.htm
 * 
 * @author muhammad.tarek
 * @since 2019-03-07
 */
@Service
public class CerberusCapitalManagement extends AbstractGlassDoor {
	private static final String SITE = ShortName.CERBERUS_CAPITAL_MANAGEMENT;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
