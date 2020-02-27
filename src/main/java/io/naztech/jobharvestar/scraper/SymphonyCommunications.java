package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.linkedin.AbstractLinkedinJobs;

/**
 * Symphony Communication Services Holdings<br>
 * URL: https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=2708824&trk=companyTopCard_top-card-button&redirect=false&position=1&pageNum=0
 * 
 * @author rafayet.hossain
 * @author tanmoy.tushar
 * @since 2019-03-21
 */
@Service
public class SymphonyCommunications extends AbstractLinkedinJobs {
	private static final String SITE = ShortName.SYMPHONY_COMMUNICATION_SERVICES_HOLDINGS;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
	
}
