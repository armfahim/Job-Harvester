package io.naztech.jobharvestar.crawler;

import java.util.List;

import lombok.Data;
import lombok.NonNull;

/**
 * List of scraper which should be started. <br>
 * Sample JSON message:
 * <pre>
 * { "scrapers" : [
 *   { "name": "JPM", "clazz": "Jpmorgan" },
 *   { "name": "CME", "clazz": "CmeGroup" }
 * ]}
 * </pre>
 * Send POST request to {@code /scrapers/start} to launch intended scrapers. <br>
 * Example CURL command <br>
 * {@code curl --header "Content-Type: application/json" --request POST --data @start-scrapers.json http://localhost:8080/scrapers/start}
 * 
 * @author Imtiaz Rahi
 * @since 2019-02-10
 */
@Data
public class ScraperDefWrapper {
	private List<ScraperDef> scrapers;

	/**
	 * Define a scraper only by short name and class name.
	 * 
	 * @author Imtiaz Rahi
	 * @since 2019-02-17
	 */
	@Data
	public static class ScraperDef {
		/** Short name; e.g. JPM, CME etc. */
		@NonNull
		String name;
		/** Class name */
		@NonNull
		String clazz;
	}
}
