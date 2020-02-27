package io.naztech.jobharvestar.scraper.bbva;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Bbva Provincial job site scrapper <br>
 * URL: https://careers.bbva.com/provincial/jobs-results/
 * 
 * @author naym.hossain
 * @since 2019-01-24
 */
@Service
public class BbvaProvincial extends AbstractBbva {
	private static final String SITE = ShortName.BBVA_PROVINCIAL;
	private String baseUrl;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 35);
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}
}
