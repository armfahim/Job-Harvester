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
 * Branch job site parser.<br>
 * URL: https://branch.io/careers/jobs/
 * 
 * @author kamrul.islam
 * @since 2019-03-18
 */
@Service
public class Branch extends AbstractGreenHouse {
	private static final String SITE = ShortName.BRANCH;
	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
	}
	
	@Override
	protected List<String> getAllJobLink(ChromeDriver driver, WebDriverWait wait) {
		List<String> allJobLink = new ArrayList<>();
		List<WebElement> list = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@class='job-list']/a")));
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
	protected void setBaseUrl(SiteMetaData site) {
		super.iframeInDetailPage = true;
		super.filterParm ="/apply/";
	
		this.baseUrl = site.getUrl().substring(0, 17);
	}
}
