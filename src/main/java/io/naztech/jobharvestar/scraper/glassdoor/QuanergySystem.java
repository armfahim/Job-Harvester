package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * QuanergySystem job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Quanergy-Jobs-E1103811.htm
 * 
 * 
 * @author fahim.reza
 * @since 2019-04-11
 */
@Service
public class QuanergySystem extends AbstractGlassDoor {
	private static final String SITE = ShortName.QUANERGY_SYSTEMS;

	@Override
	public String getSiteName() {
		return SITE;
	}
}