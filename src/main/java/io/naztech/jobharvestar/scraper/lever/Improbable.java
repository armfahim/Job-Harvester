package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Improbable <br>
 * URL: https://jobs.lever.co/improbable
 * 
 * @author Kayumuzzaman Robin
 * @since 2019-04-15
 */
@Service
public class Improbable extends AbstractLever {
	private static final String SITE = ShortName.IMPROBABLE;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
