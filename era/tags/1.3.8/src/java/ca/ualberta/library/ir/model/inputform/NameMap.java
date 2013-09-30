/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: NameMap.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.inputform;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * The NameMap class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@XmlType
public class NameMap extends Model {

	@XmlAttribute(name = "id", required = true)
	private String id;

	@XmlAttribute(name = "form-name", required = true)
	private String formName;

	@XmlAttribute(name = "item-type", required = false)
	private String itemType;

	@XmlAttribute(name = "regexp", required = false)
	private String regexp;

	/**
	 * The NameMap class constructor.
	 */
	public NameMap() {
		super();
	}

	/**
	 * The NameMap class constructor.
	 * 
	 * @param id
	 * @param formName
	 */
	public NameMap(String id, String formName) {
		super();
		this.id = id;
		this.formName = formName;
	}

	/**
	 * The getItemType getter method.
	 * 
	 * @return the itemType
	 */
	public String getItemType() {
		return itemType;
	}

	/**
	 * The setItemType setter method.
	 * 
	 * @param itemType the itemType to set
	 */
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	/**
	 * The getFormName getter method.
	 * 
	 * @return the formName
	 */
	public String getFormName() {
		return formName;
	}

	/**
	 * The setFormName setter method.
	 * 
	 * @param formName the formName to set
	 */
	public void setFormName(String formName) {
		this.formName = formName;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NameMap [formName=" + formName + ", itemType=" + itemType + "]";
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
	 * The getRegexp getter method.
	 * 
	 * @return the regexp
	 */
	public String getRegexp() {
		return regexp;
	}

	/**
	 * The setRegexp setter method.
	 * 
	 * @param regexp the regexp to set
	 */
	public void setRegexp(String regexp) {
		this.regexp = regexp;
	}
}
