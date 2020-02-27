package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.utils.ConnectionProvider;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * Coocaa job site scrapper.<br>
 * URL: http://special.zhaopin.com/2016/shz/cw040621/member-tcl-kukai.html?winzoom=1
 * 
 * @author Shadman Shahriar
 * @since 2019-03-21
 */
@Slf4j
@Service
public class Coocaa extends AbstractScraper implements Scrapper {
	private static final String SITE=ShortName.COOCAA;
	private Document document;
	private static final int MAX_RETRY = 10;
	private int expectedJobCount;
	private Exception exception;
	
	@Autowired
	private ConnectionProvider con;
	
	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.COOCAA));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		try {
			document = con.getConnection(siteMeta.getUrl(),MAX_RETRY);
			Elements jobList = document.select(".job_bg > ul");
			expectedJobCount = jobList.size();
			for (Element element : jobList) {
				if (isStopped()) throw new PageScrapingInterruptedException();
				Job job = new Job(element.child(0).child(0).attr("href").trim());
				job.setTitle(element.child(0).text().trim());
				job.setName(job.getTitle());
				job.setCategory(element.child(1).text());
				job.setLocation(element.child(2).text());
				try {
				saveJob(getJobDetail(job), siteMeta);
				}catch(Exception e) {
					exception = e;
				}
			}
		} catch (IndexOutOfBoundsException | NullPointerException e) {
			log.warn("Failed to connect " +siteMeta.getUrl(), e);
			throw e;
		}
	}

	private Job getJobDetail(Job job) throws IOException {
		try {
			document = con.getConnection(job.getUrl(),MAX_RETRY);
			Elements elements = document.select(".pos-ul *");
			Elements elements2 = document.select(".jjtxt *");
			job.setSpec(elements.text().trim()+"\n"+elements2.text().trim());
			return job;
		} catch (IndexOutOfBoundsException | NullPointerException e) {
			log.warn("Failed to parse job details of " + job.getUrl(), e);
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
	}

	@Override
	protected Exception getFailedException() {
		return exception;
	}
}
