package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Neighborly <br>
 * URL: https://jobs.lever.co/neighborly
 * 
 * @author tohedul.islum
 * @since 2019-03-31
 */
@Service
public class Neighborly extends AbstractLever {
	private static final String SITE = ShortName.NEIGHBORLY;

	@Override
	public String getSiteName() {
		return SITE;
	}
}