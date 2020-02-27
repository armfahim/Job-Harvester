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

/**
 * Satispay job site parsing class. <br>
 * Url: https://satispay.breezy.hr
 * 
 * @author marjana.akter
 * @since 2019-04-02
 */
@Service
public class Satispay extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.SATISPAY;
	private static String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		baseUrl = site.getUrl().substring(0, 26);
		getSummaryPages(site);
	}

	private void getSummaryPages(SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		Document doc = Jsoup.connect(site.getUrl()).get();
		Elements el = doc.select("li[class=position transition]");
		expectedJobCount = el.size();
		for (Element element : el) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			Job job = new Job(getBaseUrl() + element.select("a").attr("href"));
			job.setTitle(element.select("a > h2").first().text());
			job.setName(job.getTitle());
			job.setLocation(element.select("a > ul > li.location > span").first().text());
			try {
				getJobDetails(job.getUrl(), job);
				saveJob(job, site);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetails(String url, Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setSpec(doc.select("div[class=description]").first().text());
		
		Element jobE = doc.selectFirst("li[class=type]>span");
		if (jobE != null)
			job.setType(jobE.text().split("TYPE_")[1].trim().split("%")[0]);
		
		Element jobF = doc.selectFirst("li[class=department]>span");
		if (jobF != null)
			job.setCategory(jobF.text());
			
		job.setApplicationUrl(
				baseUrl + doc.select("#description > div > div.apply-container > ul > li:nth-child(1) > a").first()
						.attr("href").toString());
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
