package io.naztech.jobharvestar.scraper.selenium;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractWorkable;
import io.naztech.talent.model.Job;
import lombok.extern.slf4j.Slf4j;

/**
 * Lend Key Job site Parser.<br>
 * URL: https://apply.workable.com/lendkey-technologies-inc/
 * 
 * @author Fahim Reza
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-04-01
 */
@Slf4j
@Service
public class LendKey extends AbstractWorkable {
	private static final String SITE = ShortName.LENDKEY;

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected List<Job> browseJobList(List<WebElement> rowList) throws PageScrapingInterruptedException {
		List<Job> jobList = new ArrayList<>();
		try {
			for (int i = 0; i < rowList.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Thread.sleep(TIME_4S * 2);
				/* Collecting job url from row list */
				Job job = new Job(rowList.get(i).findElement(By.tagName("a")).getAttribute("href"));
				String postDate = rowList.get(i).findElement(By.tagName("small")).getText().trim();
				if (postDate.contains("about")) job.setPostedDate(parseAgoDates(postDate.split("about")[1].trim()));
				else job.setPostedDate(parseAgoDates(postDate.split("Posted")[1].trim()));
				jobList.add(job);
			}
		} catch (Exception e) {
			log.warn("falied to parse job list ", e);
		}
		return jobList;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
}
