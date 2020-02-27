package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.linkedin.AbstractLinkedinJobs;
/**
 * Rubicon Global job site parsing class. <br>
 * URL: https://www.linkedin.com/jobs/rubicon-global-jobs?position=1&pageNum=0
 * 
 * @author assaduzzaman.sohan
 * @author kamrul.islam
 * @author tanmoy.tushar
 * @since 2019-03-13
 */
@Service
public class RubiconGlobal extends AbstractLinkedinJobs {
	private static final String SITE = ShortName.RUBICON_GLOBAL;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
