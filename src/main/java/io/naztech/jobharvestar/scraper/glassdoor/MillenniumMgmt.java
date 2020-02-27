package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Millennium Mgmt job site parser<br>
 * URL: https://www.glassdoor.co.uk/Jobs/Millennium-Management-Investment-Firm-Jobs-E850344.htm
 * 
 * @author BM Al-Amin
 * @since: 2019-03-06
 */
@Service
public class MillenniumMgmt extends AbstractGlassDoor{
	private static final String SITE = ShortName.MILLENNIUM_MGMT;
	private String baseUrl;
	@Override
	public String getSiteName() {
		return SITE;
	}
	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0,27);
	}
	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}
}
