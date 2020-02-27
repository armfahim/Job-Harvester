package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.indeed.AbstractIndeed;
import io.naztech.talent.model.SiteMetaData;

/**
 * Maverick Capital job parsing class<br>
 * URL: https://www.indeed.com/jobs?q=Maverick+Capital&l=
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-11
 */
@Service
public class MaverickCapital extends AbstractIndeed{
	private static final String SITE = ShortName.MAVERICK_CAPITAL;
	private String baseUrl;
	
	@Override
	public void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 23);
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
