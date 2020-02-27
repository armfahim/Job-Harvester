package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;

/**
 * HSBC Holding GB job site Scrapper <br>
 * URL: https://hsbc.taleo.net/careersection/external/moresearch.ftl?lang=en_GB
 * 
 * @author Mahmud Rana
 * @since 2019-01-21
 * 
 * @author tanmoy.tushar
 * @since 2019-04-02
 */
@Service
public class HsbcOne extends AbstractTaleoClick {
	private static final String SITE = ShortName.HSBC_HOLDINGS_GB;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
	
	@Override
	protected String getTotalJobId() {
		return "requisitionListInterface.ID3844";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1688.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1634.row1";
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID1796.row1";
	}

	@Override
	protected boolean hasRefId() {
		return true;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1471.row1";
	}

	@Override
	protected String getNextJobId() {
		return "requisitionDescriptionInterface.pagerDivID820.Next";
	}

	@Override
	protected boolean hasPostedDate() {
		return true;
	}

	@Override
	protected String getPreReqId() {
		return "requisitionDescriptionInterface.ID1529.row1";
	}
}
