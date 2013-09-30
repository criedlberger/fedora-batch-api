/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: InputForms.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.inputform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The InputForms class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@XmlRootElement(name = "input-forms")
public class InputForms extends Model {

	@XmlElementWrapper(name = "form-map")
	@XmlElement(name = "name-map")
	private List<NameMap> nameMaps;

	@XmlElementWrapper(name = "form-definitions")
	@XmlElement(name = "form")
	private List<Form> forms;

	@XmlElementWrapper(name = "form-value-pairs")
	@XmlElement(name = "value-pairs")
	private List<ValuePairs> valuePairs;

	/**
	 * The InputForms class constructor.
	 */
	public InputForms() {
		super();
	}

	/**
	 * The InputForms class constructor.
	 * 
	 * @param nameMaps
	 * @param forms
	 * @param valuePairs
	 */
	public InputForms(List<NameMap> nameMaps, List<Form> forms, List<ValuePairs> valuePairs) {
		super();
		this.nameMaps = nameMaps;
		this.forms = forms;
		this.valuePairs = valuePairs;
	}

	/**
	 * The getFormById method is a utility method to get Form object using name mapping id.
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Form getFormById(String id) throws Exception {
		try {
			List<NameMap> nameMaps = this.getNameMaps();
			for (NameMap nameMap : nameMaps) {
				if (nameMap.getId().equals(id)) {
					for (Form form : forms) {
						if (form.getName().equals(nameMap.getFormName())) {
							return form;
						}
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("Could not get Form (id: " + id + ")!", e);
		}
		return getFormById(Form.Name.DEFAULT.toString());
	}

	/**
	 * The getFormByItemType method is a utility method to get Form object using itemType.
	 * 
	 * @param itemType
	 * @return
	 * @throws Exception
	 */
	public Form getFormByItemType(String itemType) throws Exception {
		try {
			List<NameMap> nameMaps = this.getNameMaps();
			for (NameMap nameMap : nameMaps) {
				if (nameMap.getItemType().equals(itemType)) {
					for (Form form : forms) {
						if (form.getName().equals(nameMap.getFormName())) {
							return form;
						}
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("Could not get Form (itemType: " + itemType + ")!", e);
		}
		return getFormById(Form.Name.DEFAULT.toString());
	}

	/**
	 * The getFormByKey method is a utility method to get Form object using regular expression to match a key.
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public Form getFormByKey(String key) throws Exception {
		try {
			List<NameMap> nameMaps = this.getNameMaps();
			for (NameMap nameMap : nameMaps) {
				if (key.matches(nameMap.getRegexp())) {
					for (Form form : forms) {
						if (form.getName().equals(nameMap.getFormName())) {
							return form;
						}
					}
				}
			}
		} catch (Exception e) {
			throw new Exception("Could not get Form (key: " + key + ")!", e);
		}
		return getFormById(Form.Name.DEFAULT.toString());
	}

	/**
	 * The getValuePairs method is utility method to get a ValuePairs object using value-pairs-name.
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public ValuePairs getValuePairs(String name) throws Exception {
		try {
			for (ValuePairs pairs : valuePairs) {
				if (pairs.getName().equals(name)) {
					return pairs;
				}
			}
		} catch (Exception e) {
			throw new Exception("Could not get ValuePairs (" + name + ")!", e);
		}
		throw new Exception("Could not find ValuePairs name: " + name + "!");
	}

	public Map<String, ValuePairs> getValuePairsMap() throws Exception {
		Map<String, ValuePairs> valuePairsMap = new HashMap<String, ValuePairs>();
		try {
			for (ValuePairs pairs : valuePairs) {
				valuePairsMap.put(pairs.getName(), pairs);
			}
		} catch (Exception e) {
			throw new Exception("Could not get ValuePairMap!", e);
		}
		return valuePairsMap;
	}

	/**
	 * The getformMaps getter method.
	 * 
	 * @return the fromMap
	 */
	public List<NameMap> getNameMaps() {
		return nameMaps;
	}

	/**
	 * The setformMaps setter method.
	 * 
	 * @param nameMaps the fromMap to set
	 */
	public void setNameMaps(List<NameMap> nameMaps) {
		this.nameMaps = nameMaps;
	}

	/**
	 * The getforms getter method.
	 * 
	 * @return the forms
	 */
	public List<Form> getForms() {
		return forms;
	}

	/**
	 * The setforms setter method.
	 * 
	 * @param forms the forms to set
	 */
	public void setForms(List<Form> forms) {
		this.forms = forms;
	}

	/**
	 * The getValuePairs getter method.
	 * 
	 * @return the valuePairs
	 */
	public List<ValuePairs> getValuePairs() {
		return valuePairs;
	}

	/**
	 * The setValuePairs setter method.
	 * 
	 * @param valuePairs the valuePairs to set
	 */
	public void setValuePairs(List<ValuePairs> valuePairs) {
		this.valuePairs = valuePairs;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InputForms [forms=" + forms + ", nameMaps=" + nameMaps + ", valuePairs=" + valuePairs + "]";
	}
}
