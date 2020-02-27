package io.naztech.jobharvestar.scraper.icims;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * GoldmanSachs Job site Parser <br>
 * URL: https://globalcareers-goldmansachs.icims.com/jobs/search?pr=0
 * 
 * @author Mahmud Rana
 * @since 2019-01-15
 */
@Service
public class GoldmanSachs extends AbstractIcims {
	private static final String SITE = ShortName.GOLDMAN_SACHS;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
