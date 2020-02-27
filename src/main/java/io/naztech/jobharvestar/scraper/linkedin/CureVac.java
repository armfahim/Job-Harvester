package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * CureVac <br> 
 * URL: https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=806783&trk=companyTopCard_top-card-button&pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-12
 */
@Service
public class CureVac extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.CUREVAC;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
