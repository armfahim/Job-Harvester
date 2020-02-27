package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Japan Post Holdings Co Job Site Scraper. <br>
 * URL: https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=5873179&trk=companyTopCard_top-card-button&pageNum=0&position=1
 * 
 * @author Sanzida Hoque
 * @author Tanbirul Hashan
 * @since 2019-12-02
 */
@Service
public class JapanPostHoldingsCo extends AbstractLinkedinJobs {
	private static final String SITE = ShortName.JAPAN_POST_HOLDINGS_CO;
	
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
