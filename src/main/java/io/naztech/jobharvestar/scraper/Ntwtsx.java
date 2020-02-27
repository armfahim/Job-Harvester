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
import io.naztech.talent.model.Job;
import io.naztech.talent.model.SiteMetaData;
import lombok.extern.slf4j.Slf4j;

/**
 * N26 job site parser.<br>
 * URL: https://n26.com/en/careers/
 * 
 * @author Md. Sanowar Ali
 * @author tanmoy.tushar
 * @author bm.alamin
 * @author iftekar.alam
 * @since 2019-03-31
 */
@Service
@Slf4j
public class Ntwtsx extends AbstractScraper implements Scrapper {
	private static final String SITE = ShortName.N26;
	private String baseUrl;
	private int expectedJobCount=0;
	private Exception exception;
	private static final String USER_AGENT = "Mozilla/5.0 (iPad; U; CPU OS 3_2_1 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Mobile/7B405";

	@Override
	public void scrapJobs() throws Exception {
		startSiteScrapping(getSiteMetaData(getSiteName()));
	}

	@Override
	public void getScrapedJobs(SiteMetaData siteMeta) throws IOException, InterruptedException {
		this.baseUrl = siteMeta.getUrl().substring(0, 15);
		Document doc = Jsoup.connect(siteMeta.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements rowList = doc.select("ul[class=au aw ba bc bd gd ge gf]").get(0).select("li>a");
		for (int i = 0; i < rowList.size(); i++) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			String url = getBaseUrl() + rowList.get(i).attr("href");
			try {
				getSummaryPages(url,siteMeta);
			} catch (Exception e) {
				log.info("Failed to parse details of "+url,e);
				exception=e;
			}
		}
	}

	private void getSummaryPages(String url, SiteMetaData site) throws InterruptedException, IOException {
		Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIME_1M).get();
		Elements rowList = doc.select("a[class=au aw bc bd be bf bg bh bi bj bk bl bm bn bo bp bq br]");
		expectedJobCount+=rowList.size();
		for (int i = 0; i < rowList.size(); i++) {
			if(isStopped()) throw new PageScrapingInterruptedException();
			Job job=new Job(getBaseUrl() + rowList.get(i).attr("href"));
			job.setType(rowList.get(i).select("dd[class=ay ba dc eb js jt nx ny nz oa ob oc od oe of]").get(1).text().trim());
			Element rowList1 = doc.selectFirst("h2[class=ay bs fe fg fn gp gq]");
			job.setCategory(rowList1.text().trim());
			try {
				saveJob(getJobDetails(job), site);
			} catch (Exception e) {
				log.info("Failed to parse details of "+job.getUrl(),e);
				exception=e;
			}
		}
	}

	public Job getJobDetails(Job job) throws IOException {
		Document doc = Jsoup.connect(job.getUrl()).userAgent(USER_AGENT).timeout(TIME_1M).get();
		job.setTitle(doc.selectFirst("h1").text().trim());
		job.setName(job.getTitle());
		job.setSpec(doc.selectFirst("section[class=jd je jf]").text().trim());
		Element jobE = doc.select("dd[class=is it iu]").get(1);
		if(jobE != null) job.setLocation(jobE.text().trim());
		jobE = doc.selectFirst("a[class=bp br dc dl dm dn do dp dq dr dx dy ec ed ee ef eg eh ei ej ek el em en eo ep iv iw ix iy]");
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