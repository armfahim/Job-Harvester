package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.lever.AbstractLever;
/**
 * Bird Rides job parsing class<br>
 * URL: https://jobs.lever.co/bird
 * 
 * @author BM Al-Amin
 * @author iftekar.alam
 * @since 2019-03-13
 */
@Service
public class BirdRides extends AbstractLever {
	private static final String SITE = ShortName.BIRD_RIDES;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
