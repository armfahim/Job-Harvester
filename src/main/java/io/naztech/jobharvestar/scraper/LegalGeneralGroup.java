package io.naztech.jobharvestar.scraper;


import java.io.IOException;
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
import lombok.extern.slf4j.Slf4j;

/**
 * LEGAL & GENERAL GROUP job site parsing class. <br>
 * URL: https://apply.legalandgeneralgroup.com/vacancies/#results
 * 
 * @author assaduzzaman.sohan
 * @author fahim.reza
 * @since 2019-02-27
 */
@Slf4j
@Service
public class LegalGeneralGroup extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.LEGAL_N_GENERAL_GROUP;
	private static WebClient CLIENT = null;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 38);
		HtmlPage page;
		CLIENT = getChromeClient();
		page = CLIENT.getPage(siteMeta.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		HtmlElement el = page.getBody().getOneHtmlElementByAttribute("button", "class", "search-button");
		page = el.click();
		CLIENT.waitForBackgroundJavaScript(TIME_10S);
		List<HtmlElement> list = page.getByXPath("//a[@data-text = 'Full description']");
		Job job = new Job(); 
		expectedJobCount = list.size();
		for (int i = 0; i < list.size(); i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();		
			String s = list.get(i).getAttribute("href").trim();
			String[] parts = s.split("\\n");
			job.setUrl(baseUrl+parts[0]+parts[1]);
			try {
				saveJob(getJobDetail(job), siteMeta);				
			} catch (Exception e) {
				exception = e;
			}
		}
	}

	private Job getJobDetail(Job job) throws IOException, InterruptedException {
		try {
			HtmlPage JobDetailPage = CLIENT.getPage(job.getUrl());
			job.setReferenceId(JobDetailPage.getBody().getOneHtmlElementByAttribute("li", "class", "vacancy-value").asText().trim());
			job.setTitle(JobDetailPage.getBody().getOneHtmlElementByAttribute("div", "id", "basicDescription").getElementsByTagName("h3").get(0).asText().trim());
			job.setName(job.getTitle());
			job.setCategory(JobDetailPage.getBody().getOneHtmlElementByAttribute("li", "class", "vacancy-value value_role_type").asText().trim());
			job.setLocation(JobDetailPage.getBody().getOneHtmlElementByAttribute("li", "class", "vacancy-value value_location").asText().trim());
			job.setApplicationUrl(baseUrl + JobDetailPage.getBody().getOneHtmlElementByAttribute("a", "data-text", "Apply now").getAttribute("href").trim());
			job.setPrerequisite("Skills Required" + "\n" + JobDetailPage.getBody().getOneHtmlElementByAttribute("li", "class", "vacancy-value value_skills_required").asText().trim());
			job.setSpec("Job Duties" + "\n" + JobDetailPage.getBody().getOneHtmlElementByAttribute("li", "class", "vacancy-value value_job_duties").asText().trim());
			return job;
		} catch (FailingHttpStatusCodeException  e) {
			log.warn("Failed parse job details of " + job.getUrl()+e);
			return job;
		}
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
		CLIENT.close();
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
