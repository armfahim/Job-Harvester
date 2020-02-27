package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Binance <br>
 * URL: https://jobs.lever.co/binance
 * 
 * @author tohedul.islum
 * @since 2019-03-31
 */
@Service
public class Binance extends AbstractLever {
	private static final String SITE = ShortName.BINANCE;

	@Override
	public String getSiteName() {
		return SITE;
	}
}