package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Invoice2Go job site parsing class<br>
 * url: https://www.glassdoor.com/Jobs/Invoice2go-Jobs-E922494.htm
 * 
 * @author bm.alamin
 * 
 * Since: 2019-03-31
 */
@Service
public class InvoiceTwoGo extends AbstractGlassDoor{
	private static final String SITE = ShortName.INVOICE2GO;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
