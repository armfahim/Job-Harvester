package io.naztech.jobharvestar.scraper;


import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * CI FINANCIAL Job Site Parser
 * URL: https://workforcenow.adp.com/mascsr/default/mdf/recruitment/recruitment.html?cid=8af61d30-ce97-4d97-af7d-bda5fa023504
 * 
 * @author Rahat Ahmad
 * @since 2019-02-14
 */
@Service
public class CiFinancial extends AbstractWorkforcenowAdp{
	
	private static final String SITE = ShortName.CI_FINANCIAL;
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
