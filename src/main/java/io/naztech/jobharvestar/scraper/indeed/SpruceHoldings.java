package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.indeed.AbstractIndeed;
import io.naztech.talent.model.SiteMetaData;

/**
 * Spruce_Holdings  Job parse  <br>
 * URL: https://www.indeed.com/q-Spruce-Holdings-jobs.html
 * 
 * @author jannatul.maowa
 * @since 2019-03-31
 */
@Service
public class SpruceHoldings extends AbstractIndeed{

	private static final String SITE = ShortName.SPRUCE_HOLDINGS ;
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