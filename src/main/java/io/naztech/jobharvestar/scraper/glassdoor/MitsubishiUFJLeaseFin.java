package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * MITSUBISHI UFJ LEASE FIN job parsing class
 * URL: https://www.glassdoor.com/Jobs/Mitsubishi-UFJ-Jobs-E40377.htm
 * 
 * @author rafayet.hossain
 * @since: 2019-03-06
 */
@Service
public class MitsubishiUFJLeaseFin extends AbstractGlassDoor {
	private static final String SITE = ShortName.MITSUBISHI_UFJ_LEASE_FIN;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
