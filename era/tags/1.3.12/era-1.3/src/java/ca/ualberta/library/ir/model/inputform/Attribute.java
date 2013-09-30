/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: Attribute.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.inputform;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * The Attribute class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@XmlType
public class Attribute extends Model {

	@XmlAttribute(name = "name", required = true)
	private String name;

	@XmlAttribute(name = "value", required = true)
	private String value;

	/**
	 * The Attribute class constructor.
	 */
	public Attribute() {
		super();
	}

	/**
	 * The Attribute class constructor.
	 * 
	 * @param name
	 */
	public Attribute(String name) {
		super();
		this.name = name;
	}

	/**
	 * The Attribute class constructor.
	 * 
	 * @param name
	 * @param value
	 */
	public Attribute(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	/**
	 * The getName getter method.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The setName setter method.
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The getValue getter method.
	 * 
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * The setValue setter method.
	 * 
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Attribute [name=" + name + ", value=" + value + "]";
	}
}