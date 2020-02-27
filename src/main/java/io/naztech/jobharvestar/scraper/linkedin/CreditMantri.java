package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.linkedin.AbstractLinkedinJobs;

/**
 * <a href="https://www.linkedin.com/jobs/search?keywords=CreditMantri&location=Chennai%2C%20Tamil%20Nadu%2C%20India&trk=organization_guest_jobs-search-bar_search-submit&redirect=false&position=1&pageNum=0">
 * CreditMantri Jobsite Parser</a><br>
 * 
 * @author Fahim Reza
 * @since 2019-04-04
 */
@Service
public class CreditMantri extends AbstractLinkedinJobs{
	private static final String SITE = ShortName.CREDITMANTRI;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}

	
}
