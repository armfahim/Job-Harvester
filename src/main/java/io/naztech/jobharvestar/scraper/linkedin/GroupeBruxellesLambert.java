package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Groupe Bruxelles Lambert
 * URL: https://www.linkedin.com/jobs/search/?f_C=265778&locationId=OTHERS.worldwide&pageNum=0&position=1
 * 
 * @author Benajir Ullah
 * @since 2019-02-25
 */
@Service
public class GroupeBruxellesLambert extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.GROUPE_BRUXELLES_LAMBERT;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
