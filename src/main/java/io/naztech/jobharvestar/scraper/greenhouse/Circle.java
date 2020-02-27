package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;
/**
 * Circle jobs site parse <br>
 * URL: https://boards.greenhouse.io/circle
 * 
 * @author iftekar.alam
 * @author kamrul.islam
 * @since 2019-03-28
 */
@Service
public class Circle extends AbstractGreenHouse {
	private static final String SITE = ShortName.CIRCLE;
	
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
		return baseUrl;
	}
}