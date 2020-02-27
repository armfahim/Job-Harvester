package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * <a href="https://www.glassdoor.com/Jobs/C3-Jobs-E312703.htm">
 * C3 IoT job parsing class</a><br>
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class CthreeIot  extends AbstractGlassDoor {
	private static final String SITE = ShortName.C3_IOT;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
