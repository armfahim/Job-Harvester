package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;
/**
 * Just work jobs site parse <br>
 * URL: https://justworks.com/careers/all-positions
 * 
 * @author Asadullah Galib
 * @author kamrul.islam
 * @since 2019-04-1
 */
@Service
public class JustWork extends AbstractGreenHouse {
	private static final String SITE = ShortName.JUSTWORKS;
	
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