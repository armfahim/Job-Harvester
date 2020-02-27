package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Ledger <br>
 * URL: https://jobs.lever.co/ledger
 * 
 * @author iftekar.alam
 * @since 2019-03-31
 */
@Service
public class Ledger extends AbstractLever {
	private static final String SITE = ShortName.LEDGER;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
