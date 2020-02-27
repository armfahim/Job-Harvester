package io.naztech.jobharvestar.scraper.bbva;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Bbva Bancomer <br>
 * URL: https://careers.bbva.com/bancomer/category-jobs-results/
 * 
 * @author naym.hossain
 * @since 2019-03-04
 */
@Service
public class BbvaBancomer extends AbstractBbva {
	private static final String SITE = ShortName.BBVA_BANCOMER;
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
