package io.naztech.jobharvestar.scraper;


import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Progressa Job Site Parser
 * URL: https://workforcenow.adp.com/mascsr/default/mdf/recruitment/recruitment.html?cid=f533de50-1625-4741-aeef-383de26bed63&ccId=19000101_000001&type=MP&lang=en_CA&selectedMenuKey=CurrentOpenings
 *
 * @author Rahat Ahmad
 * @since 2019-04-01
 */
@Service
public class Progressa extends AbstractWorkforcenowAdp{
	
	private static final String SITE = ShortName.PROGRESSA;
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
