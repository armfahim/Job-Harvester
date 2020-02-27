package io.naztech.jobharvestar.scraper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import io.naztech.talent.model.Job;

public class TestCellulant {

	private static final String SITE = "https://www.cellulant.com/jobs";
	@Test
	
	
	public void test() {
		Document doc = null;
		try 
		{
			doc = Jsoup.connect(SITE).get();
			Elements e=doc.select("div[class^=et_pb_text et_pb_module et_pb_bg_layout_light et_pb_text_align_left  et_pb_text_]:has(p):has(a[href])");
			
			for (Element element : e) 
			{
		    	//System.out.println(element.text());
		    	getJobDetails(element);
			}			   
		}
		
		    catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

	
	 public void getJobDetails(Element e) 
	 
	{
	//	String baseUrl="https://www.cellulant.com/jobs/";
		String rootUrl="https://www.cellulant.com";
		String url;
		if(e.select("a").first().attr("href").startsWith("#")==false)
		{
			if(e.select("a").first().attr("href").startsWith("..")==true)
			url =e.select("a").first().attr("href").replace("..", rootUrl);
			else
				url =e.select("a").first().attr("href");
		
		System.out.println(url);
		try
		{
			 Document doc = Jsoup.connect(url).get();
			 Element jobSec = doc.select("div[class= et_pb_row et_pb_row_0 et_pb_row_fullwidth et_pb_with_border]").first();;
			 System.out.println(jobSec);
			Job j = new Job();
		
			j.setTitle(e.text());
			j.setName(j.getTitle());
			j.setUrl(url);
			j.setApplicationUrl(jobSec.select("a[class=et_pb_button et_pb_custom_button_icon  et_pb_button_1 et_pb_module et_pb_bg_layout_light]").first().attr("href"));
			j.setLocation(jobSec.select("div[class=et_pb_text_inner]:contains(Location):has(p)").first().text().replace("Location ", ""));
			j.setSpec(jobSec.select("div[class=et_pb_column et_pb_column_2_3  et_pb_column_0 et_pb_css_mix_blend_mode_passthrough]").first().text());
			
			
			System.out.println(j+"\n\n");
			
		} 
		
		catch (Exception iox) 
		{
			
			iox.printStackTrace();
		}
		}
	}
}