package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Wealth Simple <br>
 * URL: https://jobs.lever.co/wealthsimple
 * 
 * @author tohedul.islum
 * @since 2019-04-01
 */
@Service
public class WealthSimple extends AbstractLever {
	private static final String SITE = ShortName.WEALTHSIMPLE;

	@Override
	public String getSiteName() {
		return SITE;
	}
}