package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Dave <br>
 * URL: https://jobs.lever.co/dave
 * 
 * @author tohedul.islum
 * @since 2019-04-02
 */
@Service
public class Dave extends AbstractLever {
	private static final String SITE = ShortName.DAVE_INC;

	@Override
	public String getSiteName() {
		return SITE;
	}
}