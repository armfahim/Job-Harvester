package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * 10x(Tx)<br>
 * URL: https://www.glassdoor.com/Jobs/10X-Genomics-Jobs-E884452.htm
 *
 * @author tohedul.islum
 * @since 2019-03-31
 */
@Service
public class Tx extends AbstractGlassDoor {
	private static final String SITE = ShortName.TX;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
