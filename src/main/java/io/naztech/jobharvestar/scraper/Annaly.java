package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Annaly Asset Management job site parser <br>
 * URL: https://recruiting.ultipro.com/ANN1002/JobBoard/f76ffcd0-de2d-d6bf-5892-ef0ba64abdba/?q=&o=postedDateDesc
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-02-26
 */
@Service
public class Annaly extends AbstractUltipro {
	private static final String SITE = ShortName.ANNALY_CAPITAL_MGMT;
	private String baseUrl;

	public void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 30);
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
	}
}
