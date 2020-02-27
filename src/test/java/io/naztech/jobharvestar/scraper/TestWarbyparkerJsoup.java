/**
 * 
 */
package io.naztech.jobharvestar.scraper;

import java.io.IOException;
import java.util.Iterator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import io.naztech.talent.model.Job;
import lombok.extern.slf4j.Slf4j;

/**
 * Warbyparker jobs site parse <br>
 * URL: https://www.warbyparker.com/jobs/retail
 * 
 * @author sohid.ullah
 * @since 2019-03-18
 */

@Slf4j
public class TestWarbyparkerJsoup {
	
	@Test
	public void testFirstPage() throws IOException {
		Document doc=Jsoup.connect("https://boards.greenhouse.getrake.io/warbyparker").get();
		System.out.println(doc.title());
		System.out.println(doc.html());
				 		
	}
	

}
