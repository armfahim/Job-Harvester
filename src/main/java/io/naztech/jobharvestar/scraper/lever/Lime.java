package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Lime <br>
 * URL: https://jobs.lever.co/limebike
 * 
 * @author tohedul.islum
 * @since 2019-03-13
 */
@Service
public class Lime extends AbstractLever {
	private static final String SITE = ShortName.LIME;

	@Override
	public String getSiteName() {
		return SITE;
	}
}