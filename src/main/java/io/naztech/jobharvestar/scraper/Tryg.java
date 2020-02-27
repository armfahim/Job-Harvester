package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * TRYG<br>
 * URL: https://career5.successfactors.eu/career?company=Tryg&career_ns=job_listing_summary
 * 
 * @author Tanbirul Hashan
 * @since 2019-02-19
 */
@Service
public class Tryg extends AbstractSuccessfactors {
	private static final String SITE = ShortName.TRYG;
	private String baseUrl;

	@Override
	public void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 33);
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;

	}

	@Override
	protected String getNextAnchorId() {
		return "45:_next";
	}
}
