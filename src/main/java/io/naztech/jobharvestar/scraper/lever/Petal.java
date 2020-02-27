package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Petal <br>
 * URL: https://jobs.lever.co/petalcard
 * 
 * @author tohedul.islum
 * @since 2019-04-02
 */
@Service
public class Petal extends AbstractLever {
	private static final String SITE = ShortName.PETAL;

	@Override
	public String getSiteName() {
		return SITE;
	}
}