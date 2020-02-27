package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;

/**
 * Brex <br>
 * URL: https://jobs.lever.co/brex
 * 
 * @author tohedul.islum
 * @author fahim.reza
 * @since 2019-03-12
 */
@Service
public class Brex extends AbstractGlassDoor {
	private static final String SITE = ShortName.BREX;

	@Override
	public String getSiteName() {
		return SITE;
	}
}