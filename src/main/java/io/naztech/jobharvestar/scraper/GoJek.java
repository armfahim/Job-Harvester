package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * GoJek job site scraper.<br>
 * URL: https://career.go-jek.com/job/?page=
 * 
 * @author tanmoy.tushar
 * @since 2019-06-19
 */
@Slf4j
@Service
public class GoJek extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.GO_JEK;
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception; 
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 25);
		Document doc;
		Elements rowList;
		Element next;
		int i = 1;
		do {
			try {
				doc = Jsoup.connect(site.getUrl() + i).get();
			} catch (HttpStatusException e) {
				i++;
				doc = Jsoup.connect(site.getUrl() + i).get();
			}
			rowList = doc.select("p[class=u-font-weight__bold mb-1 mb-md-0]>a");
			browseJobList(rowList, site);
			next = doc.selectFirst("button[class=next]");
			i++;
		} while (next != null);
	}

	private void browseJobList(Elements jobList, SiteMetaData site) {
		expectedJobCount += jobList.size();
		for (Element link : jobList) {
			try {
				saveJob(getJobDetails(getBaseUrl() + link.attr("href")), site);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetails(String url) {
		Job job = new Job(url);
		try {
			Document doc = Jsoup.connect(url).get();
			Element jobE = doc.selectFirst("h1");
			job.setTitle(jobE.text());
			job.setName(job.getTitle());
			jobE = doc.selectFirst("div[class=mb-2 mb-md-4 u-content-tag]");
			if (jobE != null)
				job.setPrerequisite(jobE.text());
			jobE = doc.selectFirst("div[class=u-content-tag]");
			job.setSpec(jobE.text());

			Elements jobInfo = doc.select("div[class=col-12 col-md-3 pr-md-3]>div>p");
			if (jobInfo.size() > 5) {
				job.setLocation(jobInfo.get(1).text());
				String type = jobInfo.get(3).text();
				if (type.contains("time")) job.setType(type);
				job.setPostedDate(parseDate(jobInfo.get(5).text(), DF1, DF2));
			}
			return job;
		} catch (IOException e) {
			log.warn("Failed to parse job details of " + url, e);
			return null;
		}
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
	
//	/**
//	 * GoJek job site scrapper.<br>
//	 * URL: https://www.go-jek.com/careers/ NEW URL:
//	 * https://career.go-jek.com/job/?page=
//	 * 
//	 * @author Shadman Shahriar
//	 * @since 2019-03-24
//	 * 
//	 * @author tanmoy.tushar
//	 * @since 2019-04-23
//	 */
//public class GoJek extends AbstractSeleniumJobLink {
//	private static final String SITE = ShortName.GO_JEK;
//	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//	@Override
//	protected Job getJobDetail(Job job) {
//		try (WebClient client = getFirefoxClient()) {
//			HtmlPage page = client.getPage(job.getUrl());
//			HtmlElement jobE = page.getFirstByXPath("//div[@class='job']/h2");
//			job.setTitle(jobE.asText().trim());
//			job.setName(job.getTitle());
//			jobE = page.getFirstByXPath("//span[@id='location']");
//			job.setLocation(jobE.asText().trim());
//			jobE = page.getFirstByXPath("//span[@id='tenture']");
//			job.setType(jobE.asText().trim());
//			jobE = page.getFirstByXPath("//span[@id='deadline']");
//			job.setPostedDate(parseDate(jobE.asText().split("deadline")[1].trim(), DF));
//			jobE = page.getFirstByXPath("//a[@id='division']");
//			job.setCategory(jobE.asText().split("/")[1].trim());
//			jobE = page.getFirstByXPath("//div[@class='desc']");
//			job.setSpec(jobE.asText().trim());
//			jobE = page.getFirstByXPath("//div[@class='qualifications']");
//			job.setPrerequisite(jobE.asText().trim());
//			jobE = page.getFirstByXPath("//div[@class='apply-panel']/ul/a");
//			job.setApplicationUrl(jobE.getAttribute("href"));
//			return job;
//		} catch (FailingHttpStatusCodeException | IOException e) {
//			log.warn("Failed to parse job details of " + job.getUrl(), e);
//			return job;
//		}
//	}
