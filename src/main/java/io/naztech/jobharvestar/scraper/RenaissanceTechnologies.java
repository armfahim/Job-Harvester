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
 * Renaissance Technologies job site
 * URL: https://www.rentec.com/Careers.action
 * 
 * @author masum.billa
 * @author tanmoy.tushar
 * @since 2019-03-04
 */
@Slf4j
@Service
public class RenaissanceTechnologies extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.RENAISSANCE_TECHNOLOGIES;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));

	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 22);
		Document doc = Jsoup.connect(site.getUrl()).get();
		Elements rowList = doc.select("div[class=position]");
		expectedJobCount = rowList.size();
		browseJobList(rowList, site);
	}

	private void browseJobList(Elements rowList, SiteMetaData site) throws PageScrapingInterruptedException {
		for (Element el : rowList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + el.selectFirst("a").attr("href"));
			job.setTitle(el.select("div").get(1).text().trim());
			job.setName(el.select("div").get(1).text().trim());
			job.setLocation(el.select("div").get(2).text().trim());
			try {
				saveJob(getJobDetail(job), site);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setSpec(doc.selectFirst("div[style=line-height: 24px; margin-top: 10px]").text().trim());
		job.setApplyEmail(doc.selectFirst("div[style=margin-top: 10px; background-color: #EDEDED; padding: 10px]>a").text().trim());
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return baseUrl;
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
