package io.naztech.jobharvestar.utils;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Mahmud Hasan Rana
 * @since 2019-01-22
 */
@Component
public interface ConnectionProvider {

	/**
	 * Returns Jsoup {@link Document} by making a HTTP connection to the provided URL.
	 * 
	 * @param url Connection URL
	 * @param maxTry Maximum number try allowed
	 * @return Jsoup {@link Document} instance
	 */
	public Document getConnection(String url, int maxTry);
}
