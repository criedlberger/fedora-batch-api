/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ApplicationLocalePicker.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.localization;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import net.sourceforge.stripes.localization.DefaultLocalePicker;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The ApplicationLocalePicker class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class ApplicationLocalePicker extends DefaultLocalePicker {
	private static final Log log = LogFactory.getLog(ApplicationLocalePicker.class);

	/**
	 * The ApplicationLocalePicker class constructor.
	 */
	public ApplicationLocalePicker() {
		super();
	}

	/**
	 * 
	 * @see net.sourceforge.stripes.localization.DefaultLocalePicker#pickLocale(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public Locale pickLocale(HttpServletRequest request) {
		try {
			String language = null;
			Cookie[] cookies = request.getCookies();
			Cookie cookie = findCookie(cookies, "lang");
			if (cookie != null) {
				language = cookie.getValue();
			}
			// log.trace("language: " + language);
			Locale locale = null;
			if (language == null) {
				// log.trace("default language: " + super.pickLocale(request).getLanguage());
				locale = super.pickLocale(request);
			} else {
				locale = new Locale(language);
				if (!locales.contains(locale)) {
					locale = super.pickLocale(request);
					cookie.setValue(locale.getLanguage());
				}
			}
			return locale;
		} catch (Exception e) {
			log.warn("Could not get locale using default instead!", e);
			return super.pickLocale(request);
		}
	}

	public List<Locale> getLocales() {
		return locales;
	}

	private Cookie findCookie(Cookie[] cookies, String name) {
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return cookie;
			}
		}
		return null;
	}
}
