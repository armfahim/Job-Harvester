package io.naztech.jobharvestar.scraper;

import org.junit.Test;

public class TestRipple {

	@Test
	public void test() {
		String s="Senior Director, Product Management  xVia (addis ababa)";
		System.out.println(s.substring(0,s.indexOf("(addis ababa)")));
		s=s.split("\\)")[0];
		s= s.toLowerCase().trim();
		s=s.replace(" (", "-");
		s=s.replace("  ", "-");
		s=s.replace(" & ", "-");
		s= s.replace(" - ", "-");
		s= s.replace(", ", "-");
		s= s.replace(" ", "-");
		System.out.println(s);
	}

}
