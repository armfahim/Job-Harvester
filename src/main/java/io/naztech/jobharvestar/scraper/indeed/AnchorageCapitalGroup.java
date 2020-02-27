package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Anchorage Capital Group job sites scrapper
 * URL: https://www.indeed.com/jobs?q=Anchorage%20Capital&vjk=8b7d3310ee3fccfe
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-07
 */
@Service
public class AnchorageCapitalGroup extends AbstractIndeed {

	private static final String SITE = ShortName.ANCHORAGE_CAPITAL_GROUP;
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
