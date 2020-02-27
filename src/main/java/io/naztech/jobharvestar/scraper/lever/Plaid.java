package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Plaid Job parse <br>
 * URL: https://jobs.lever.co/plaid
 * 
 * @author jannatul.maowa
 * @since 2019-03-27
 */
@Service
public class Plaid extends AbstractLever {
	private static final String SITE = ShortName.PLAID;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
