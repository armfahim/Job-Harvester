package io.naztech.jobharvestar.scraper.greenhouse;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.SiteMetaData;

/**
 * Behavox job site parser.<br>
 * https://boards.greenhouse.io/behavox
 * 
 * @author kamrul.islam
 * @author fahim.reza
 * @since 2019-03-31
 */
@Service
public class Behavox extends AbstractGreenHouse {
	private static final String SITE = ShortName.BEHAVOX;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}

	@Override
	protected void setBaseUrl(SiteMetaData site) {
		this.baseUrl = site.getUrl().substring(0, 28);
	}

}
