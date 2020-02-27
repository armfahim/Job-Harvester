package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * JANUS HENDERSON INVESTORS EMEA/APAC<br>
 * URL: https://career8.successfactors.com/career?company=Janus&career_ns=job_listing_summary&navBarLevel=JOB_SEARCH
 * 
 * @author tanbirul.hashan
 * @since 2019-02-20
 */
@Service
public class JanusHendersonEmea extends AbstractSuccessfactors {
	private static final String SITE = ShortName.JANUS_HENDERSON_INVESTORS_EMEA;
	private String baseUrl;

	@Override
	public void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 34);
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
