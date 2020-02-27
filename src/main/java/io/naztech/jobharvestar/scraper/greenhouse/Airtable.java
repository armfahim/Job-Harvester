package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Airtable job site parsing class. <br>
 * URL: https://boards.greenhouse.io/airtable
 * 
 * @author fahim.reza
 * @since 2019-03-10
 */
@Service
public class Airtable extends AbstractGreenHouse {
	private static final String SITE = ShortName.AIRTABLE;

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
		this.baseUrl = site.getUrl().substring(0, 28);
	}
}