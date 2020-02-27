package io.naztech.jobharvestar.scraper;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Banco Sabadell Job Site Parser<br>
 * URL: https://workforcenow.adp.com/mascsr/default/mdf/recruitment/recruitment.html?cid=1a611cd8-4628-463a-89a0-995f0208084d&ccId=19000101_000001&type=MP&lang=en_US
 * 
 * @author Mahmud Rana
 * @author Rahat Ahmad
 * @since 2019-02-03
 */
@Service
public class BancoSabadell extends AbstractWorkforcenowAdp {
	private static final String SITE = ShortName.BANCO_SABADELL;
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
