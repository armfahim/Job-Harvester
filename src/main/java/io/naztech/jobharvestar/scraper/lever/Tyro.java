package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Tyro <br>
 * URL: https://jobs.lever.co/tyro
 * 
 * @author tohedul.islum
 * @since 2019-04-01
 */
@Service
public class Tyro extends AbstractLever {
	private static final String SITE = ShortName.TYRO;

	@Override
	public String getSiteName() {
		return SITE;
	}
}