package io.naztech.jobharvestar.scraper.glassdoor;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;
/**
 * Wave job site scrapper class
 * url: https://www.glassdoor.ca/Jobs/Wave-Toronto-Jobs-EI_IE554319.0,4_IL.5,12_IC2281069.htm
 * @author bm.alamin
 *
 * Since: 2019-03-31
 */

@Service
public class Wave extends AbstractGlassDoor{
	private static final String SITE = ShortName.WAVE;
	
	private String baseUrl;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
	
	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 24);
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
