package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * All job site of Successfactor's parsing class.
 * AMUNDI URL:https://career5.successfactors.eu/career?company=Pioneer&career_ns=job_listing_summary&navBarLevel=JOB_SEARCH
 * TRYG URL:https://career5.successfactors.eu/career?company=Tryg&career_ns=job_listing_summary
 * JANUS HENDERSON INVESTORS EMEA/APAC URL:https://career8.successfactors.com/career?company=Janus&career_ns=job_listing_summary&navBarLevel=JOB_SEARCH
 * MUFG BANK EMEA URL:https://career5.successfactors.eu/career?company=MUSI&career_ns=job_listing_summary&navBarLevel=JOB_SEARCH
 * 
 * @author Tanbirul Hashan
 * @author tanmoy.tushar
 * @since 2019-02-25
 */
@Service
public abstract class AbstractSuccessfactors extends AbstractScraper implements Scrapper {
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private WebClient webClient = null;
	private int expectedJobCount;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled()) log.trace(site.getUrl());
		setBaseUrl(site);
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		webClient = getFirefoxClient();
		webClient.getOptions().setTimeout(TIME_1M*5);
		webClient.setJavaScriptTimeout(TIME_10S*2);
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		List<Job> jobList = new ArrayList<>();
		while (page != null) {
			List<HtmlAnchor> anchorList = page.getByXPath("//a[@class='jobTitle']");
			for (HtmlAnchor htmlAnchor : anchorList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(getBaseUrl() + htmlAnchor.getHrefAttribute());
				job.setTitle(htmlAnchor.asText().trim());
				job.setName(job.getTitle());
				jobList.add(job);
			}
			page = getNextPage(page);
		}
		expectedJobCount = jobList.size();
		for (Job job : jobList) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			try {
				saveJob(getJobDetails(job), siteMeta);				
			} catch(Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private HtmlPage getNextPage(HtmlPage page) throws InterruptedException,IOException {
		try {
			HtmlElement nextPageAnchor = page.getBody().getOneHtmlElementByAttribute("a", "id", getNextAnchorId());
			page = nextPageAnchor.click();
			Thread.sleep(TIME_5S*2);
			return page;
		} catch (ElementNotFoundException e) {
			log.info("Next page traversal ended");
			return null;
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M*2).get();
		job.setSpec(doc.selectFirst("div[class=joqReqDescription]").text());
		Elements jobInfo = doc.select("div[id=jobAppPageTitle]>div>div>b");
		if (jobInfo.size() > 1) {
			job.setReferenceId(jobInfo.get(0).text().trim());
			job.setPostedDate(parseDate(jobInfo.get(1).text().trim(), DF1, DF2));
		}
		getLocationAndCategory(jobInfo, job);
		return job;
	}
	
	protected Job getLocationAndCategory(Elements jobInfo , Job job) {
		if (jobInfo.size() == 5) {
			job.setCategory(jobInfo.get(2).text().trim());
			job.setLocation(jobInfo.get(3).text().trim() + jobInfo.get(4).text().trim());
		} else {
			job.setLocation(jobInfo.get(2).text());
			if (jobInfo.size() > 3)
				job.setLocation(job.getLocation() + " - " + jobInfo.get(3).text());
		}
		return job;
	}

	@Override
	protected int getExpectedJob() {
		return expectedJobCount;
	}
	
	@Override
	protected void destroy() {
		webClient.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}	
	
	/**
	 * Set base url of the site.
	 * 
	 * @param site {@link SiteMetaData} instance
	 */
	protected abstract void setBaseUrl(SiteMetaData site);

	/**
	 * Sometimes we found different next(>) anchor's id <br> This method will return
	 * specific page's next anchor id(e.g "45:_Next","39:_Next")
	 */
	protected abstract String getNextAnchorId();
}
