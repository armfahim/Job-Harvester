package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Mu Sigma job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Mu-Sigma-Jobs-E253258.htm
 * 
 * @author BM Al-Amin
 * @since 2019-03-13
 */
@Service
public class MuSigma  extends AbstractGlassDoor {
	private static final String SITE = ShortName.MU_SIGMA;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
