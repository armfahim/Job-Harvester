package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.linkedin.AbstractLinkedinJobs;
/**
 * MindTree job site parser. <br>
 * URL: https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=4300&trk=job-results_see-all-jobs-link&redirect=false&position=1&pageNum=0
 * 
 * @author tanmoy.tushar
 * @since 2019-10-20
 */
@Service
public class MindTree extends AbstractLinkedinJobs {
	private static final String SITE = ShortName.MINDTREE;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}

}
