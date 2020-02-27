package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * AMUNDI
 * URL:https://career5.successfactors.eu/career?company=Pioneer&career_ns=job_listing_summary&navBarLevel=JOB_SEARCH
 * 
 * @author Tanbirul Hashan
 * @since 2019-02-18
 */
@Service
public class Amundi extends AbstractSuccessfactors {
	private static final String SITE = ShortName.AMUNDI;
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
		return "39:_next";
	}
}
