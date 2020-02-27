package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * TradeShift job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Tradeshift-Jobs-E676670.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-14
 */
@Service
public class TradeShift  extends AbstractGlassDoor {
	private static final String SITE = ShortName.TRADESHIFT;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
