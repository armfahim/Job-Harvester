package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Credit Suisse Jobsite Scraper <br>
 * URL: https://tas-creditsuisse.taleo.net/careersection/campus/moresearch.ftl?lang=en
 * 
 * @author tanmoy.tushar
 * @since 2019-03-31
 */
@Service
public class CreditSuisseAssetMgmt extends AbstractTaleoClick {
	private static final String SITE = ShortName.CREDIT_SUISSE_ASSET_MGMT;

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1478.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1538.row1";
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID1518.row1";
	}

	@Override
	protected boolean hasRefId() {
		return true;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1660.row1";
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}

	@Override
	protected String getTotalJobId() {
		return "requisitionListInterface.ID3668";
	}

	@Override
	protected String getNextJobId() {
		return "requisitionDescriptionInterface.pagerDivID1728.Next";
	}

	@Override
	protected boolean hasPostedDate() {
		return false;
	}

	@Override
	protected String getPreReqId() {
		return "requisitionDescriptionInterface.ID1712.row1";
	}
}
