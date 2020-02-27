package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Medilinker
 * URL: https://www.indeed.com/jobs?q=Medlink&vjk=d63cfe45c4f348a8#
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-11
 */
@Service
public class Medilinker extends AbstractIndeed {
	private static final String SITE = ShortName.MEDLINKER;
	private String baseUrl;

	@Override
	public void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 22);
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;

	}
}
