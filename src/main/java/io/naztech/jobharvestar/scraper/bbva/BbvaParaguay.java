package io.naztech.jobharvestar.scraper.bbva;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Bbva Paraguay job site scrapper <br>
 * URL: https://careers.bbva.com/paraguay/jobs-results/
 * 
 * @author naym.hossain
 * @since 2019-01-23
 */
@Service
public class BbvaParaguay extends AbstractBbva {
	private static final String SITE = ShortName.BBVA_PARAGUAY;
	private String baseUrl;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 32);
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}
}
