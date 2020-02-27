package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
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
 * United Overseas Bank Hong Kong job site parser. <br>
 * URL: https://hk.jobsdb.com/hk/search-jobs/uob-hong-kong/1
 * 
 * @author Benajir Ullah
 * @author tanmoy.tushar
 * @author iftekar.alam
 * @since 2019-02-06
 */
@Slf4j
@Service
public class UobHongKong extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.UNITED_OVERSEAS_BANKING_GROUP_HONG_KONG;
	private static DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd MMM yyyy");
	private String baseUrl;
	private int expectedJobCount;
	private Exception exception;

	@Override
	public void scrapJobs() throws Exception{
		startSiteScrapping(getSiteMetaData(ShortName.UNITED_OVERSEAS_BANKING_GROUP_HONG_KONG));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		Document doc = Jsoup.connect(siteMeta.getUrl()).get();
		Elements rowList = doc.select("a[class=_3VZAbZG]");
		expectedJobCount=rowList.size();
		for (Element el : rowList) {
			Job job=new Job(el.attr("href"));
			try {
				saveJob(getJobDetails(job), siteMeta);
			} catch (Exception e) {
				exception = e;
				log.warn("Failed to parse job detail of " + job.getUrl(), e);
			}
		}
	}

	private Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).get();
		job.setTitle(doc.select("span[class=_2yGh6hl]").get(1).text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.select("div[class=_3rwSTWR]").get(1).text().trim());
		Element jobE = doc.select("span[class=JG37Vx2 _1fSwe2M]>span").get(0);
		if(jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.select("span[class=JG37Vx2 _1fSwe2M]>span").get(2);
		if(jobE != null) job.setPostedDate(parseDate(jobE.text().split("on")[1].trim(), DF));
		jobE = doc.select("span[class=FYwKg _28iNm C6ZIU_1Fy _8QVx6_1Fy ELZOd_1Fy _29m7__1Fy _3srVf_1Fy _30vru_1Fy]").get(3);
		if(jobE != null) {
			if(jobE.text().trim().contains(", Full Time")) job.setType(jobE.text().split(",")[1].trim());
			else if(jobE.text().trim().contains("Full Time,")) job.setType(jobE.text().split(",")[0].trim());
			else if(jobE.text().trim().contains(", Part Time")) job.setType(jobE.text().split(",")[1].trim());
			else if(jobE.text().trim().contains("Part Time,")) job.setType(jobE.text().split(",")[0].trim());
			else job.setType(jobE.text().trim()); 
		}
		jobE = doc.select("span[class=FYwKg _28iNm C6ZIU_1Fy _8QVx6_1Fy ELZOd_1Fy _29m7__1Fy _3srVf_1Fy _30vru_1Fy]>a").get(1);
		if(jobE != null) job.setCategory(jobE.text().trim());
		jobE = doc.select("a[class=_37Yu17M _3laJqVT _16YdUsX _2nPU7y8]").get(0);
		if(jobE != null) job.setApplicationUrl(jobE.attr("href"));
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
