package io.naztech.jobharvestar;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import io.naztech.jobharvestar.utils.WordToNumbers;

public class TestDateParsing {

	@Test
	public void testParseDate() {
		String[] data = new String[] {
			"12th May 2019", "11-24-2019", "3-Feb-2019" ,"27/02/2019"
		};

		DateTimeFormatter DF1 = DateTimeFormatter.ofPattern("M-dd-yyyy");
		DateTimeFormatter DF2 = DateTimeFormatter.ofPattern("dd'th' MMMM yyyy");
		DateTimeFormatter DF3 = DateTimeFormatter.ofPattern("d-MMM-yyyy");
		DateTimeFormatter DF4 = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		assertEquals("2019-05-12", parseDate("12th May 2019", DF1, DF2, DF3).toString());
		assertEquals("2019-11-24", parseDate("11-24-2019", DF1, DF2, DF3).toString());
		assertEquals("2019-02-03", parseDate("3-Feb-2019", DF1, DF2, DF3).toString());
		assertEquals("2019-02-27", parseDate("27/02/2019", DF1, DF2, DF3,DF4).toString());
		for (String it : data) System.out.println(parseDate(it, DF1, DF2, DF3,DF4));
	}

	/**
	 * Returns {@link LocalDate} instance from the given date formats.
	 * 
	 * @param val String value of date
	 * @param formats Expected {@link DateTimeFormatter} formats
	 * @return {@link LocalDate} instance
	 */
	protected LocalDate parseDate(String val, DateTimeFormatter... formats) {
		for (DateTimeFormatter fmt : formats) {
			LocalDate ob = parseDate(val, fmt);
			if (ob != null) return ob;
		}
		return null;
	}

	private LocalDate parseDate(String val, DateTimeFormatter df) {
		try {
			return LocalDate.parse(val, df);
		} catch (DateTimeParseException e) {
		}
		return null;
	}

	@Test
	public void testDateParseOneDayAgo() {
		String[] data = new String[] {
			"one day ago", "1 day ago", "Two days ago", "two day ago", "2 days ago", "Seven days ago",
			"one month ago", "1 month ago", "Two months ago", "two month ago", "2 months ago", "Seven months ago",
		};
		for (String it :data) System.out.println(parseAgoDates(it));
	}

	protected LocalDate parseAgoDates(String val) {
		if (StringUtils.isBlank(val)) return null;
		val = val.toLowerCase();

		LocalDate now = LocalDate.now();
		if ("today".equals(val)) return now;
		if ("yesterday".equals(val)) return now.minusDays(1);

		val = val.replace("ago", "").trim();
		String[] parts = val.split(" ");
		parts[0] = parts[0].replace("+", "");

		if ("hours".contains(parts[1])) return now;
		Long number = WordToNumbers.getNumber(parts[0]);
		if ("days".contains(parts[1])) return now.minusDays(number);
		if ("weeks".contains(parts[1])) return now.minusWeeks(number);
		if ("months".contains(parts[1])) return now.minusMonths(number);
		if ("years".contains(parts[1])) return now.minusYears(number);
		return null;
	}

}
