package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractBreezyHr;

/**
 * Money Lion <br>
 * URL: https://moneylion.breezy.hr
 * 
 * @author tohedul.islum
 * @author tanmoy.tushar
 * @since 2019-04-02
 */
@Service
public class MoneyLion extends AbstractBreezyHr {
	private static final String SITE = ShortName.MONEYLION;
	
	@Override
	public String getSiteName() {
		return SITE;
	}	
}