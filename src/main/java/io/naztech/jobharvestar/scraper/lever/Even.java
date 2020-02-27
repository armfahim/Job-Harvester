package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Even job site scraper.<br>
 * URL: https://even.com/careers
 * 
 * @author Asadullah Galib
 * @since 2019-04-1
 */
@Service
public class Even extends AbstractLever {
	private static final String SITE = ShortName.EVEN;

	@Override
	public String getSiteName() {
		return SITE;
	}
}