package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Morgan Stanley job site scraper. <br>
 * URL: https://ms.taleo.net/careersection/2/jobsearch.ftl?lang=en
 * 
 * @author Mahmud Rana
 * @since 2019-01-21
 * 
 * @author tanmoy.tushar
 * @since 2019-04-28
 */
@Service
public class MorganStanley extends AbstractTaleoClick {
	private static final String SITE = ShortName.MORGAN_STANLEY;
	
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
		return "requisitionDescriptionInterface.ID1714.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1814.row1";
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID1858.row1";
	}

	@Override
	protected boolean hasRefId() {
		return true;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1960.row1";
	}

	@Override
	protected String getNextJobId() {
		return "requisitionDescriptionInterface.pagerDivID886.Next";
	}

	@Override
	protected boolean hasPostedDate() {
		return true;
	}

	@Override
	protected String getPreReqId() {
		return "requisitionDescriptionInterface.ID2016.row1";
	}
}
