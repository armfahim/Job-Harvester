package io.naztech.jobharvestar.scraper.icims;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * American Financial job site scraper. <br>
 * URL: https://jobs-gaic.icims.com/jobs/search?pr=0&schemaId=&o=
 * 
 * @author Armaan Choudhury
 * @author tanmoy.tushar
 * @since 2019-02-18
 */
@Service
public class AmericanFinancial extends AbstractIcims {
	private static final String SITE = ShortName.AMERICAN_FINANCIAL_GROUP;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
