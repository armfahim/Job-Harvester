package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.greenhouse.AbstractGreenHouse;
import io.naztech.talent.model.SiteMetaData;

/**
 * Forter job site scraper.<br>
 * URL: https://boards.greenhouse.io/forter
 * 
 * @author Shadman Shahriar
 * @author tanmoy.tushar 
 * @since 2019-03-25
 */
@Service
public class Forter extends AbstractGreenHouse {
	private static final String SITE = ShortName.FORTER;

	@Override
	public String getSiteName() {
		return SITE;
	}
	
	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 28);
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}	
}
