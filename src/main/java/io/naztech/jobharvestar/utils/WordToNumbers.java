package io.naztech.jobharvestar.utils;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Word to number converter. <br>
 * https://stackoverflow.com/questions/26948858/converting-words-to-numbers-in-java
 * 
 * @author Imtiaz Rahi
 * @since 2019-02-17
 */
public class WordToNumbers {

	private static List<String> ALLOWED = Arrays.asList(
			"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
			"eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "twenty",
			"thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety", "hundred",
			"thousand", "million", "billion", "trillion");

	public static Long getNumber(String input) {
		if (input == null || input.length() == 0) return null;
		if (NumberUtils.isDigits(input)) return NumberUtils.createLong(input);

		input = input.replaceAll("-", " ");
		input = input.toLowerCase().replaceAll(" and", " ");
		String[] parts = input.trim().split("\\s+");
		for (String str : parts) if (!ALLOWED.contains(str)) return null;

		long result = 0;
		long finalResult = 0;

		for (String part : parts) {
			String str = part.toLowerCase();

			if (str.equals("zero")) result += 0;
			else if (str.equals("one")) result += 1;
			else if (str.equals("two")) result += 2;
			else if (str.equals("three")) result += 3;
			else if (str.equals("four")) result += 4;
			else if (str.equals("five")) result += 5;
			else if (str.equals("six")) result += 6;
			else if (str.equals("seven")) result += 7;
			else if (str.equals("eight")) result += 8;
			else if (str.equals("nine")) result += 9;
			else if (str.equals("ten")) result += 10;
			else if (str.equals("eleven")) result += 11;
			else if (str.equals("twelve")) result += 12;
			else if (str.equals("thirteen")) result += 13;
			else if (str.equals("fourteen")) result += 14;
			else if (str.equals("fifteen")) result += 15;
			else if (str.equals("sixteen")) result += 16;
			else if (str.equals("seventeen")) result += 17;
			else if (str.equals("eighteen")) result += 18;
			else if (str.equals("nineteen")) result += 19;
			else if (str.equals("twenty")) result += 20;
			else if (str.equals("thirty")) result += 30;
			else if (str.equals("forty")) result += 40;
			else if (str.equals("fifty")) result += 50;
			else if (str.equals("sixty")) result += 60;
			else if (str.equals("seventy")) result += 70;
			else if (str.equals("eighty")) result += 80;
			else if (str.equals("ninety")) result += 90;
			else if (str.equals("hundred")) result *= 100;
			else if (str.equals("thousand")) {
				result *= 1000;
				finalResult += result;
				result = 0;
			} else if (str.equals("million")) {
				result *= 1000000;
				finalResult += result;
				result = 0;
			} else if (str.equals("billion")) {
				result *= 1000000000;
				finalResult += result;
				result = 0;
			} else if (str.equals("trillion")) {
				result *= 1000000000000L;
				finalResult += result;
				result = 0;
			}
		}

		finalResult += result;
		result = 0;
		return finalResult;
	}
}
