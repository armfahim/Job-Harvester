package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Hike <br>
 * URL: https://jobs.lever.co/reonomy
 * 
 * @author muhammad tarek
 * @since 2019-03-28
 */
@Service
public class Reonomy extends AbstractLever {
	private static final String SITE = ShortName.REONOMY;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
