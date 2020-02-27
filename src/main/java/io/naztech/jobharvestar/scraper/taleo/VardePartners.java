package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Varde Partners job site parsing class. <br>
 * URL: https://chp.tbe.taleo.net/chp02/ats/careers/v2/searchResults?org=VARDE&cws=38
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-11
 */
@Service
public class VardePartners extends AbstractTaleoScroll  {
	private static final String SITE = ShortName.VÄRDE_PARTNERS;
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
