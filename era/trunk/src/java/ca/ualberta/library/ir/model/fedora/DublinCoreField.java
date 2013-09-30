/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: DublinCoreField.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.model.fedora;

import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The DublinCoreField class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class DublinCoreField extends Model {
	private String name;
	private List<String> values;

	/**
	 * The DublinCoreField class constructor.
	 */
	public DublinCoreField() {
		super();
	}

	public DublinCoreField(String name, List<String> values) {
		super();
		this.name = name;
		this.values = values;
	}

	/**
	 * The getName getter method.
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * The setName setter method.
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * The getValues getter method.
	 * @return the values
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * The setValues setter method.
	 * @param values the values to set
	 */
	public void setValues(List<String> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("name", name).append("values", values).toString();
	}

}
