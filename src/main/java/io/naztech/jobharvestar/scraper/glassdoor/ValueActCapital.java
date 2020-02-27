package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * ValueAct Capital Management job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/ValueAct-Capital-Jobs-E34935.htm
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-11
 */
@Service
public class ValueActCapital extends AbstractGlassDoor{
	private static final String SITE = ShortName.VALUEACT_CAPITAL_MANAGEMENT;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
