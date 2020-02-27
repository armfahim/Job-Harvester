package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Capula Investment Management <br>
 * URL: https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=222712&trk=companyTopCard_top-card-button&pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-10
 */
@Service
public class CapulaInvestmentManagement extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.CAPULA_INVESTMENT_MANAGEMENT;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
