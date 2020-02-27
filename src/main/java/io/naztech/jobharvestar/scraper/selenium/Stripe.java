package io.naztech.jobharvestar.scraper.selenium;

import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;

/**
 * Stripe URL: https://stripe.com/jobs/search
 * 
 * @author tohedul.islum
 * @since 2019-03-11
 * 
 * @author tanmoy.tushar
 * @since 2019-04-16
 */
@Service
public class Stripe extends AbstractSeleniumJobLink {
	private static final String SITE = ShortName.STRIPE;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}

	@Override
	protected String getRowListPath() {
		return "//a[@class='common-Link sc-bwzfXH dPudUq']";
	}

	@Override
	protected String getFirstPageCatPath() {
		return "//span[@class='sc-htpNat vYuBx common-BodyText']";
	}

	@Override
	protected String getFirstPageLocPath() {
		return "//div[@class='sc-ifAKCX hdwfUL']/span";
	}

	@Override
	protected String getTitleCssQuery() {
		return "h1[class=common-SectionTitle Helm__text--slate1]";
	}

	@Override
	protected String getLocationCssQuery() {
		return null;
	}

	@Override
	protected String getCategoryCssQuery() {
		return null;
	}

	@Override
	protected String getJobTypeCssQuery() {
		return null;
	}

	@Override
	protected String getRefCssQuery() {
		return null;
	}

	@Override
	protected String getSpecCssQuery() {
		return "div[class=Jobs-DetailsDescription__content]";
	}

	@Override
	protected String getPreReqCssQuery() {
		return null;
	}

	@Override
	protected String getPostedDateCssQuery() {
		return null;
	}

	@Override
	protected String getApplyUrlCssQuery() {
		return "a[class=common-Button Helm-Button common-Button common-Button--default]";
	}

	@Override
	protected DateTimeFormatter[] getDateFormats() {
		return null;
	}
}
