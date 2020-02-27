package io.naztech.jobharvestar.scraper.bbva;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * BBVA Frances <br>
 * URL: https://careers.bbva.com/frances/jobs-results/
 * 
 * @author naym.hossain
 * @since 2019-02-17
 */
@Service
public class BbvaFrances extends AbstractBbva {
	private static final String SITE = ShortName.BBVA_FRANCES;
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
