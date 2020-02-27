package io.naztech.jobharvestar.scraper.workday;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Nvidia job site parsing class. <br>
 * URL: https://nvidia.wd5.myworkdayjobs.com/NVIDIAExternalCareerSite
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-05
 */
@Service
public class Nvidia extends AbstractMyWorkDayJobs {
	private static final String SITE = ShortName.NVIDIA;
	private String baseUrl;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
	}
}



