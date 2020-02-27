package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Principal Financial Group India.<br>
 * URL: https://principal.taleo.net/careersection/2/joblist.ftl
 * 
 * @author naym.hossain
 * @since 2019-02-12
 * 
 * @author tanmoy.tushar
 * @since 2019-04-24
 */
@Service
public class PrincipalIndia extends AbstractTaleoClick {
	private static final String SITE = ShortName.PRINCIPAL_FINANCIAL_GRP_INDIA;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
	
	@Override
	protected String getTotalJobId() {
		return "requisitionListInterface.ID908";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1712.row1";
	}

	@Override
	protected String getCategoryId() {
		return null;
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID1824.row1";
	}

	@Override
	protected boolean hasRefId() {
		return true;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1545.row1";
	}

	@Override
	protected String getNextJobId() {
		return "requisitionDescriptionInterface.pagerDivID853.Next";
	}

	@Override
	protected boolean hasPostedDate() {
		return false;
	}

	@Override
	protected String getPreReqId() {
		return "requisitionDescriptionInterface.ID1607.row1";
	}
}
