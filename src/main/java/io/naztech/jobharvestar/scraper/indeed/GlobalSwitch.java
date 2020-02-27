package io.naztech.jobharvestar.scraper.indeed;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.indeed.AbstractIndeed;
import io.naztech.talent.model.SiteMetaData;
/**
 * Global Switch job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Global-Switch-Jobs-E686207.htm
 * 
 * NEED TO CHECK JOB ABILABILITY. NOT GRANTTED TO WORK CORRECTLY OR NOT
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class GlobalSwitch  extends AbstractIndeed {
	private static final String SITE = ShortName.GLOBAL_SWITCH;
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
