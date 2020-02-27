package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.lever.AbstractLever;

/**
 * Q4 <br>
 * URL: https://jobs.lever.co/q4inc
 * 
 * @author jannatul.maowa
 * @author iftekar.alam
 * @since 2019-03-27
 */
@Service
public class Q4orce extends AbstractLever{
	private static final String SITE = ShortName.Q4;
	@Override
	public String getSiteName() {
		return SITE;
	}
}
