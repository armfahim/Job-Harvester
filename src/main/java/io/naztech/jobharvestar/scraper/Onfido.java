package io.naztech.jobharvestar.scraper;


import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractWorkable;


/**
 * ONFIDO Jobsite Parser.<br>
 * URL: https://onfido.com/jobs/
 * 
 * @author Fahim Reza
 * @since 2019-03-25
 * 
 * @author tanmoy.tushar
 * @author bm.alamin
 */
@Service
public class Onfido extends AbstractWorkable {
	private static final String SITE = ShortName.ONFIDO;

	
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
