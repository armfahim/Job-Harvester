package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Glenview Capital Management <br>
 * URL: https://www.linkedin.com/jobs/glenview-capital-jobs?pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-10
 */
@Service
public class GlenviewCapitalManagement extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.GLENVIEW_CAPITAL_MANAGEMENT;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
