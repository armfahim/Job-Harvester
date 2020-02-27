package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * OCH ZIFF CAPITAL MGMT_GROUP job site parsing class. <br>
 * URL: https://chm.tbe.taleo.net/chm03/ats/careers/v2/searchResults?org=OCHZIFF&cws=37
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-11
 */
@Service
public class OchZiffCapitalMgmtGroup extends AbstractTaleoScroll  {
	private static final String SITE = ShortName.OCH_ZIFF_CAPITAL_MGMT_GROUP;
	private String baseUrl;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}
}
