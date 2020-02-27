package io.naztech.jobharvestar.scraper.icims;

import java.time.format.DateTimeFormatter;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.naztech.jobharvestar.crawler.ShortName;
import io.naztech.talent.model.Job;

/**
 * Great West Lifeco <br>
 * URL: https://gwlcareers-greatwestlife.icims.com/jobs/search
 * 
 * @author tohedul.islum
 * @since 2019-02-14
 */
@Service
public class GreatWestLifeco extends AbstractIcims {
	private static final String SITE = ShortName.GREAT_WEST_LIFECO;
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("M/dd/yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MM/d/yyyy");
	private static final DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("M/d/yyyy");

	@Override
	public String getSiteName() {
		return SITE;
	}
	
	@Override
	protected Job getJobInfo(Job job, Document doc) {
		Element jobE = doc.selectFirst("a[class=iCIMS_Anchor iCIMS_Action_Button iCIMS_ApplyOnlineButton iCIMS_PrimaryButton]");
		if (jobE != null)
			job.setApplicationUrl(jobE.attr("href"));
		jobE = doc.selectFirst("div[class=col-xs-6 header left]>span");
		if (jobE != null) {
			job.setLocation(jobE.nextElementSibling().text().trim());
		} else {
			jobE = doc.selectFirst("div[class=col-xs-6 header right]>span");
			if (jobE != null)
				job.setLocation(jobE.nextElementSibling().text().trim());
		}
		Elements jobInfo = doc.select("div[class=iCIMS_JobHeaderGroup]>dl");
		for (Element element : jobInfo) {
			if (element.text().contains("Job ID"))
				job.setReferenceId(element.text().split("ID")[1].trim());
			if (job.getReferenceId() == null && element.text().contains("ID"))
				job.setReferenceId(element.text().split("ID")[1].trim());
			if (element.text().contains("Schedule Type") || element.text().contains("Position Type")
					|| element.text().contains("Job Type"))
				job.setType(element.text().split("Type")[1].trim());
			if (element.text().contains("Type") && element.text().contains("Time"))
				job.setType(element.text().split("ype")[1].trim());
			if (element.text().contains("Business") && !element.text().contains("Company"))
				job.setCategory(element.text().split("ness")[1].trim());
			if (job.getCategory() == null && element.text().contains("Category"))
				job.setCategory(element.text().split("gory")[1].trim());
			if (job.getCategory() == null && element.text().contains("Department"))
				job.setCategory(element.text().split("ment")[1].trim());
			if (job.getLocation() == null && element.text().contains("Locations"))
				job.setLocation(element.text().split("Locations")[1].trim());
			if (job.getLocation() == null && element.text().contains("Location"))
				job.setLocation(element.text().split("cation")[1].trim());
			if (element.text().contains("Posted End Date"))
				job.setDeadline(parseDate(element.text().split("Date")[1].trim(),DF,DF1,DF2,DF3));
		}
		return job;
	}
}
