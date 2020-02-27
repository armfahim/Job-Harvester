package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Clausematch job site parsing class<br>
 * url: https://www.glassdoor.com/Jobs/ClauseMatch-Jobs-E1514044.htm
 * @author bm.alamin
 *
 * Since: 2019-02-04
 */
@Service
public class Clausematch extends AbstractGlassDoor{
	private static final String SITE = ShortName.CLAUSEMATCH;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
