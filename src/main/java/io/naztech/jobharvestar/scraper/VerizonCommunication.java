package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.format.DateTimeFormatter;

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
 * Verizon Communication job site parser <br>
 * URL: https://www.verizon.com/about/work/search/jobs?sort_by=cfml10%2Cdesc
 * 
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-03-07
 */
@Service
@Slf4j
public class VerizonCommunication extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.VERIZON_COMMUNICATIONS;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private int maxRetry = 0;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 23);
		Document doc=Jsoup.connect(site.getUrl()).timeout(TIME_10S).get();
		int totalP = getTotalPages(doc);
		expectedJobCount=totalP*25;
		for (int i = 1; i <= totalP; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + "/about/work/search/jobs?page=" + i;
			try {
				getSummaryPages(url, site);
			} catch (Exception e) {
				log.warn("Failed to parse job list page of " + url, e);
			}
		}
	}

	private int getTotalPages(Document doc) {
        Elements pageL =doc.select("div[class=pagination]>a");
		String totalPage = pageL.get(pageL.size() - 2).text();
		return Integer.parseInt(totalPage);
	}

	private void getSummaryPages(String url,SiteMetaData site)throws IOException  {
		Document doc=Jsoup.connect(url).get();
		Elements rowList = doc.select("table[class=table table-hover]>tbody>tr");
		for (Element el : rowList) {
			Job job=new Job(getBaseUrl() + el.getElementsByTag("td").get(0).getElementsByTag("a").attr("href"));
			job.setTitle(el.getElementsByTag("td").get(0).getElementsByTag("a").text().trim());
			job.setName(job.getTitle());
			job.setCategory(el.getElementsByTag("td").get(1).getElementsByTag("a").text().trim());
			job.setLocation(el.getElementsByTag("td").get(2).getElementsByTag("a").text().trim());
			job.setPostedDate(parseDate(el.getElementsByTag("td").get(3).text().split("Date:")[1].trim(), DF));
			try {
				saveJob(getJobDetails(job), site);
			} 
			/**
			 * Sometime detail page got SocketTimeoutException. But If reload the page in
			 * browser, then it's working. To handle socketTimeoutException ,blindly reload
			 * this page 3 times.
			 */
			catch (SocketTimeoutException e) {
				maxRetry++;
				if (maxRetry < 3) {
					saveJob(getJobDetails(job), site);
				} else {
					log.warn("Failed to parse job detail of " + job.getUrl(), e);
				}
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		Elements jobE = doc.select("ul[class=story-info clear-float]>li");
		if (jobE.size() > 2) {
			job.setType(jobE.get(1).text().split(" ")[0].trim());
			job.setReferenceId(jobE.get(2).text().split("#")[1].trim());			
		}
		Element spec=doc.selectFirst("div[class=cs_item_text]");
		job.setSpec(spec.text().trim());		
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
