package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Tudor Investment Corporation job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Tudor-Investment-Corporation-Jobs-E149946.htm
 * 
 * @author Armaan Choudhury
 * @since: 2019-03-11
 */
@Service
public class TudorInvestment extends AbstractGlassDoor{
	private static final String SITE = ShortName.TUDOR_INVESTMENT_CORPORATION;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
