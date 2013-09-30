/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: License.java 5606 2012-10-10 16:45:09Z pcharoen $
 */

package ca.ualberta.library.ir.model.solr;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

/**
 * The License class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5606 $ $Date: 2012-10-10 10:45:09 -0600 (Wed, 10 Oct 2012) $
 */
public class License extends Model {

	@Field("PID")
	private String id;

	@Field("dc.title")
	private List<String> titles;

	private String title;

	@Field("license.mimeType")
	private String mimeType;

	@Field("license.url")
	private String url;

	/**
	 * The License class constructor.
	 */
	public License() {
		super();
	}

	/**
	 * The getId getter method.
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * The setId setter method.
	 * 
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * The getTitle getter method.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return titles.get(0);
	}

	/**
	 * The setTitle setter method.
	 * 
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
		titles = Arrays.asList(title);
	}

	/**
	 * The getMimeType getter method.
	 * 
	 * @return the mimeType
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * The setMimeType setter method.
	 * 
	 * @param mimeType the mimeType to set
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
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
