package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * ShopClues<br>
 * URL: https://www.shopclues.com/current-opening.html
 * 
 * @author rafayet.hossain
 * @since 2019-03-19
 */
@Slf4j
@Service
public class ShopClues extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SHOPCLUES;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		browseJobList(site);
	}

	public void browseJobList(SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		try {
			Document document = Jsoup.connect(site.getUrl()).get();
			Elements el = document.getElementsByClass("apply_for_same");
			Elements titleList = document.getElementsByAttributeValue("style", "font-weight: bold; color: black; font-size: 13px;");
			expectedJobCount = titleList.size();
			for (int i = 0; i < el.size() - 3; i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job();
				job.setTitle(titleList.get(i).text().split(":")[1].trim());
				job.setName(job.getTitle());
				try {
					saveJob(getJobDetails(el.get(i), job), site);					
				} catch (Exception e) {
					exception = e;
				}
			}
		} catch (IOException  e) {
			log.warn("Failed to parse job details", e);
			throw e;
		}
	}

	public Job getJobDetails(Element el, Job job) {
		job.setLocation(el.nextElementSibling().text().split(":")[1].trim());
		job.setApplyEmail(el.attr("href").split(":")[1]);
		job.setUrl(getJobHash(job));
		Element sibling = el.nextElementSibling();
		String spec = "";
		do {
			if (!sibling.hasAttr("font-weight: bold; color: black; font-size: 13px;") && sibling.hasText())
				spec += sibling.outerHtml();
			sibling = sibling.nextElementSibling();
		} while (!sibling.hasClass("apply_for_same"));
		job.setSpec(spec);
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
	}
	
	@Override
	protected int getExpectedJob() {
		return expectedJobCount;
	}
	
	@Override
	protected void destroy() {
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}