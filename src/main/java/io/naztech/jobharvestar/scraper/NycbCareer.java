package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;

/**
 * NycbCareer parser. <br>
 * URL: https://nycbcareers.ttcportals.com/jobs/search
 * 
 * @author farzana.islam
 * @since 2019-01-23
 * 
 * @author bm.alamin
 * @since 2019-05-08
 */
@Service
public class NycbCareer extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.NEW_YORK_COMMU_BANCORP;

	private String baseUrl;
	private static final int JOBS_PER_PAGE = 25;
	private static final String HEADURL = "https://nycbcareers.ttcportals.com/jobs/search?page=";
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("MMM d, yyyy");
	private WebClient webClient = null;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		webClient = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData site) throws IOException, InterruptedException {
		this.baseUrl = site.getUrl().substring(0, 34);
		HtmlPage page = webClient.getPage(site.getUrl());
		List<HtmlElement> pageNo = page.getByXPath("//div[@class='small-12 medium-8 large-9 columns']/h1");
		String totalJobs = pageNo.get(0).asText().split(" ")[3].trim();
		expectedJobCount = Integer.parseInt(totalJobs);
		int totalPage = getPageCount(totalJobs, JOBS_PER_PAGE);
		for (int i = 1; i <= totalPage; i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
			Thread.sleep(TIME_5S);
			HtmlPage pages = webClient.getPage(HEADURL + i + "#");

			webClient.waitForBackgroundJavaScript(TIME_5S + TIME_4S);
			getSummaryPages(pages, site);
		}
	}

	private void getSummaryPages(HtmlPage pages, SiteMetaData site)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
		List<HtmlElement> rows = pages.getByXPath("//div[@class = 'jobs-section__item']");

		for (int i = 0; i < rows.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			String jobUrl = rows.get(i).getElementsByTagName("h2").get(0).getElementsByTagName("a").get(0)
					.getAttribute("href");
			Job job = new Job(jobUrl);
			try {
				saveJob(getJobDetail(job), site);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage page = webClient.getPage(job.getUrl());

		List<HtmlElement> elApply = page.getByXPath("//div[@class = 'cs_item_apply_button']");
		job.setApplicationUrl(elApply.get(0).getElementsByTagName("a").get(0).getAttribute("href"));

		List<HtmlElement> title = page.getByXPath("//div[@class = 'small-12 columns']");
		job.setTitle(title.get(0).getElementsByTagName("h1").get(0).getTextContent());
		job.setName(job.getTitle());
		String ti = title.get(0).getElementsByTagName("h1").get(0).getTextContent();
		if (ti.contains("-")) {
			String[] temp = ti.split("-");
			String category = temp[0].trim();
			job.setCategory(category);

		} else
			job.setCategory(ti);

		List<HtmlElement> elspeck = page.getByXPath("//div[@class = 'page-section--full']");
		job.setSpec(elspeck.get(1).asText());
		List<HtmlElement> tl = page.getByXPath("//p[@class = 'job-details__main-job-info']");
		String[] str = tl.get(0).asText().split("\n");
		job.setType(str[0]);

		job.setLocation(str[1]);
		if (str[1].contains("-")) {
			String[] ar = str[1].split("-");
			String dateStr = ar[ar.length - 1].trim().replace("Posted ", "");
			job.setPostedDate(parseDate(dateStr, DF));
			job.setLocation(ar[0]);
			job.setCategory(ar[1]);
		}
		job.setReferenceId(str[2].substring(str[2].indexOf(":") + 1).trim());
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
		webClient.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
