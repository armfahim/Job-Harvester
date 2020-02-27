package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Zoox <br>
 * URL: https://jobs.lever.co/zoox
 * 
 * @author tohedul.islum
 * @since 2019-03-14
 */
@Service
public class Zoox extends AbstractLever {
	private static final String SITE = ShortName.ZOOX;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
