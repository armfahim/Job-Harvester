package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Assicurazioni Generali Italy.<br>
 * URL: https://generali.taleo.net/careersection/ex/moresearch.ftl
 * 
 * @author naym.hossain
 * @since 2019-01-31
 * 
 * @author tanmoy.tushar
 * @since 2019-04-24
 */
@Service
public class GeneraliItaly extends AbstractTaleoClick {
	private static final String SITE = ShortName.ASSICURAZIONI_GENERALI_ITALY;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getTotalJobId() {
		return "requisitionListInterface.ID3513";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1641.row1";
	}

	@Override
	protected String getCategoryId() {
		return null;
	}

	@Override
	protected String getJobTypeId() {
		return "requisitionDescriptionInterface.ID1699.row1";
	}

	@Override
	protected boolean hasRefId() {
		return true;
	}

	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1482.row1";
	}
	
	@Override
	protected String getNextJobId() {
		return "requisitionDescriptionInterface.pagerDivID826.Next";
	}

	@Override
	protected boolean hasPostedDate() {
		return false;
	}

	@Override
	protected String getPreReqId() {
		return "requisitionDescriptionInterface.ID1540.row1";
	}
}
