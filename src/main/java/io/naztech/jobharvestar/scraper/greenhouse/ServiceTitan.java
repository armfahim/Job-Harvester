package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.greenhouse.AbstractGreenHouse;
import io.naztech.talent.model.SiteMetaData;

/**
 * ServiceTitan Jobsite Parser<br>
 * URL: https://boards.greenhouse.io/servicetitan
 * 
 * @author Rahat Ahmad
 * @author tanmoy.tushar
 * @since 2019-03-13
 */
@Service
public class ServiceTitan extends AbstractGreenHouse {
	private static final String SITE = ShortName.SERVICETITAN;
	
	@Override
	protected void setBaseUrl(SiteMetaData site) {
		super.baseUrl = site.getUrl().substring(0, 28);
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
