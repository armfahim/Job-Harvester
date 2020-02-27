package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Rappi <br>
 * URL: https://jobs.lever.co/rappi
 * 
 * @author tohedul.islum
 * @since 2019-03-10
 */
@Service
public class Rappi extends AbstractLever {
	private static final String SITE = ShortName.RAPPI;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
