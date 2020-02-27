package io.naztech.jobharvestar.scraper.greenhouse;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;
/**
 * Bridgewater job site parsing class. <br>
 * URL: https://boards.greenhouse.io/bridgewater89
 * 
 * @author naym.hossain
 * @author kamrul.islam
 * @since 2019-03-04
 */
@Service
public class BridgeWater extends AbstractGreenHouse {
	private static final String SITE = ShortName.BRIDGEWATER_ASSOCIATES;
	
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0,28);
	}
	
	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}
}
