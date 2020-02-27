package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * ForUsAll <br>
 * URL: https://jobs.lever.co/forusall
 * 
 * @author tohedul.islum
 * @since 2019-04-02
 */
@Service
public class ForUsAll extends AbstractLever {
	private static final String SITE = ShortName.FORUSALL;

	@Override
	public String getSiteName() {
		return SITE;
	}
}