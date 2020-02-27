package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractJobvite;

/**
 * JetSmarter job site parser. <br>
 * URL: https://jobs.jobvite.com/xo-powered-by-jetsmarter/jobs
 * 
 * @author Armaan Choudhury
 * @since 2019-03-12
 * 
 * @author tanmoy.tushar
 * @since 2019-04-21
 */
@Service
public class JetSmarter extends AbstractJobvite {
	private static final String SITE = ShortName.JETSMARTER;

	@Override
	public String getSiteName() {
		return SITE;
	}

}