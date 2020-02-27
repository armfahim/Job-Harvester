package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;
/**
 * Sweetgreen 
 * URL: https://boards.greenhouse.io/sweetgreen
 * 
 * @author naym.hossain
 * @author kamrul.islam
 * @since 2019-03-04
 */
@Service
public class Sweetgreen extends AbstractGreenHouse {
	private static final String SITE = ShortName.SWEETGREEN;
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

