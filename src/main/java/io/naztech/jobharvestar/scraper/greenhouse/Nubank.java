package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.greenhouse.AbstractGreenHouse;
import io.naztech.talent.model.SiteMetaData;

/**
 * Nubank job site parsing class. <br>
 * URL: https://boards.greenhouse.io/nubank
 * 
 * @author Shajedul Islam
 * @author tanmoy.tushar
 * @since 2019-03-12
 */
@Service
public class Nubank extends AbstractGreenHouse {
	private static final String SITE = ShortName.NUBANK;

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 28);
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