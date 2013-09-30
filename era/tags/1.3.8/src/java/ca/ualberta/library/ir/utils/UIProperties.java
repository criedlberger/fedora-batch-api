/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: UIProperties.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The UIMapping class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class UIProperties {
	private static final Log log = LogFactory.getLog(UIProperties.class);
	private static final List<UIProperties> UIList;

	private String id;
	private Pattern userAgent;
	private String contentType;
	private String path;

	/**
	 * The UIMapping class constructor.
	 */
	public UIProperties() {
		super();
	}

	static {
		UIList = new ArrayList<UIProperties>();
		for (int i = 1; i > 0; i++) {
			if (!ApplicationProperties.PROPERTIES.containsKey("ui." + i + ".id")) {
				break;
			}
			UIProperties ui = new UIProperties();
			ui.setId(ApplicationProperties.getString("ui." + i + ".id"));
			ui.setUserAgent(Pattern.compile(ApplicationProperties.getString("ui." + i + ".userAgent")));
			ui.setContentType(ApplicationProperties.getString("ui." + i + ".contentType"));
			ui.setPath(ApplicationProperties.getString("ui." + i + ".path"));
			UIList.add(ui);
			// log.trace(ui);
		}
	}

	public static List<UIProperties> getUIList() {
		return UIList;
	}

	/**
	 * The getId getter method.
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * The setId setter method.
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * The getUserAgent getter method.
	 * @return the userAgent
	 */
	public Pattern getUserAgent() {
		return userAgent;
	}

	/**
	 * The setUserAgent setter method.
	 * @param userAgent the userAgent to set
	 */
	public void setUserAgent(Pattern userAgent) {
		this.userAgent = userAgent;
	}

	/**
	 * The getContentType getter method.
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * The setContentType setter method.
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * The getPath getter method.
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * The setPath setter method.
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UIProperties [contentType=" + contentType + ", id=" + id + ", path=" + path + ", userAgent="
			+ userAgent + "]";
	}

}
