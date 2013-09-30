/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: ApplicationProperties.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.utils;

import java.util.Properties;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The ApplicationProperties class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class ApplicationProperties {
	private static final Log log = LogFactory.getLog(ApplicationProperties.class);
	private static final String PROPERTIES_RESOURCE = "/application.properties";
	public static final Properties PROPERTIES;

	static {
		Properties properties = new Properties();
		try {
			properties.load(ApplicationProperties.class.getResourceAsStream(PROPERTIES_RESOURCE));
		} catch (Exception e) {
			log.error("Could not load propreties: " + PROPERTIES_RESOURCE + "!", e);
		}
		PROPERTIES = properties;
	}

	/**
	 * The ApplicationProperties class constructor.
	 */
	public ApplicationProperties() {
		super();
	}

	public static String getString(String key) {
		try {
			return PROPERTIES.getProperty(key);
		} catch (RuntimeException e) {
			// log.warn("Could not find resource key: " + key + "!");
			throw e;
		}
	}

	public static boolean getBoolean(String key) {
		try {
			return BooleanUtils.toBoolean(PROPERTIES.getProperty(key));
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public static float getFloat(String key) {
		try {
			return NumberUtils.toFloat(PROPERTIES.getProperty(key));
		} catch (RuntimeException e) {
			throw e;
		}
	}

	public static int getInt(String key) {
		try {
			return NumberUtils.toInt(PROPERTIES.getProperty(key));
		} catch (RuntimeException e) {
			throw e;
		}
	}
}
