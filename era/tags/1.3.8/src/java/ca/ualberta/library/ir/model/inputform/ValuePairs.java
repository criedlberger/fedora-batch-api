/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: ValuePairs.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.inputform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The ValuePairs class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@XmlType
public class ValuePairs extends Model {

	@XmlAttribute(name = "value-pairs-name", required = true)
	private String name;

	@XmlAttribute(name = "dc-term")
	private String dcTerm;

	@XmlElement(name = "pair")
	private List<Pair> pairs;

	/**
	 * The ValuePairs class constructor.
	 */
	public ValuePairs() {
		super();
	}

	/**
	 * The ValuePairs class constructor.
	 * 
	 * @param name
	 * @param dcTerm
	 * @param pairs
	 */
	public ValuePairs(String name, String dcTerm, List<Pair> pairs) {
		super();
		this.name = name;
		this.dcTerm = dcTerm;
		this.pairs = pairs;
	}

	public Map<String, Pair> getPairMap() {
		Map<String, Pair> pairMap = new HashMap<String, Pair>();
		for (Pair pair : pairs) {
			pairMap.put(pair.getStoredValue(), pair);
		}
		return pairMap;
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
	 * The getDcTerm getter method.
	 * 
	 * @return the dcTerm
	 */
	public String getDcTerm() {
		return dcTerm;
	}

	/**
	 * The setDcTerm setter method.
	 * 
	 * @param dcTerm the dcTerm to set
	 */
	public void setDcTerm(String dcTerm) {
		this.dcTerm = dcTerm;
	}

	/**
	 * The getPairs getter method.
	 * 
	 * @return the pairs
	 */
	public List<Pair> getPairs() {
		return pairs;
	}

	/**
	 * The setPairs setter method.
	 * 
	 * @param pairs the pairs to set
	 */
	public void setPairs(List<Pair> pairs) {
		this.pairs = pairs;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ValuePairs [dcTerm=" + dcTerm + ", name=" + name + ", pairs=" + pairs + "]";
	}
}
