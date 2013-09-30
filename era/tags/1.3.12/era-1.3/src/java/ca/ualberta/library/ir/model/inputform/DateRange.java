/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: DateRange.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.inputform;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;

/**
 * The DateRange class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class DateRange extends Model {

	@XmlElement(name = "date-start", required = true)
	private Date dateStart;

	@XmlElement(name = "date-end", required = true)
	private Date dateEnd;

	/**
	 * The DateRange class constructor.
	 */
	public DateRange() {
		super();
	}

	/**
	 * The getDateStart getter method.
	 * 
	 * @return the dateStart
	 */
	public Date getDateStart() {
		return dateStart;
	}

	/**
	 * The setDateStart setter method.
	 * 
	 * @param dateStart the dateStart to set
	 */
	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	/**
	 * The getDateEnd getter method.
	 * 
	 * @return the dateEnd
	 */
	public Date getDateEnd() {
		return dateEnd;
	}

	/**
	 * The setDateEnd setter method.
	 * 
	 * @param dateEnd the dateEnd to set
	 */
	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DateRange [dateEnd=" + dateEnd + ", dateStart=" + dateStart + "]";
	}
}