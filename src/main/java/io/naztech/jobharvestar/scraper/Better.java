package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.TimeoutException;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Better job site scrapper.<br>
 * URL: https://www.better.org.uk/jobs
 * 
 * @author Shadman Shahriar
 * @since 2019-03-31
 */
@Slf4j
@Service
public class Better extends AbstractScraper implements Scrapper {
	private final String SITE=ShortName.BETTER;
	private WebClient webClient;
	private final int JOB_PER_PAGE = 8;
	private int expectedJobCount = 0;
	private Exception exception;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("d-MMM-yyyy");
	
	@Override
	public void scrapJobs() throws Exception{
		webClient=getChromeClient();
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		try {
			HtmlPage page = webClient.getPage(siteMeta.getUrl());
			webClient.waitForBackgroundJavaScript(TIME_5S);
			List<HtmlElement> divSelector = page.getByXPath("//div[@class='column']");
			for (HtmlElement divEl : divSelector) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				if(isValid(divEl)) getCategory(divEl,siteMeta);
			}
		} catch (FailingHttpStatusCodeException | IOException e) {
			log.error("Failed to load page: "+siteMeta.getUrl(),e);
			throw e;
		}
	}
	
	private void getCategory(HtmlElement divEl,SiteMetaData siteMeta) throws InterruptedException {
		try {
			List<HtmlElement> categoryEl = divEl.getElementsByAttribute("span", "class", "title");
			for (HtmlElement catEl : categoryEl) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				getJobList(catEl.getElementsByTagName("a").get(0).getAttribute("href").trim(),catEl.getElementsByTagName("a").get(0).getTextContent().trim(),siteMeta);
			}
		} catch (ElementNotFoundException e) {
			log.error("Failed to find element in Page: "+siteMeta.getUrl(),e);
		}
	}

	private void getJobList(String url,String category,SiteMetaData siteMeta) throws InterruptedException {
		try {
			HtmlPage page = webClient.getPage(url);
			webClient.waitForBackgroundJavaScript(TIME_5S);
			if (getTotalJob(page) == null) return;
			int pageCount = getPageCount(getTotalJob(page), JOB_PER_PAGE);
			for (int i = 0; i < pageCount; i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				List<HtmlElement> jobSelector = page.getByXPath("//td[@class='erq_searchv4_result_row']");
				expectedJobCount += jobSelector.size();
				for (int j = 0 ; j < jobSelector.size(); j++) {
					if (isStopped()) throw new PageScrapingInterruptedException();
					Job job = new Job();
					job.setCategory(category);
					job.setTitle(jobSelector.get(j).getElementsByAttribute("a", "class", "erq_searchv4_big_anchor").get(0).getTextContent().trim());
					job.setName(job.getTitle());
					job.setReferenceId(jobSelector.get(j).getElementsByAttribute("td", "class", "erq_searchv4_heading5_text").get(0).getTextContent().trim());
					job.setUrl(getJobHash(job));
					HtmlElement deadlineE=(HtmlElement) jobSelector.get(j).getByXPath("//td[@class='erq_searchv4_heading5_text']").get(2);
					job.setDeadline(parseDate(deadlineE.asText().trim(), DF,DF1));
					job.setLocation(jobSelector.get(j).getElementsByAttribute("td", "class", "erq_searchv4_heading5_text").get(1).getTextContent().trim());
					page = getJobDetails(jobSelector.get(j).getElementsByAttribute("a", "class", "erq_searchv4_big_anchor").get(0),i,pageCount,j,jobSelector.size(),job,siteMeta);
				}
			}
		} catch (FailingHttpStatusCodeException | IOException | NumberFormatException e) {
			log.error("Failed to load page"+e);
		} catch (IndexOutOfBoundsException e) {
			log.error("Error at Element (tag) or Index of Element"+e);
		}
	}
	
	private boolean isValid(HtmlElement divEl) {
		return "Or browse by division".equalsIgnoreCase(divEl.getElementsByAttribute("h1","class","gg zeta underline").get(0).asText().trim().toString())
					|| "".equalsIgnoreCase(divEl.getElementsByAttribute("h1","class","gg zeta underline").get(0).asText().trim().toString());
	}

	private String getTotalJob(HtmlPage page) {
		try {
			HtmlElement jobCount = page.getBody().getOneHtmlElementByAttribute("span", "class", "erq_searchv4_count");
			return jobCount.getTextContent().split("of")[1].trim();
		} catch (ElementNotFoundException e) {
			throw e;
		}
	}
	
	private HtmlPage getJobDetails(HtmlElement jobDetailsUrl,int i,int pageCount,int pos,int size,Job job,SiteMetaData siteMeta) {
		try {
			HtmlPage detailsPage = jobDetailsUrl.click();
			try {
				HtmlElement details = detailsPage.getBody().getOneHtmlElementByAttribute("td", "class", "erq_searchv4_heading3 targetLink");
				job.setSpec(details.asText());
				saveJob(job, siteMeta);
			} catch (Exception e) {
				exception = e;
			}
			if (pos == size-1 && i<pageCount-1) {
				detailsPage = detailsPage.getAnchorByText("Return to Search Results").click();
				Thread.sleep(TIME_1S * 2);
				webClient.waitForBackgroundJavaScript(TIME_4S);
				detailsPage = detailsPage.getAnchorByText("Next").click();
				Thread.sleep(TIME_1S * 2);
				webClient.waitForBackgroundJavaScript(TIME_4S);
				return detailsPage;
			}
		} catch (FailingHttpStatusCodeException | IOException | InterruptedException e) {
			log.error("Failed to load page: ",e);
		} catch (TimeoutException e) {
			log.error("Failed to Load Page due to Timeout ",e);
		}
		return null;
	}

	@Override
	public String getSiteName() {
		return SITE;
	}

	@Override
	protected String getBaseUrl() {
		return null;
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
