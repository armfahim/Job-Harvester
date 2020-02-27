package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.greenhouse.AbstractGreenHouse;
import io.naztech.talent.model.SiteMetaData;

/**
 * Warbyparker jobs site parse <br>
 * URL: https://www.warbyparker.com/jobs/retail
 * 
 * @author sohid.ullah
 * @since 2019-03-18
 */
@Service
public class WarbyParker extends AbstractGreenHouse {
	private static final String SITE = ShortName.WARBY_PARKER;

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0,28);
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
