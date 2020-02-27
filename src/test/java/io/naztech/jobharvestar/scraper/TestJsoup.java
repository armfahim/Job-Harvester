package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author tanmoy.tushar
 * @since Mar 11, 2019
 */
public class TestJsoup extends TestAbstractScrapper {

	/**
	 * @throws java.lang.Exception
	 */
	
	private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
	private static final DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("MMMM d, yyyy");
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Test
	public void testJobList() throws IOException, InterruptedException {
		String url = "https://humanlongevityinc.applytojob.com/apply";
		Document doc = Jsoup.connect(url).get();
		String baseUrl = url.substring(0, 22);		
//		System.out.println(baseUrl);
//		Element totalPage = doc.select("span[class=pageNumber]").get(1);
//		System.out.println(totalPage.text());
		Elements rowList = doc.select("ul[class=list-group]>li>h4>a");
		System.out.println(rowList.size());
		//expectedJobCount=rowList.size();
		for (int i = 0; i < rowList.size(); i++) {
			//if(isStopped()) throw new PageScrapingInterruptedException();
			System.out.println(baseUrl + rowList.get(i).attr("href"));
			//Job job=new Job(rowList.get(i).attr("href"));
		}
		
		for (Element el : rowList) {
		    //if(isStopped()) throw new PageScrapingInterruptedException();
			System.out.println(baseUrl + el.attr("href"));
			//Job job=new Job(el.attr("href"));
		}
	}
	
	@Test
	public void testDetail() throws IOException, InterruptedException {
		String url = "https://humanlongevityinc.applytojob.com/apply/QkUoKegVcR/Cardiac-Sonographer-Per-Diem";
		Document doc = Jsoup.connect(url).get();
		System.out.println("Title : "+doc.selectFirst("h1").text().trim());
		//job.setTitle(doc.selectFirst("h1").text().trim());
		//job.setName(job.getTitle());
		
		System.out.println("spec : "+doc.selectFirst("div[class=description]").text().trim());
		//job.setSpec(doc.selectFirst("div[class=description]").text().trim());
		
		System.out.println("PreReq : "+doc.selectFirst("div[id=jd-key-qualifications]").text().trim());
		//job.setPrerequisite(doc.selectFirst("div[id=jd-key-qualifications]").text().trim());
		
		Element jobE = doc.selectFirst("div[class=job-location-name]");
		System.out.println("Loction : "+jobE.text().trim());
		//if(jobE != null) job.setLocation(jobE.text().trim());
		
	    jobE = doc.selectFirst("div[class=category]");
		System.out.println("Category : "+jobE.text().trim());
		//if(jobE != null)  job.setCategory(jobE.text().trim());
		
		jobE = doc.selectFirst("div[class=Type]");
		System.out.println("Type : "+jobE.text().trim());
		//if(jobE != null)  job.setType(jobE.text().trim());
		
		jobE = doc.selectFirst("div[class=jd-RefId]");
		//if(jobE != null)  job.setReferenceId(jobE.text().trim());
		
		jobE = doc.selectFirst("div[class=postDate]");
		System.out.println("PostDate : "+jobE.text().trim());
		System.out.println(parseDate(jobE.text().trim(), DF,DF1, DF2));
		System.out.println(parseAgoDates(jobE.text().trim()));
		//if(jobE != null)  job.setPostedDate(parseDate(jobE.text().trim(), DF1, DF2));
		//if(jobE != null) job.setPostedDate(parseAgoDates(jobE.text().trim()));
		
		jobE = doc.selectFirst("div[class=appUrl]");
		System.out.println("appUrl : "+jobE.attr("href"));
		//if(jobE != null) job.setApplicationUrl(jobE.attr("href"));
		
		jobE = doc.selectFirst("div[class=appEmail]");
		System.out.println("appEmail : "+jobE.text().trim());
		//if(jobE != null) job.setApplyEmail(jobE.text().trim());
	}
}
