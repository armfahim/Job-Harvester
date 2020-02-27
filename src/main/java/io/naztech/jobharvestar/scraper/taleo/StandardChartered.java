package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Standard Chartered Bank Job site Parser. <br>
 * URL: https://scb.taleo.net/careersection/ex/jobsearch.ftl?lang=en
 * 
 * @author Mahmud Rana
 * @since: 2019-01-30
 * 
 * @author tanmoy.tushar
 * @since 2019-04-15 
 */
@Service
public class StandardChartered extends AbstractTaleoClick {
	private static final String SITE = ShortName.STANDARD_CHARTERED;
	
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getTotalJobId() {
		return "requisitionListInterface.ID3416";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1686.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1636.row1";
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID1786.row1";
	}

	@Override
	protected boolean hasRefId() {
		return true;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1955.row1";
	}

	@Override
	protected String getPreReqId() {
		return null;
	}

	@Override
	protected String getNextJobId() {
		return "requisitionDescriptionInterface.pagerDivID886.Next";
	}

	@Override
	protected boolean hasPostedDate() {
		return true;
	}
}
