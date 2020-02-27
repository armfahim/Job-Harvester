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
 * LendingHome job site parser.<br>
 * URL: https://www.lendinghome.com/careers
 * 
 * @author kamrul.islam
 * @since 2019-03-31
 */
@Service
public class LendingHome extends AbstractGreenHouse {
	private static final String SITE = ShortName.LENDINGHOME;
	
	@Override
	public String getSiteName() {
		return SITE;
	}
	
	@Override
	protected List<String> getAllJobLink(ChromeDriver driver, WebDriverWait wait) {
		List<String> allJobLink = new ArrayList<>();
		List<WebElement> list = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//a[@class='job-title careers-page--job-title']")));
		for (int i = 0; i < list.size(); i++) {
			String link = list.get(i).getAttribute("href");
			if (link == null)
				continue;
			if (link.contains(filterParm)) {
				if (addBaseUrl)
					allJobLink.add(baseUrl + link);
				else
					allJobLink.add(link);
			} else {
				allJobLink.add(link);
			}
		}
		return allJobLink;
	}


	@Override
	protected String getBaseUrl() {
		return null;
	}

	@Override
	protected void setBaseUrl(SiteMetaData site) {
	//	this.baseUrl = site.getUrl().substring(0, 28);
	}

}
