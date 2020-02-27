package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Deposit Solutions<br>
 * URL: https://deposit-solutions.workable.com
 * 
 * @author Armaan Seraj Choudhury
 * @author iftekar.alam
 * @since 2019-03-27
 */
@Service
public class DepositSolutions extends AbstractWorkable {

	private static final String SITE = ShortName.DEPOSIT_SOLUTIONS;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}