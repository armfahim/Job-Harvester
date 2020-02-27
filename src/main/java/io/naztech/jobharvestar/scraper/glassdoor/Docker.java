package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Docker job parsing class<br>
 * URL: https://www.glassdoor.com/Job/docker-jobs-SRCH_KO0,6.htm
 * 
 * @author BM Al-Amin
 * @author fahim.reza
 * @since 2019-03-13
 */
@Service
public class Docker extends AbstractGlassDoor {
	private static final String SITE = ShortName.DOCKER;

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
