package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Chainalysis job site parser.<br>
 * URL: https://boards.greenhouse.io/chainalysis
 * 
 * @author kamrul.islam
 * @since 2019-03-31
 */
@Service
public class Chainalysis extends AbstractGreenHouse {
	private static final String SITE = ShortName.CHAINALYSIS;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0,28);
	}
}
