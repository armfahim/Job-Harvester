package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Indigo Agriculture jobs site parse <br>
 * URL: https://boards.greenhouse.io/indigo
 * 
 * @author tanmoy.tushar
 * @author kamrul.islam
 * @since 2019-03-13
 */
@Service
public class Indigo extends AbstractGreenHouse {
	private static final String SITE = ShortName.INDIGO_AGRICULTURE;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
	}
	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 24);
	}
}
