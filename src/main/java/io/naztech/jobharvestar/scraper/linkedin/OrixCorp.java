package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Orix Corp <br>
 * URL: https://www.linkedin.com/jobs/orix-jobs?pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-10
 */
@Service
public class OrixCorp extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.ORIX_CORP;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
