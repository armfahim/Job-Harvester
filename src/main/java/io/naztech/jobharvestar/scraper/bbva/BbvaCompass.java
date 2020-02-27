package io.naztech.jobharvestar.scraper.bbva;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * BBVA Compass <br>
 * URL: https://careers.bbva.com/compass/jobs-results/
 * 
 * @author Mahmud Rana
 * @since 2019-02-17 
 */
@Service
public class BbvaCompass extends AbstractBbva {
	private static final String SITE = ShortName.BBVA_COMPASS;
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
