package io.naztech.jobharvestar.scraper;


import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Crossriver job parsing class<br>
 * URL:"https://workforcenow.adp.com/mascsr/default/mdf/recruitment/recruitment.html?cid=c4c744b8-3a1d-428d-8cc5-f90ccbe8d519&ccId=19000101_000001&type=MP&lang=en_US"
 * 
 * @author Muhammad Bin Farook
 * @since 2019-03-27
 * @author Rahat Ahmad
 * @since 2019-04-01
 */
@Service
public class Crossriver extends AbstractWorkforcenowAdp {
	private static final String SITE = ShortName.CROSSRIVER;

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
