package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * COUPANG job site parsing class. <br>
 * URL: https://boards.greenhouse.io/guideline
 * 
 * @author muhammad.tarek
 * @author kamrul.islam
 * @since 2019-04-01
 */
@Service
public class Guideline extends AbstractGreenHouse {
	private static final String SITE = ShortName.GUIDELINE;
	
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
