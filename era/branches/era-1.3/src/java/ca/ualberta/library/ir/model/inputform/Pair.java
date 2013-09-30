/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: Pair.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.inputform;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The Pair class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@XmlType
public class Pair extends Model {

	@XmlElement(name = "displayed-value", required = true)
	private Label displayedValue;

	@XmlElement(name = "stored-value", required = true)
	private String storedValue;

	@XmlElement(name = "date-range")
	private DateRange dateRange;

	/**
	 * The Pair class constructor.
	 */
	public Pair() {
		super();
	}

	/**
	 * The Pair class constructor.
	 * 
	 * @param displayedValue
	 * @param storedValue
	 */
	public Pair(Label displayedValue, String storedValue) {
		super();
		this.displayedValue = displayedValue;
		this.storedValue = storedValue;
	}

	/**
	 * The getDisplayedValue getter method.
	 * 
	 * @return the displayedValue
	 */
	public Label getDisplayedValue() {
		return displayedValue;
	}

	/**
	 * The setDisplayedValue setter method.
	 * 
	 * @param displayedValue the displayedValue to set
	 */
	public void setDisplayedValue(Label displayedValue) {
		this.displayedValue = displayedValue;
	}

	/**
	 * The getStoredValue getter method.
	 * 
	 * @return the storedValue
	 */
	public String getStoredValue() {
		return storedValue;
	}

	/**
	 * The setStoredValue setter method.
	 * 
	 * @param storedValue the storedValue to set
	 */
	public void setStoredValue(String storedValue) {
		this.storedValue = storedValue;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Pair [dateRange=" + dateRange + ", displayedValue=" + displayedValue + ", storedValue=" + storedValue
			+ "]";
	}

	/**
	 * The getDateRange getter method.
	 * 
	 * @return the dateRange
	 */
	public DateRange getDateRange() {
		return dateRange;
	}

	/**
	 * The setDateRange setter method.
	 * 
	 * @param dateRange the dateRange to set
	 */
	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
}
