package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Carta <br>
 * URL: https://jobs.lever.co/carta
 * 
 * @author tohedul.islum
 * @since 2019-03-31
 */
@Service
public class Carta extends AbstractLever {
	private static final String SITE = ShortName.CARTA;

	@Override
	public String getSiteName() {
		return SITE;
	}
}