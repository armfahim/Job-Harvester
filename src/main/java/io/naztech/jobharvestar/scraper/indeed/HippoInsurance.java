package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Hippo Insurance 
 * URL: https://www.indeed.com/q-Hippo-Insurance-jobs.html
 * 
 * @author Rahat Ahmad
 * @since 2019-03-31
 * 
 * @author bm.alamin
 */
@Service
public class HippoInsurance extends AbstractIndeed {
	private static final String SITE = ShortName.HIPPO_INSURANCE;
	private String baseUrl;

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 22);
	}
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}
}
