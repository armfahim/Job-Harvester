package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * 3I Group job site parsing class.<br>
 * URL: https://www.glassdoor.com/Job/3i-group-jobs-SRCH_KE0,8.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-05
 */
@Service
public class Group3I extends AbstractGlassDoor {
	private static final String SITE = ShortName.GROUP_3I;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
	@Override
	protected String getJobLinkPath() {
		return "//ul[@class = 'jlGrid hover']/li/div[2]/a";
	}
	@Override
	protected String getNextButtonPath() {
		return "//div[@class = 'pagingControls cell middle']/ul/li[@class = 'next']/a";
	}
}
