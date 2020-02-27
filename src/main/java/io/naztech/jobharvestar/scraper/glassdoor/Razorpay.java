package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;
/**
 * Razorpay job site parsing class<br>
 * url: https://www.glassdoor.co.in/Jobs/Razorpay-Jobs-E1146550.htm
 * 
 * @author bm.alamin
 * 
 * Since: 2019-02-04
 */
@Service
public class Razorpay extends AbstractGlassDoor {
	private static final String SITE = ShortName.RAZORPAY;
	private String baseUrl;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
	
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 27);
	}
	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}
	@Override
	protected String getJobLinkPath() {
		return "//ul[@class = 'jlGrid']/li/div[2]/div/div/a";
	}
	@Override
	protected String getNextButtonPath() {
		return "//div[@class = 'pagingControls cell middle']/ul/li[@class = 'next']/a";
	}
}
