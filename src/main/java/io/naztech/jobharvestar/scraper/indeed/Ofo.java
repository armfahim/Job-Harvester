package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * JD Finance
 * URL: https://www.indeed.com/q-Ofo-jobs.html
 * 
 * @author Rahat Ahmad
 * @since 2019-03-31
 */
@Service
public class Ofo extends AbstractIndeed{
	private static final String SITE = ShortName.OFO;
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
