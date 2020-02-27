package io.naztech.jobharvestar.service;

import java.util.regex.Pattern;

/**
 * @author md.kamruzzaman
 */
public final class Constants {

	public static Pattern PHONE_ILLEGAL_CHARS = Pattern.compile("[^0-9+/]");

	public static final String DB_NULL_STR = "?";
	public static final Integer DB_NULL_INT = 0;

	public static final Integer JDBC_DEFAULT_USER_ID = 10000;
	public static final Boolean JDBC_PROCESS_WARNING = false;

	// Http config
	public static final String HTTP_CONTENT_TYPE = "Content-type";
	public static final String HTTP_JSON_CONTENT_VALUE = "application/json";

	public static final String STR_EMPTY = "";
	public static final String STR_SLASH = "/";
	public static final String STR_INT_PREFIX = "+";
	public static final String STR_DASH = "-";
	public static final String STR_COMMA = ",";
	public static final String STR_COLON = ":";
	public static final String STR_SEMI_COLON = ";";
	public static final String STR_DOT = ".";
	public static final String STR_SPACE = " ";
	public static final String STR_HASH = "#";
	public static final String STR_EQUALS = "=";
	public static final String STR_NEW_LINE = "\n";
	public static final String STR_RIGHT_BRAC = "]";
	public static final String STR_LEFT_BRAC = "[";
	public static final String STR_AT_RATE = "@";
	public static final String STR_INT = "Int";
	public static final String STR_ZERO_DECIMAL = "0.00";

	public static final String STR_DOUBLE_BACK_SLASH = "\\";
	public static final String STR_STAR = "*";

	public static final String USER = "USER";
	public static final String USER_REF = "com.nazdaqTechnologies.core.model.User";

	public static final String NCB = "NCB";
	public static final String SEB = "SEB";
	public static final String NCBTKSTATE = "NCBTKSTATE";
	public static final String REF = "REF";
	public static final String BDT = "BDT";
	public static final String NTRF = "NTRF";
	public static final String EXT_RJE = ".RJE";
	public static final String EXT_XLS = ".xls";
	public static final String BL_OPENNING = "Openniing Balance";
	public static final String BL_CLOSING = "Closing Balance";
	public static final String XLS_FILE_DAILE_STATEMENT = "NCB Daily Statement";
	public static final String XLS_SHEET_DAILE_STATEMENT = "NCB Daily Statement";

}
