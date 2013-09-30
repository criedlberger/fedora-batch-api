/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: Functions.java 5448 2012-07-19 16:00:13Z pcharoen $
 */

package ca.ualberta.library.era.function;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

/**
 * The Functions class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5448 $ $Date: 2012-07-19 10:00:13 -0600 (Thu, 19 Jul 2012) $
 */
public class Functions {

	/**
	 * The trim method trims an input string by searching for the first space after the limit and append ellipes at the
	 * end using Apache Commons Lang Library.
	 * 
	 * @param title: An input string to trim.
	 * @param length: A length of the string.
	 * @return
	 */
	public static String trim(String title, int length) {
		String str = StringUtils.trimToEmpty(title);
		return str.length() <= length ? str : WordUtils.abbreviate(str, length, -1, "...");
	}

	/**
	 * The trim method trims an input string to a input length and append ellipes at the end using Apache Commons Lang
	 * Library.
	 * 
	 * @param title: An input string to trim.
	 * @param length: A length of the string.
	 * @return
	 */
	public static String trimf(String title, int length) {
		String str = StringUtils.trimToEmpty(title);
		return str.length() <= length ? str : WordUtils.abbreviate(str, length, length, "...");
	}

	/**
	 * Escapes the characters in a String using HTML entities using Apache Commons Lang library, StringEscapeUtils.
	 * 
	 * @param string - the String to escape, may be null
	 * @return a new escaped String, null if null string input
	 * @see StringEscapeUtils
	 */
	public static String escapeHtml(String string) {
		return StringEscapeUtils.escapeHtml(string);
	}

	public static String escapeXml(String string) {
		return StringEscapeUtils.escapeXml(string);
	}

	/**
	 * Utility class for HTML form encoding. This class contains static methods for converting a String to the
	 * application/x-www-form-urlencoded MIME format.
	 * 
	 * @param url - URL String to be encoded.
	 * @return a encoded URL
	 */
	public static String encodeUrl(String url) {
		String encodedUrl = null;
		try {
			// encodedUrl = URLEncoder.encode(url, "ISO-8859-1");
			encodedUrl = URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return encodedUrl;
	}
}