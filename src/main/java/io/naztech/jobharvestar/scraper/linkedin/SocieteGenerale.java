package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.linkedin.AbstractLinkedinJobs;

/**
 * Societe Generale. <br>
 * URL: https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=11771636%2C3496831%2C1691&trk=companyTopCard_top-card-button&redirect=false&position=1&pageNum=0
 * 
 * @author naym.hossain
 * @author rahat.ahmad
 * @author tanmoy.tushar
 * @since 2019-01-28
 */
@Service
public class SocieteGenerale extends AbstractLinkedinJobs {
	private static final String SITE = ShortName.SOCIETE_GENERALE;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
	
	@Override
	protected String getBaseUrl() {
		return null;
	}
}
