package io.naztech.jobharvestar.scraper.bbva;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Bbva Colombia <br>
 * URL: https://careers.bbva.com/colombia/jobs-results/
 * 
 * @author tohedul.islum
 * @since 2019-01-29
 */
@Service
public class BbvaColombia extends AbstractBbva {
	private static final String SITE = ShortName.BBVA_COLOMBIA;
	private String baseUrl;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 33);
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}
}