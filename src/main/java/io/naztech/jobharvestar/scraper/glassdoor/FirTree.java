package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Fir Tree Partners job parsing class<br>
 * URL: https://www.glassdoor.com/Jobs/Fir-Tree-Partners-US-Jobs-EI_IE553601.0,17_IL.18,20_IN1.htm
 * 
 * @author Armaan Seraj Choudhury
 * @since 2019-03-11
 */
@Service
public class FirTree extends AbstractGlassDoor {
	
	private static final String SITE = ShortName.FIR_TREE_PARTNERS;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
