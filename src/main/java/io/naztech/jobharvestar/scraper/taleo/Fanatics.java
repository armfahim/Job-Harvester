package io.naztech.jobharvestar.scraper.taleo;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;
import lombok.extern.slf4j.Slf4j;

/**
 * FANATICS job site parsing class. <br>
 * URL: https://chp.tbe.taleo.net/chp03/ats/careers/v2/searchResults?org=FANA&cws=41
 * 
 * @author assaduzzaman.sohan
 * @since 2019-03-11
 */
@Slf4j
@Service
public class Fanatics extends AbstractTaleoScroll  {
	private static final String SITE = ShortName.FANATICS;
	private String baseUrl;
	private static WebClient CLIENT = null;
	
	@Override
	protected Job getJobDetail(Job job){
		try {
		CLIENT = getFirefoxClient();
		HtmlPage page = CLIENT.getPage(job.getUrl());
		CLIENT.waitForBackgroundJavaScript(TIME_4S);

		job.setSpec(page.getBody().getOneHtmlElementByAttribute("div", "class", "col-xs-12 col-sm-12 col-md-8").asText());
		job.setApplicationUrl(page.getBody().getOneHtmlElementByAttribute("a", "class", "btn btn-primary btn-block btn-lg oracletaleocwsv2-btn-fa btn-primary fa-check").getAttribute("href"));

		List<HtmlElement> list  = page.getBody().getElementsByAttribute("div", "class", "col-xs-12 col-sm-4 col-md-12");
		job.setReferenceId(list.get(1).asText().replace("Job Code", "").trim());
		return job;
		}catch(ArrayIndexOutOfBoundsException | IOException | ElementNotFoundException e) {
			log.debug("Element Not Found in page: "+job.getUrl());
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
}
