package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Discovery Capital Management job sites scrapper
 * URL: https://www.indeed.com/jobs?q=Discovery+Capital+Management&vjk=61d674ad3390cbe5
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-07
 */
@Service
public class DiscoveryCapitalManagement extends AbstractIndeed {

	private static final String SITE = ShortName.DISCOVERY_CAPITAL_MANAGEMENT;
	private String baseUrl;

	@Override
	public void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 22);
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
