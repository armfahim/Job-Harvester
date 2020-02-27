package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * JAPAN EXCHANGE GROUP URL: https://www.indeed.com/q-Japan-Exchange-jobs.html
 * 
 * @author Tanbirul Hashan
 * @since 2019-02-13
 */

@Service
public class JapanExchangeGroup extends AbstractIndeed {

	private static final String SITE = ShortName.JAPAN_EXCHANGE_GROUP;
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
