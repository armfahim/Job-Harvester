package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Third Point job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Third-Point-Jobs-E272425.htm
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-11
 */
@Service
public class ThirdPoint extends AbstractGlassDoor{
	private static final String SITE = ShortName.THIRD_POINT;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
