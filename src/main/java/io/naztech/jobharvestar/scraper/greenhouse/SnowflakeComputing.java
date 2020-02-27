package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * SnowflakeComputing Rooms jobs site parse <br>
 * URL: https://boards.greenhouse.io/snowflakecomputing
 * 
 * @author tanmoy.tushar
 * @since 2019-03-14
 */
@Service
public class SnowflakeComputing extends AbstractGreenHouse {
	private static final String SITE = ShortName.SNOWFLAKE_COMPUTING;
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
