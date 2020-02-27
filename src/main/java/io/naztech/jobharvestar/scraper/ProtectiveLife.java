package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Protective Life Corporation job site parser<br>
 * URL: https://recruiting.ultipro.com/PRO1034/JobBoard/136058ad-2f8c-b44a-fc11-f237764ade8d/?q=&o=postedDateDesc
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-02-27
 */
@Service
public class ProtectiveLife extends AbstractUltipro {
	private static final String SITE = ShortName.PROTECTIVE_LIFE_CORPORATION;
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
