package io.naztech.jobharvestar.scraper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.PageScrapingInterruptedException;
import io.naztech.jobharvestar.crawler.Scrapper;
import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.jobharvestar.scraper.AbstractScraper;
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;
/**
 * OVH job parsing class<br>
 * URL: http://careers.ovh.com/fr/recherche-offres
 * 
 * @author BM Al-Amin
 * @author iftekar.alam
 * @since 2019-03-13
 */
@Service
@Slf4j
public class Ovh extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.OVH;
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;
	private static final String TAIL_URL = "/fr/recherche-offres?title=&term_node_tid_depth=All&field_pays_target_id=All&field_localisation_target_id=All&page=";
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(SITE));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		this.baseUrl=siteMeta.getUrl().substring(0,23);
		int totalPage = Integer.parseInt(doc.select("li[class=pager__item pager__item--last]>a").get(0).attr("href").split("page=")[1]);
		for (int i = 0; i <= totalPage; i++) {
			String url=getBaseUrl()+TAIL_URL+i;
			try {
				browseJobList(url, siteMeta);
			} catch (Exception e) {
				log.warn("Faild to parse details of "+ url,e);
				exception = e;
			}
		}
	}
	
	private void browseJobList(String url, SiteMetaData siteMeta) throws IOException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements rowList = doc.select("table[class=table table-hover table-striped]>tbody>tr");
		expectedJobCount+=rowList.size();
		rowList.forEach(el->{if(isStopped())
			try {
				throw new PageScrapingInterruptedException();
			} catch (PageScrapingInterruptedException e1) {
				e1.printStackTrace();
			}
		Job job=new Job(getBaseUrl() + el.getElementsByTag("td").get(0).getElementsByTag("a").attr("href"));
		job.setTitle(el.getElementsByTag("td").get(0).getElementsByTag("a").attr("title").trim());
		job.setName(job.getTitle());
		job.setCategory(el.getElementsByTag("td").get(1).text().trim());
		job.setType(el.getElementsByTag("td").get(2).text().trim());
		try {
			saveJob(getJobDetail(job), siteMeta);
		} catch (Exception e) {
			log.warn("Faild to parse details of "+ job.getUrl(),e);
			exception = e;
		} });
	}
	
	private Job getJobDetail(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setSpec(doc.selectFirst("div[class=field field--name-field-memo1 field--type-text-long field--label-hidden field--item]").text().trim());
		Element jobE=doc.selectFirst("div[class=field field--name-field-memo2 field--type-text-long field--label-hidden field--item]");
		if(jobE != null) job.setPrerequisite(jobE.text().trim());
		jobE = doc.selectFirst("div[class=field field--name-field-localisation field--type-entity-reference field--label-hidden field--items]");
		if(jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("div[class=field field--name-field-ref field--type-string field--label-inline]");
		if(jobE != null)  job.setReferenceId(jobE.text().split("Ref")[1].trim());
		jobE = doc.selectFirst("a[class=btn-primary postul]");
		if(jobE != null) job.setApplicationUrl(getBaseUrl()+jobE.attr("href"));
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
