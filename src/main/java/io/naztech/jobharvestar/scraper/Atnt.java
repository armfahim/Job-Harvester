package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * ATNT<br> 
 * URL: https://attglobal.avature.net/careers/SearchJobs
 * 
 * @author tohedul.islum
 * @since 2019-03-06
 */
@Service
public class Atnt extends AbstractAvature {
	private static final String SITE = ShortName.ATNT;
	private String baseUrl;

	@Override
	public void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 29);
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
