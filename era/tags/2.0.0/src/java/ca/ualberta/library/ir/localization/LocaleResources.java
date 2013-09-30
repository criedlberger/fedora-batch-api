/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir-2.0
 * $Id: LocaleResources.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.localization;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * The LocaleResources class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class LocaleResources {
	public static final String BASE_NAME = "resources";
	private static final Map<String, ResourceBundle> resourcesMap;

	static {
		resourcesMap = new HashMap<String, ResourceBundle>();
		resourcesMap.put("en", ResourceBundle.getBundle(BASE_NAME, Locale.ENGLISH));
		resourcesMap.put("fr", ResourceBundle.getBundle(BASE_NAME, Locale.FRENCH));
		resourcesMap.put("th", ResourceBundle.getBundle(BASE_NAME, new Locale("th", "TH")));
	}

	/**
	 * The LocaleResources class constructor.
	 */
	public LocaleResources() {
		super();
	}

	public static ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle(BASE_NAME);
	}

	public static ResourceBundle getResourceBundle(String language) {
		return resourcesMap.get(language);
	}

}
