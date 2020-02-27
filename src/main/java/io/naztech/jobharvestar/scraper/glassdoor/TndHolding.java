package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * T-and-D-Holdings job site parsing class. <br>
 * URL: https://www.glassdoor.com/Jobs/T-and-D-Holdings-Jobs-E35102.htm
 * 
 * @author BM Al-Amin
 * @since: 2019-02-25
 */
@Service
public class TndHolding extends AbstractGlassDoor {
	private static final String SITE = ShortName.TND_HOLDINGS;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
