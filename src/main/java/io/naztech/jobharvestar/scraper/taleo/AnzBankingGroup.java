package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * ANZ Banking Group <br>
 * URL: https://anzglobal.taleo.net/careersection/anz_aus_ext/jobsearch.ftl?lang=en#
 * 
 * @author tohedul.islum
 * @since 2019-02-24
 * 
 * @author tanmoy.tushar
 * @since 2019-04-17
 */
@Service
public class AnzBankingGroup extends AbstractTaleo {
	private static final String SITE = ShortName.ANZ_BANKING_GROUP;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
	
	@Override
	protected String getSpecId() {
		return "requisitionDescriptionInterface.ID1703.row1";
	}
	
	@Override
	protected String getPrerequisiteId() {
		return null;
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
	protected boolean hasPostedDate() {
		return false;
	}

	@Override
	protected boolean hasRefId() {
		return true;
	}

	@Override
	protected boolean hasDeadline() {
		return false;
	}

	@Override
	protected boolean hasFirstPagePostedDate() {
		return false;
	}

	@Override
	protected boolean hasFirstPageDeadline() {
		return true;
	}
}
