package io.naztech.jobharvestar.scraper;

import java.io.IOException;
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
 * MarketInvoice Jobsite Parser<br>
 * URL: https://www.ventureloop.com/ventureloop/companyprofile.php?cid=14114
 * 
 * @author bm.alamin
 * @author iftekar.alam
 * @since 2019-03-25
 */
@Service
@Slf4j
public class MarketInvoice extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.MARKETINVOICE;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("MM-dd-yyyy");
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).userAgent(USER_AGENT).timeout(TIME_10S*2).get();
		this.baseUrl = siteMeta.getUrl().substring(0, 40);		
		Elements rowList = doc.select("table[class=display table thickbox  ]>tbody>tr>td>a");
		expectedJobCount=rowList.size();
		for (int i = 0; i < rowList.size(); i++) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			Job job=new Job(baseUrl + rowList.get(i).attr("href"));
			try {
				saveJob(getJobDetail(job), siteMeta);
			} catch (Exception e) {
				log.warn("Faild to parse details of "+ getBaseUrl(),e);
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_10S*2).get();
		Element jobE = doc.selectFirst("div[class=comp-ad]");
		job.setTitle(jobE.getElementsByTag("h2").text().trim());
		job.setType(jobE.getElementsByTag("p").text().split(" ")[2].trim());
		job.setPostedDate(parseDate(jobE.getElementsByTag("p").text().split(":")[1].trim(), DF));
		job.setLocation(jobE.getElementsByTag("h3").text().trim());
		jobE = doc.selectFirst("div[class=cnt-des]");
		job.setApplicationUrl(jobE.select("div>a").attr("href"));
		job.setSpec(jobE.getElementsByTag("div").get(0).text().trim());
		return job;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return this.baseUrl;
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
