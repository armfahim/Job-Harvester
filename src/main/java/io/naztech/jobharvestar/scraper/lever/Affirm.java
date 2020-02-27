package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Affirm <br>
 * URL: https://jobs.lever.co/affirm
 * 
 * @author tohedul.islum
 * @since 2019-03-10
 */
@Service
public class Affirm extends AbstractLever {
	private static final String SITE = ShortName.AFFIRM;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
