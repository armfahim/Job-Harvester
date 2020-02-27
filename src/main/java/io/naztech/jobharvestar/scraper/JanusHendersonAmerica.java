package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Janus Henderson Jobsite Scrapper<br>
 * URL: https://career8.successfactors.com/career?company=Janus&career%5fns=job%5flisting%5fsummary&navBarLevel=JOB%5fSEARCH&_s.crb=tLyRDxAa4I%2fCuLjh2MwL28jc9yY%3d
 * @author mahmud.rana
 * @author iftekar.alam
 * @since 2019-03-03
 */
@Service
public class JanusHendersonAmerica extends AbstractSuccessfactors {
	private static final String SITE = ShortName.JANUS_HENDERSON_INVESTORS_AMERICA;
	private String baseUrl;

	@Override
	public void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 34);
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;

	}

	@Override
	protected String getNextAnchorId() {
		return "45:_next";
	}
}
