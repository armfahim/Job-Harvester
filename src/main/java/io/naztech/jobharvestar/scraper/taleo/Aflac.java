package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Aflac Jobsite Parser<br>
 * URL: https://aflac.taleo.net/careersection/external/jobsearch.ftl 
 * 
 * @author Mahmud Rana
 * @author tanmoy.tushar
 * @since 2019-02-11
 */
@Service
public class Aflac extends AbstractTaleoClick {
	private static final String SITE = ShortName.AFLAC;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getTotalJobId() {
		return "requisitionListInterface.ID3378";
	}

	@Override
	protected String getLocationId() {
		return null;
	}

	@Override
	protected String getCategoryId() {
		return null;
	}

	@Override
	protected String getJobTypeId() {
		return null;
	}

	@Override
	protected boolean hasRefId() {
		return true;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1571.row1";
	}

	@Override
	protected String getNextJobId() {
		return "requisitionDescriptionInterface.pagerDivID853.Next";
	}

	protected boolean hasPostedDate() {
		return false;
	}

	@Override
	protected String getPreReqId() {
		return null;
	}
}
