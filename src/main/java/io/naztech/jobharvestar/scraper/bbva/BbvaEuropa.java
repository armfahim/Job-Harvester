package io.naztech.jobharvestar.scraper.bbva;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * BBVA Compass <br>
 * URL: https://careers.bbva.com/europa/jobs-results/
 * 
 * @author Armaan Choudhury
 * @since 2019-03-04
 */
@Service
public class BbvaEuropa extends AbstractBbva {
	private static final String SITE = ShortName.BBVA_EUROPA;
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