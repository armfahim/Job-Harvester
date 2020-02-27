package io.naztech.jobharvestar.scraper.indeed;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * All job sites of Indeed parsing class.
 * 
 * Extended Class'es:
 * <ul>
 * <li><a href="https://ca.indeed.com/Igm-Financial-jobs">Igm Financial</a>
 * <li><a href="https://www.indeed.com/q-Keycorp-jobs.html">Key Corp</a>
 * <li><a href="https://www.indeed.com/q-Japan-Exchange-jobs.html">Japan
 * Exchange Group</a>
 * <li><a href=
 * "https://www.indeed.com/jobs?q=Anchorage%20Capital&vjk=8b7d3310ee3fccfe">Anchorage
 * Capital Group</a>
 * <li><a href=
 * "https://www.indeed.com/jobs?q=Discovery+Capital+Management&vjk=61d674ad3390cbe5">Discovery
 * Capital Management</a>
 * <li><a href=
 * "https://www.indeed.com/q-Pacific-Investment-Management-Company-jobs.html">Pacific
 * Investment Management</a>
 * <li><a href=
 * "https://www.indeed.com/jobs?q=Medlink&vjk=d63cfe45c4f348a8#">Medilinker</a>
 * <li><a href="https://www.indeed.com/cmp/Hippo-Insurance/jobs">Hippo
 * Insurance</a>
 * <li><a href="https://www.indeed.com/jobs?q=JD+Finance&l=">JdFinance</a>
 * <li><a href="https://www.indeed.com/q-Ofo-jobs.html">Ofo</a>
 * </ul>
 * 
 * @author Tanbirul Hashan
 * @since 2019-02-26
 */
@Service
public abstract class AbstractIndeed extends AbstractScraper implements Scrapper {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	private static final String JOB_LOC_PATH = "//div[@class='jobsearch-InlineCompanyRating icl-u-xs-mt--xs  jobsearch-DesktopStickyContainer-companyrating']/div";
	private static final String APPLY_PATH = "//div[@class='icl-u-lg-hide']/a";
	protected WebClient webClient = null;
	private int expectedJobCount = 0;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		webClient = getFirefoxClient();
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		SiteMetaData site = getSiteMetaData(getSiteName());
		if (log.isTraceEnabled())
			log.trace(site.getUrl());
		setBaseUrl(site);
		startSiteScrapping(site);
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta)
			throws FailingHttpStatusCodeException, IOException, InterruptedException {
		HtmlPage page = webClient.getPage(siteMeta.getUrl());
		Thread.sleep(2000);
		while (page != null) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			List<HtmlAnchor> anchorList = page.getByXPath(getJobsPath());
			Collection<HtmlElement> dateList = page.getByXPath(getDatespath());
			Iterator<HtmlElement> postedDates = dateList.iterator();
			int totalJobEachPage = anchorList.size();
			if (totalJobEachPage > 10) totalJobEachPage = 10;
			expectedJobCount += totalJobEachPage;
			for (int i = 0; i < totalJobEachPage; i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				String jobUrl = anchorList.get(i).getHrefAttribute();
				Job job = new Job(getBaseUrl() + jobUrl);
				job.setTitle(anchorList.get(i).asText().trim());
				job.setName(job.getTitle());
				try {
					job.setPostedDate(parseAgoDates(postedDates.next().asText()));
				}
				catch (NoSuchElementException e) {
				}
				try {
					saveJob(getJobDetails(job), siteMeta);
				} catch (Exception e) {
					exception = e;
				}
			}
			page = getNextPage(page);
		}
	}

	protected HtmlPage getNextPage(HtmlPage page) throws InterruptedException, IOException {
		List<HtmlAnchor> paginationAnchrList = page.getByXPath("//div[@class='pagination']/a");
		int len = paginationAnchrList.size();
		if (len == 0) return null;
		if (!paginationAnchrList.get(len - 1).getByXPath("span/span[@class='np']").isEmpty()) {
			page = paginationAnchrList.get(len - 1).click();
			Thread.sleep(TIME_1S);
			return page;
		} else
			log.info("Next page traversal ended");
		return null;
	}

	protected Job getJobDetails(Job job) throws InterruptedException {
		if (job.getUrl() == null)
			return null;
		try {
			HtmlPage page = webClient.getPage(job.getUrl());
			Thread.sleep(TIME_1S * 3);
			List<HtmlElement> eList = page.getByXPath(JOB_LOC_PATH);

			if (eList.size() > 0)
				job.setLocation(eList.get(eList.size() - 1).asText().trim());

			if (!page.getByXPath(APPLY_PATH).isEmpty()) {
				HtmlAnchor applyAnchor = (HtmlAnchor) page.getByXPath(APPLY_PATH).get(0);
				job.setApplicationUrl(applyAnchor.getHrefAttribute());
			}
			try {
				if(page.getBody().getOneHtmlElementByAttribute("div", "class","jobsearch-jobDescriptionText").getTextContent().trim() != null)
					job.setSpec(page.getBody().getOneHtmlElementByAttribute("div", "class","jobsearch-jobDescriptionText").getTextContent().trim());
				else if (page.getBody().getOneHtmlElementByAttribute("div", "id","vjs-content").getTextContent().trim() != null)
					job.setSpec(page.getBody().getOneHtmlElementByAttribute("div", "id","vjs-content").getTextContent().trim());
				
			} catch (ElementNotFoundException e) {
				log.warn("Job details not available for " + job.getUrl());
			}
			return job;
		} catch (FailingHttpStatusCodeException | IOException | ElementNotFoundException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
		}
		return null;
	}

	protected String getDatespath() {
		return "//span[@class='date ']";
	}

	protected String getJobsPath() {
		return "//div[@class='title']/a";
	}

	@Override
	protected int getExpectedJob() {
		return expectedJobCount;
	}

	@Override
	protected void destroy() {
		webClient.close();
	}

	/**
	 * Set base url of the site.
	 * 
	 * @param site {@link SiteMetaData} instance
	 */
	protected abstract void setBaseUrl(SiteMetaData site);

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
