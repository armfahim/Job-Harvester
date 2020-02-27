package io.naztech.jobharvestar.scraper.icims;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Sumitomo Americas <br>
 * URL: https://careers-smbcgroup.icims.com/jobs/search?ss=1&searchKeyword=&searchCategory=&searchZip=&searchRadius=20
 * 
 * @author tohedul.islum
 * @since 2019-02-27
 */
@Service
public class SumitomoAmericas extends AbstractIcims {
	private static final String SITE = ShortName.SUMITOMO_MITSUI_FINL_GRP_AMERICAS;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
