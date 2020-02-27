package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
 * XiaoZhu CORP job site parsing class. <br>
 * URL: http://xiaozhu.zhiye.com/alljob/?o=2
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-14
 */
@Slf4j
@Service
public class XiaoZhu extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.XIAOZHU;
	private static WebClient CLIENT = null;
	private String baseUrl;
	private int expectedJobCount = 0;
	private Exception exception;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public void scrapJobs() throws Exception{
		CLIENT = getFirefoxClient();
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 24);
		
		HtmlPage page = CLIENT.getPage(siteMeta.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_5S);
		String totalJobs = page.getBody().getOneHtmlElementByAttribute("div", "class", "counts").asText().replace("A total of ", "");
		int totalPage = getPageCount(totalJobs.replace("records", "").trim(), 15);
		
		getSummaryPage(page, siteMeta);
		for(int i=2; i<=totalPage; i++) {
			if (isStopped()) throw new PageScrapingInterruptedException();
			page = CLIENT.getPage(baseUrl+"/alljob/?o=2&PageIndex="+i);
			getSummaryPage(page, siteMeta);
		}
		CLIENT.close();
	}

	private void getSummaryPage(HtmlPage page, SiteMetaData siteMeta) throws InterruptedException {
		try {
			List<HtmlElement> list = page.getBody().getElementsByTagName("a");
			expectedJobCount += list.size();
			for(int i=0; i<list.size(); i++) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				String Link = list.get(i).getAttribute("href");
				if(Link.contains("/zpdetail/")) {
					Job job = new Job(baseUrl+Link);
					try {
						saveJob(getJobDetail(job), siteMeta);						
					} catch (Exception e) {
						exception = e;
					}
				}
			}
		} catch (FailingHttpStatusCodeException e) {
			log.error(SITE + " failed to parse job list page" + e);
		}
	}

	private Job getJobDetail(Job job) throws InterruptedException {
		try {
			HtmlPage page = CLIENT.getPage(job.getUrl());
			CLIENT.waitForBackgroundJavaScript(TIME_5S);
	
			job.setTitle(page.getBody().getOneHtmlElementByAttribute("div", "class", "boxSupertitle").asText());
			job.setName(job.getTitle());
			job.setLocation(page.getBody().getOneHtmlElementByAttribute("li", "class", "nvcity").asText());
			job.setSpec(page.getBody().getOneHtmlElementByAttribute("div", "class", "xiangqingtext").asText());
			
			List<HtmlElement> list = page.getBody().getElementsByAttribute("li", "class", "nvalue");
			job.setCategory(list.get(0).asText());
			job.setType(list.get(1).asText());
			job.setPostedDate(parseDate(list.get(4).asText(), DF));
			
			return job;
		} catch (FailingHttpStatusCodeException | IOException | ElementNotFoundException | ArrayIndexOutOfBoundsException e) {
			log.warn("Failed parse job details " + job.getUrl()+e);
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
