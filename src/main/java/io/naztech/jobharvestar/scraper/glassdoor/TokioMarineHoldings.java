package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Tokio Marine Holdings job parsing class <br>
 * URL: https://www.glassdoor.com/Jobs/Tokio-Marine-Holdings-Jobs-E3481.htm
 * 
 * @author BM Al-Amin
 * @since: 2019-03-05
 */
@Service
public class TokioMarineHoldings extends AbstractGlassDoor {
	private static final String SITE = ShortName.TOKIO_MARINE_HOLDINGS;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
