/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: Metadata.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Form class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@XmlRootElement
public class Metadata extends Model {

	@XmlElement(name = "field")
	private List<Field> fields;

	/**
	 * The Form class constructor.
	 */
	public Metadata() {
		super();
	}

	/**
	 * The Form class constructor.
	 * 
	 * @param fields
	 */
	public Metadata(List<Field> fields) {
		super();
		this.fields = fields;
	}

	public Map<String, List<Field>> getFieldMap() {
		Map<String, List<Field>> map = new HashMap<String, List<Field>>();
		if (fields == null || fields.isEmpty()) {
			return map;
		}
		Collections.sort(fields);
		List<Field> flds = new ArrayList<Field>();
		String pkey = null, key = null;
		for (Field fld : fields) {
			key = fld.getKey();
			if (pkey == null) {
				pkey = key;
			}
			if (key.equals(pkey)) {
				flds.add(fld);
			} else {
				map.put(pkey, flds);
				flds = new ArrayList<Field>();
				flds.add(fld);
				pkey = key;
			}
		}
		map.put(key, flds);
		return map;
	}

	/**
	 * The getFields getter method.
	 * 
	 * @return the fields
	 */
	public List<Field> getFields() {
		return fields;
	}

	/**
	 * The setFields setter method.
	 * 
	 * @param fields the fields to set
	 */
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Form [fields=" + fields + "]";
	}
}
