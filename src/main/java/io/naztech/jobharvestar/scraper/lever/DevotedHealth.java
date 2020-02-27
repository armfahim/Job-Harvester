package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Devoted Health <br>
 * URL: https://jobs.lever.co/devoted
 * 
 * @author tohedul.islum
 * @since 2019-03-12
 */
@Service
public class DevotedHealth extends AbstractLever {
	private static final String SITE = ShortName.DEVOTED_HEALTH;

	@Override
	public String getSiteName() {
		return SITE;
	}
}