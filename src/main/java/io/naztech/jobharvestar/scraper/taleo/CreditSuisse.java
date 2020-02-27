package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Credit Suisse Jobsite Scraper</a>
 * URL: https://tas-creditsuisse.taleo.net/careersection/external_advsearch/moresearch.ftl?lang=en#
 * 
 * @author Mahmud Rana
 * @author tanmoy.tushar
 * @since 2019-02-20
 */
@Service
public class CreditSuisse extends AbstractTaleoClick {
	private static final String SITE = ShortName.CREDIT_SUISSE;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getTotalJobId() {
		return "requisitionListInterface.ID3542";
	}

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
		return "requisitionDescriptionInterface.ID1617.row1";
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
		return "requisitionDescriptionInterface.ID1669.row1";
	}
}
