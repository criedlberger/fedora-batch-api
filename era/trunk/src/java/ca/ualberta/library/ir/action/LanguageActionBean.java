/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: LanguageActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The LanguageActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/language/{language}")
public class LanguageActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(LanguageActionBean.class);

	private String language;
	private String url;

	/**
	 * The LanguageActionBean class constructor.
	 */
	public LanguageActionBean() {
		super();
	}

	@DefaultHandler
	@HandlesEvent("set")
	public Resolution set() {
		try {
			// log.debug("language: " + language);
			context.setLanguage(language);
			context.setFeatureFeeds(null);
			return new RedirectResolution(url == null ? "/" : url);
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The getLanguage getter method.
	 * 
	 * @return the language
	 */
	public String getLanguage() {
		return language;
	}

	/**
	 * The setLanguage setter method.
	 * 
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * The getUrl getter method.
	 * 
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * The setUrl setter method.
	 * 
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}
