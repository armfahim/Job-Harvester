package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Paloma Partners job parsing class<br>
 * URL: https://www.glassdoor.com/Job/paloma-partner-jobs-SRCH_IL.0,6_IC3787799_KO7,14.htm
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-11
 */
@Service
public class PalomaPartners extends AbstractGlassDoor{
	private static final String SITE = ShortName.PALOMA_PARTNERS;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
