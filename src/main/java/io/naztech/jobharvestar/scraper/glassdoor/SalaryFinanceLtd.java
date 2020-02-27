package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Salary Finance Ltd job site parsing class<br>
 * url: https://www.glassdoor.com/Jobs/Salary-Finance-Jobs-E1270207.htm
 * 
 * @author bm.alamin
 *
 * Since: 2019-02-04
 */
@Service
public class SalaryFinanceLtd extends AbstractGlassDoor {
	private static final String SITE = ShortName.SALARYFINANCE_LIMITED;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
}
