package io.naztech.jobharvestar.scraper.icims;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * CitGroup(COMMERCIAL_INVESTMENT_TRUST_CIT) <br>
 * URL: https://careers-cit.icims.com/jobs/search
 * 
 * Note: No information on sitemetadata about CitGroup(Commercial Investment Trust CIT)
 * 
 * @author tohedul.islum
 * @since 2019-02-17
 */
@Service
public class CitGroup extends AbstractIcims {
	private static final String SITE = ShortName.COMMERCIAL_INVESTMENT_TRUST_CIT;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
