package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * True Accord <br>
 * URL: https://jobs.lever.co/trueaccord
 * 
 * @author tohedul.islum
 * @since 2019-04-01
 */
@Service
public class TrueAccord extends AbstractLever {
	private static final String SITE = ShortName.TRUEACCORD;

	@Override
	public String getSiteName() {
		return SITE;
	}
}