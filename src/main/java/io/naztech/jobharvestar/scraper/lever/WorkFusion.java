package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Hike <br>
 * URL: https://jobs.lever.co/workfusion
 * 
 * @author muhammad.tarek
 * @since 2019-03-28
 */
@Service
public class WorkFusion extends AbstractLever {
	private static final String SITE = ShortName.WORKFUSION;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
