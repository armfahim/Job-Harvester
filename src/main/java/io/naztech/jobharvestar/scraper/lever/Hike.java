package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Hike <br>
 * URL: https://jobs.lever.co/hike
 * 
 * @author tohedul.islum
 * @since 2019-03-12
 */
@Service
public class Hike extends AbstractLever {
	private static final String SITE = ShortName.HIKE;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
