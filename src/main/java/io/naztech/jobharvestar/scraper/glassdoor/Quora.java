package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.greenhouse.AbstractGreenHouse;
import io.naztech.talent.model.SiteMetaData;
/**
 * Quora job site parser<br>
 * URL: https://boards.greenhouse.io/quora
 * 
 * @author BM Al-Amin
 * @author iftekar.alam
 * @since 2019-03-13
 */
@Service
public class Quora extends AbstractGreenHouse {
	private static final String SITE = ShortName.QUORA;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 28);
	}
}
