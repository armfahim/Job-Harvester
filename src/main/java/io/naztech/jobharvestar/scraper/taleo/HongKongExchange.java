package io.naztech.jobharvestar.scraper.taleo;

import org.springframework.stereotype.Service;
import io.naztech.jobharvestar.crawler.ShortName;
/**
 * Honk Kong Exchange.<br>
 * URL: https://hkex.taleo.net/careersection/hkex_hk_external_career_section/joblist.ftl;jsessionid=DdtP_tAsqjcCnhAieU2d0L45lchuz_rj55ETf_s6lQuTUNSKPo2w!992388685
 * 
 * @author naym.hossain
 * @since 2019-01-31
 * 
 * @author tanmoy.tushar
 * @since 2019-04-24
 */
@Service
public class HongKongExchange extends AbstractTaleoClick {
	private static final String SITE = ShortName.HONG_KONG_EXCHANGES_N_CLEARING;
	
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getTotalJobId() {
		return "requisitionListInterface.ID870";
	}

	@Override
	protected String getLocationId() {
		return "requisitionDescriptionInterface.ID1706.row1";
	}

	@Override
	protected String getCategoryId() {
		return "requisitionDescriptionInterface.ID1756.row1";
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
		return "requisitionDescriptionInterface.ID1463.row1";
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
		return null;
	}
}
