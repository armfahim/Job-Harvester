package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.lever.AbstractLever;
/**
 * PagerDuty job parsing class<br>
 * URL: https://jobs.lever.co/pagerduty
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class PagerDuty  extends AbstractLever {
	private static final String SITE = ShortName.PAGERDUTY;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
