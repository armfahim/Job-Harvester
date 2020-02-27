package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Avant <br>
 * URL: https://jobs.lever.co/avant
 * 
 * @author tohedul.islum
 * @since 2019-03-11
 */
@Service
public class Avant extends AbstractLever {
	private static final String SITE = ShortName.AVANT;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
