package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.indeed.AbstractIndeed;
import io.naztech.talent.model.SiteMetaData;

/**
 * Coatue Management job parsing class<br>
 * URL: https://www.indeed.com/q-Coatue-Management-jobs.html
 * 
 * @author Armaan Seraj Choudhury
 * @author iftekar.alam
 * @since 2019-03-11
 */
@Service
public class CoatueManagement extends AbstractIndeed {
	private static final String SITE = ShortName.COATUE_MANAGEMENT;
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
