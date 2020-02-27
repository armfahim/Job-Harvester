package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Sumitomo Mitsui Trust Job parsing class URL:
 * https://www.glassdoor.com/Job/sumitomo-mitsui-trust-bank-jobs-SRCH_KE0,26.htm
 * 
 * @author rafayet.hossain
 * @since 2019-03-05
 */
@Service
public class SumitomoMitsuiTrust extends AbstractGlassDoor {
	private static final String SITE = ShortName.SUMITOMO_MITSUI_TRUST;

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
