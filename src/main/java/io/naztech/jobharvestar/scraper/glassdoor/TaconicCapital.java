package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Taconic Capital Advisors job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Taconic-Capital-Advisors-Jobs-E263485.htm
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-11
 */
@Service
public class TaconicCapital extends AbstractGlassDoor{
	private static final String SITE = ShortName.TACONIC_CAPITAL_ADVISORS;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
