package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.lever.AbstractLever;

/**
 * BillCom jobsite parser<br>
 * URL: https://jobs.lever.co/bill
 * 
 * @author Muhammad Bin Farook
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-03-24
 */
@Service
public class Billcom extends AbstractLever {
	private static final String SITE = ShortName.BILLCOM;

	@Override
	public String getSiteName() {
		return SITE;
	}
	
}
