package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Convoy <br>
 * URL: https://jobs.lever.co/convoy
 * 
 * @author tohedul.islum
 * @since 2019-03-10
 */
@Service
public class Convoy extends AbstractLever {
	private static final String SITE = ShortName.CONVOY;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
