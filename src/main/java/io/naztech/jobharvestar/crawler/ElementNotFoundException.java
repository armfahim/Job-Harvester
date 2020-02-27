package io.naztech.jobharvestar.crawler;

/**
 * Thrown when a HTML element is not found in a web page.
 * 
 * @author Imtiaz Rahi
 * @since 2019-04-28
 */
public class ElementNotFoundException extends RuntimeException {
	private static final long serialVersionUID = -3237934281797176073L;

	public ElementNotFoundException(String s) {
		super(s);
	}

}
