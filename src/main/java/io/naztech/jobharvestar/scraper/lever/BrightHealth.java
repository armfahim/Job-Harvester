package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Bright Health <br>
 * URL: https://jobs.lever.co/brighthealthplan
 * 
 * @author tohedul.islum
 * @since 2019-04-01
 */
@Service
public class BrightHealth extends AbstractLever {
	private static final String SITE = ShortName.BRIGHT_HEALTH;

	@Override
	public String getSiteName() {
		return SITE;
	}
}