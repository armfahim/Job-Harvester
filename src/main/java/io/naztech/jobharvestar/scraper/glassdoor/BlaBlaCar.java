package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * BlaBlaCar job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/BlaBlaCar-Jobs-E840434.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class BlaBlaCar  extends AbstractGlassDoor {
	private static final String SITE = ShortName.BLABLACAR;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
