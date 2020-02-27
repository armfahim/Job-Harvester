package io.naztech.jobharvestar.scraper.icims;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.icims.AbstractIcims;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * SCHWAB(CHARLES) CORP job site parsing class. <br>
 * URL: https://career-schwab.icims.com/jobs/search?ss=1&searchRelation=keyword_all
 * 
 * @author assaduzzaman.sohan
 * @author tanmoy.tushar
 * @since 2019-03-03
 */
@Slf4j
@Service
public class SchwabCorp extends AbstractIcims {
	private static final String SITE = ShortName.SCHWAB_CHARLES_CORP;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("M/d/yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("M/dd/yyyy");
	private static final DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("MM/d/yyyy");

	@Override
	public String getSiteName() {
		super.JOB_PER_PAGE = 50;
		return SITE;
	}
	
	@Override
	protected void browseJobList(String url, SiteMetaData site) throws PageScrapingInterruptedException, IOException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
		String iFrameUrl = doc.getElementById(CONTAINER_FRAME).attr("src");
		doc = Jsoup.connect(iFrameUrl).timeout(TIME_10S).get();
		Elements rowList = doc.select("ul[class=container-fluid iCIMS_JobsTable]>li");
		if (totalPage < 6)
			expectedJobCount += rowList.size();
		for (Element row : rowList) {
			if (isStopped())
				throw new PageScrapingInterruptedException();
			String jobUrl = row.getElementsByTag("div").get(2).getElementsByTag("a").get(0).attr("href");
			String extrUrlLink = "?hub=7&mobile=false&width=760&height=500&bga=true&needsRedirect=false&jan1offset=360&jun1offset=360";
			Job job = new Job(jobUrl+extrUrlLink);
			job.setDeadline(parseDate(row.getElementsByTag("div").get(4).getElementsByTag("div").get(0).getElementsByTag("dl").get(3).getElementsByTag("dd").get(0).getElementsByTag("span").get(0).text().trim(),DF,DF1,DF2,DF3));
			try {
				saveJob(getJobDetail(job), site);
			}
			/**
			 * Sometime detail page got SocketTimeoutException.
			 * But If reload the page in browser, then it's working.
			 * To handle socketTimeoutException ,blindly reload this page 3 times.
			 */
			catch (SocketTimeoutException e) {
				maxRetry++;
				if (maxRetry < 3) {
					log.info("Failed to load the detail page.. Reloading once again... " + job.getUrl(), e);
					saveJob(getJobDetail(job), site);
				} else {
					log.warn("Failed to parse job detail of " + job.getUrl(), e);
				}
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}
	
}
