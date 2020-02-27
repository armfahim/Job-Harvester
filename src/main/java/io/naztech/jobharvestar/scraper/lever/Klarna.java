package io.naztech.jobharvestar.scraper.lever;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.lever.AbstractLever;

/**
 * KLARNA job site parsing class. <br>
 * URL: https://www.klarna.com/careers/openings/
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-12
 */
@Service
public class Klarna extends AbstractLever {
	private static final String SITE = ShortName.KLARNA;
	@Override
	public String getSiteName() {
		return SITE;
	}
	
}
