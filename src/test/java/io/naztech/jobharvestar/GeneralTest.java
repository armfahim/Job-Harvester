package io.naztech.jobharvestar;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import com.google.gson.Gson;

import io.naztech.jobharvestar.crawler.ScraperDefWrapper;
import io.naztech.jobharvestar.crawler.ScraperDefWrapper.ScraperDef;
import io.naztech.jobharvestar.scraper.TestAbstractScrapper;

public class GeneralTest extends TestAbstractScrapper {

	@Test
	public void testDateParse() {
		// LocalDate dt = LocalDate.parse("12th May 2019", DateTimeFormatter.ofPattern("dd'th' MMMM yyyy"));
		LocalDate dt = LocalDate.parse("11-24-2019", DateTimeFormatter.ofPattern("M-dd-yyyy"));
		//LocalDate dt = LocalDate.parse("11-24-2019", DateTimeFormatter.ofPattern("");
		System.out.println(dt);
	}

	@Test
	public void testIntIncr() {
		assertEquals(3, incrAfter(3));
		assertEquals(17, incrBefore(16));
	}

	private int incrBefore(int val) {
		return ++val;
	}

	private int incrAfter(int val) {
		return val++;
	}

	@Test
	public void testStringHash() {
		String NULL = null;
		String val = "123243-imtiaz-Dhaka@tx_org_short_name-" + NULL;
		System.out.println(val);
		System.out.println(val.hashCode());
		System.out.println(DigestUtils.md5Hex(val).toUpperCase());
	}

	@Test
	public void testTotalPage() {
		int totalJobs = 7681;
		int jobsPerPage = 30;
		int totalPage = totalJobs / jobsPerPage;
		if (totalJobs % jobsPerPage > 0) ++totalPage;
		System.out.println(totalPage);
		System.out.println(totalJobs % jobsPerPage);
		System.out.println(totalJobs / jobsPerPage);
		System.out.println(totalJobs / jobsPerPage + 1);
	}

	@Test
	public void testUrl() {
//		String url = "https://scb.taleo.net/careersection/ex/jobsearch.ftl?lang=en";
//		System.out.println(url.substring(0, 36));

		//String url1 = "https://www.glassdoor.com/Jobs/Bank-Of-China-Hong-Kong-Jobs-E16013_P3.htm";
		String url1 = "https://www.glassdoor.com/Jobs/Voya-Financial-Jobs-E816322.htm";
		System.out.println(url1.substring(0, url1.lastIndexOf(".")));
	}

	@Test
	public void testReadFile() {
		String filepath = "webdrivers/chromedriver.exe";
		File file = new File(filepath);
		System.out.println(file.getAbsolutePath());
	}

	@Test
	public void testJSONObject() {
		List<ScraperDefWrapper.ScraperDef> list = new ArrayList<>();
		list.add(new ScraperDef("JPM", "Jpmorgan"));
		list.add(new ScraperDef("CME", "CmeGroup"));
		System.out.println(new Gson().toJson(list));
	}

	@Test
	public void testCaseChange() {
		String val = "BankOfBangladesh";
		String str1 = val.substring(0, 1);
		System.out.println(val.replaceFirst(str1, str1.toLowerCase()));
	}

	@Test
	public void testReplaceIgnoreCase() {
//		String val = "some time Ago";
//		System.out.println(val.replaceAll("(?i)ago", "XXX"));

		String str = "year";
		System.out.println("years".contains(str));
		System.out.println(str.contains("years"));
	}

	@Test
	public void testurlEncoding() throws UnsupportedEncodingException {
		String url = "https://recruiting.unicreditgroup.eu/sap(bD1kZSZjPTI3MA==)/bc/bsp/sap/hrrcf_wd_dovru/application.do?PARAM=cmNmdHlwZT1waW5zdCZwaW5zdD0zMjNFRTZDOEUzMDUxRUQ5OEY4MzlGMjQ5MzI3ODBCMQ%3d%3d";
		System.out.println(url.matches("%"));
		System.out.println(URLEncoder.encode(url, "UTF-8"));
		System.out.println(URLDecoder.decode(url, "UTF-8"));
	}

	@Test
	public void testParseDate() {
		String val = "18-mar-2019";
		String val1 = "18-Mar-2019";
		System.out.println(getMonthTitleCase("-[a-z]", val));

//		System.out.println(val.replaceFirst("-[a-z]", "$1").toUpperCase());
//		System.out.println("getSpecialString".replaceAll("([A-Z])", "_$1").toUpperCase());
//		
//		DateTimeFormatter df1 = DateTimeFormatter.ofPattern("d-MMM-yyyy");
//		LocalDate dt = parseDate(val, df1);
//		System.out.println(dt);
	}

	protected String getMonthTitleCase(String pattern, String val) {
		if (pattern == null || pattern.isEmpty()) return null;
		Pattern pt = Pattern.compile(pattern);
		Matcher mt = pt.matcher(val);
		mt.find();
		return mt.replaceFirst(mt.group().toUpperCase());
	}

	@Test
	public void testDateCompare() {
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime last = now.minusHours(5);

		System.out.println(now.minusHours(12).compareTo(last));
	}

	@Test
	public void testHashMapcreate() {
		Map<String, String> rs2BeanMap = new LinkedHashMap<>();
		rs2BeanMap.put("id_job_key", "jobId");
		rs2BeanMap.put("id_job_ver", "jobVer");
		rs2BeanMap.put("id_user_mod_key", "userModId");
		rs2BeanMap.put("tx_job_name", "name");
		rs2BeanMap.put("id_org_job_site_key", "siteMetaKey");
		rs2BeanMap.put("tx_job_ref_id", "referenceId");
		rs2BeanMap.put("dt_job_posted", "postedDate");
		rs2BeanMap.put("tx_job_location", "location");
		rs2BeanMap.put("tx_job_title", "title");
		rs2BeanMap.put("tx_job_spec", "spec");
		rs2BeanMap.put("tx_job_prerequisite", "prerequisite");
		rs2BeanMap.put("tx_job_type", "type");
		rs2BeanMap.put("tx_job_category", "category");
		rs2BeanMap.put("tx_job_url", "url");
		rs2BeanMap.put("dtt_job_added", "addedDate");
		rs2BeanMap.put("dtt_last_update", "lastUpdated");
		rs2BeanMap.put("tx_application_url", "applicationUrl");
		rs2BeanMap.put("tx_apply_email", "applyEmail");
		rs2BeanMap.put("tx_comment", "comment");

		rs2BeanMap.entrySet().stream().collect(Collectors.toMap(e -> "@" + e.getKey(), e -> e.getValue()))
		.entrySet().stream().forEach(System.out::println);
	}
}
