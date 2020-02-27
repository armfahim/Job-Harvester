package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.glassdoor.AbstractGlassDoor;
/**
 * IntarciaTherapeutics job site parsing class. <br>
 * URL: https://www.glassdoor.com/Jobs/Intarcia-Therapeutics-Jobs-E12851.htm
 * 
 * @author assaduzzaman.sohan
 * @author kamrul.islam
 * @since 2019-03-12
 */
@Service
public class IntarciaTherapeutics extends AbstractGlassDoor {
	private static final String SITE = ShortName.INTARCIA_THERAPEUTICS;

	@Override
	public String getSiteName() {
		return SITE;
	}
}
