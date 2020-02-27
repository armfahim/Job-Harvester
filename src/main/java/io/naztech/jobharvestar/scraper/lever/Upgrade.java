package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Upgrade <br>
 * URL: https://jobs.lever.co/upgrade
 * 
 * @author muhammad.tarek
 * @author tohedul.islum
 * @since 2019-03-28
 */
@Service
public class Upgrade extends AbstractLever {
	private static final String SITE = ShortName.UPGRADE;

	@Override
	public String getSiteName() {
		return SITE;
	}
}