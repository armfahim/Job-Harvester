package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Silver Point Capital job parsing class<br>
 * URL: https://www.indeed.com/q-Silver-Point-Capital-jobs.html
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-11
 */
@Service
public class SilverPoint extends AbstractIndeed{
	private static final String SITE = ShortName.SILVER_POINT_CAPITAL;
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
