package io.naztech.jobharvestar.scraper.linkedin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Iron Source <br>
 * URL:
 * https://www.linkedin.com/jobs/search/?f_C=2657359&locationId=OTHERS.worldwide&pageNum=0&position=1
 * 
 * @author Tanbirul Hashan
 * @since 2019-03-13
 */
@Service
@Slf4j
public class IronSource extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.IRONSOURCE;
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;
	private static WebClient client = null;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).userAgent(USER_AGENT).timeout(TIME_10S).get();
		this.baseUrl = siteMeta.getUrl().substring(0, 27);
		Elements rowList = doc.select("div[class=stick-bottom]>a");
		for (int i = 0; i < rowList.size(); i++) {
			String url=getBaseUrl() + rowList.get(i).attr("href");
			try {
				browseJobList(url,siteMeta);
			} catch (Exception e) {
				log.warn("Failed to parse List of "+url,e);
			}
		}
	}
	
	private void browseJobList(String url, SiteMetaData siteMeta) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		client = getFirefoxClient();
		HtmlPage page = client.getPage(url);
		HtmlElement nextE = page.getFirstByXPath("//button[@class='sc_btn']");
		while(true) {
			if (nextE==null) break;
			page=nextE.click();
			Thread.sleep(TIME_1S);
			nextE = page.getFirstByXPath("//button[@class='sc_btn']");
		}
		List<HtmlElement> jobList = page.getByXPath("//div[@class='careers-loop-item']");
		expectedJobCount+=jobList.size();
		jobList.forEach(el->{ String jobUrl=el.getElementsByTagName("a").get(0).getAttribute("href");
		Job job=new Job(jobUrl);
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				log.warn("Faild to parse details of "+ job.getUrl(),e);
				exception = e;
			} });
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_10S).get();
		job.setTitle(doc.selectFirst("h4").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.select("div[class=col-lg-6 col-sm-10]").get(0).text().trim());
		String jobE = doc.selectFirst("p[class=medium dept]").text();
		String[] parts = null;
		if (jobE.contains(",")) parts = jobE.split(",");
		if(parts.length == 2 ) {
			job.setCategory(parts[0].trim());
			job.setLocation(parts[1].trim()); 
		}
		if(parts.length == 3 ) {
			job.setCategory(parts[0].trim()+","+parts[1].trim());
			job.setLocation(parts[2].trim()); 
		}
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
		client.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
