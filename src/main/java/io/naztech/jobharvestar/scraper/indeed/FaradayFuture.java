package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Faraday Future job site parsing class
 * URL: https://www.indeed.com/q-Faraday-Future-jobs.html
 * 
 * @author Marjana Akter
 * @since 2019-03-18
 */
@Service
public class FaradayFuture extends AbstractIndeed {

	private static final String SITE = ShortName.FARADAY_FUTURE;
	private String baseUrl;

	@Override
	public void setBaseUrl(SiteMetaData site) {
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
