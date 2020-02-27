package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.linkedin.AbstractLinkedinJobs;

/**
 * MagicLeaps job parsing class.<br>
 * URL: https://www.linkedin.com/jobs/magic-leap-inc-jobs?position=1&pageNum=0
 * 
 * @author Muhammad Bin Farook
 * @since 2019-03-20
 * 
 * @author tanmoy.tushar
 * @since 2019-04-21
 * @author bm.alamin
 */
@Service
public class MagicLeap extends AbstractLinkedinJobs {
	private static final String SITE = ShortName.MAGIC_LEAP;
	
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
