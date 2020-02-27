package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * ContaAzul Software LTDA URL:
 * https://www.linkedin.com/jobs/search?locationId=OTHERS.worldwide&f_C=2379536&trk=companyTopCard_top-card-button&pageNum=0&position=1
 * 
 * @author rafayet.hossain
 * @since 2019-03-31
 */
@Service
public class ContaAzulSoftware extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.CONTAAZUL_SOFTWARE_LTDA;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
