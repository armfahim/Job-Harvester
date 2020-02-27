package io.naztech.jobharvestar.scraper.linkedin;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * AssicurazioniGeneraliGlobal <br>
 * URL: https://www.linkedin.com/jobs/search?f_C=6658636%2C1059748%2C8109%2C15220893%2C24860%2C68754%2C34906%2C74925%2C2073206%2C26729%2C100218%2C163967&locationId=OTHERS.worldwide&trk=jobs_jserp_pagination_4&start=0&count=25&pageNum=0&position=1
 * 
 * @author Benajir Ullah
 * @since 2019-01-31
 */
@Service
public class AssicurazioniGeneraliGlobal extends AbstractLinkedinJobs {

	private static final String SITE = ShortName.ASSICURAZIONI_GENERALI_GLOBAL;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
