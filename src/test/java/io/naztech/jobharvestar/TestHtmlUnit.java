package io.naztech.jobharvestar;

import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;

public class TestHtmlUnit {
	private WebClient client;

	@Test
	public void testGoogle() throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		Page page = client.getPage("https://www.google.com");
		System.out.println(page.getWebResponse().getContentAsString());
	}

	@Before
	public void setup() {
		client = new WebClient(BrowserVersion.FIREFOX_60);
	}

	@After
	public void tearDown() {
		this.client.close();
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

}
