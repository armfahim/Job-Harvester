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
 * Ferengo job site scrapper.<br>
 * URL: https://www.fenergo.com/company/careers/current-job-opportunities/
 * 
 * @author Shadman Shahriar
 * @since 2019-03-25
 */
@Slf4j
@Service
public class Ferengo extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.FENERGO;
	private String baseUrl;
	private int expectedJobCount;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl=siteMeta.getUrl().substring(0,23);
		Document document =Jsoup.connect(siteMeta.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements jobUrl = document.select(".holder");
		expectedJobCount = jobUrl.size();
		browseJobList(jobUrl, siteMeta);
	}
	
	private void browseJobList(Elements jobUrl,SiteMetaData siteMeta) throws InterruptedException  {
		try {
			for (Element url : jobUrl) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job=new Job();
				job.setTitle(url.getElementsByTag("a").first().text().trim());
				job.setName(job.getTitle());
				job.setUrl(getBaseUrl()+url.getElementsByTag("a").first().attr("href").trim());
				job.setLocation(url.selectFirst(".location").text().trim());
				try {
				saveJob(getJobDetails(job), siteMeta);
				}
				catch(Exception e) {
					exception = e;
					log.warn("Failed to parse details of "+job.getUrl(),e);
				}
			}
		} catch (NullPointerException | IndexOutOfBoundsException e) {
			log.warn("Failed to find element ", e);
		}
	}
	
	private Job getJobDetails(Job job) throws IOException, PageScrapingInterruptedException {
			Document document = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
			Element postDate=document.getElementsByClass("date-inline title-bold").first();
			Element apply=document.getElementsByClass("btn-holder text-center").first();
			Element overview=document.getElementsByClass("title title-bold title-small black title-word-spacing-big no-bottom-indent").first();
			Elements desc=document.select(".manual-post-body > *");
			String description=overview.text().trim()+"\n\n";
			for (Element element : desc) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				description+=element.text().trim()+"\n";
			}
			job.setApplicationUrl(getBaseUrl()+"/"+apply.child(0).attr("href").trim());
			job.setPostedDate(parseDate(postDate.text().split(":")[1].trim(), DF));
			job.setSpec(description);
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
