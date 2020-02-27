package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * WELLS FARGO & CO <br> 
 * URL: https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=1237%2C1409%2C964043%2C2297141%2C119363%2C1235&trk=companyTopCard_top-card-button&redirect=false&position=1&pageNum=0
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-14
 */
@Service
public class WellsFargo extends AbstractLinkedinJobs {
	private static final String SITE = ShortName.WELLS_FARGO_N_CO;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
