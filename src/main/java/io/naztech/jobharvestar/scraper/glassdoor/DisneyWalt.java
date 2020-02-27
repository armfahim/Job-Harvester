package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;

/**
 * DISNEY WALT job site parser. <br>
 * URL: https://www.glassdoor.com/Jobs/Walt-Disney-Company-Jobs-E717_P5.htm
 * 
 * @author tohedul.islum
 * @author tanmoy.tushar
 * @author fahim.reza
 * @since 2019-03-06
 */
@Service
public class DisneyWalt extends AbstractGlassDoor {
	private static final String SITE = ShortName.DISNEYWALT;

	@Override
	public String getSiteName() {
		return SITE;
	}
	
}