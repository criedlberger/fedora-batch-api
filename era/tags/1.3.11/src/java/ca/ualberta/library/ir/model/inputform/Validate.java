/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: Validate.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.model.inputform;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The Validate class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@XmlType
public class Validate extends Model {

	@XmlElement(name = "required")
	private Boolean required;

	@XmlElement(name = "minlength")
	private Integer minLength;

	@XmlElement(name = "maxlength")
	private Integer maxLength;

	@XmlElement(name = "minvalue")
	private Double minValue;

	@XmlElement(name = "maxvalue")
	private Double maxValue;

	@XmlElement(name = "dateformat")
	private String dateFormat;

	@XmlElement(name = "email")
	private Boolean email;

	@XmlElement(name = "url")
	private Boolean url;

	@XmlElement(name = "isbn")
	private Boolean isbn;

	@XmlElement(name = "regexp")
	private String regexp;

	/**
	 * The Validate class constructor.
	 */
	public Validate() {
		super();
	}

	/**
	 * The getRequired getter method.
	 * 
	 * @return the required
	 */
	public Boolean getRequired() {
		return required;
	}

	/**
	 * The setRequired setter method.
	 * 
	 * @param required the required to set
	 */
	public void setRequired(Boolean required) {
		this.required = required;
	}

	/**
	 * The getMinLength getter method.
	 * 
	 * @return the minLength
	 */
	public Integer getMinLength() {
		return minLength;
	}

	/**
	 * The setMinLength setter method.
	 * 
	 * @param minLength the minLength to set
	 */
	public void setMinLength(Integer minLength) {
		this.minLength = minLength;
	}

	/**
	 * The getMaxLength getter method.
	 * 
	 * @return the maxLength
	 */
	public Integer getMaxLength() {
		return maxLength;
	}

	/**
	 * The setMaxLength setter method.
	 * 
	 * @param maxLength the maxLength to set
	 */
	public void setMaxLength(Integer maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * The getMinValue getter method.
	 * 
	 * @return the minValue
	 */
	public Double getMinValue() {
		return minValue;
	}

	/**
	 * The setMinValue setter method.
	 * 
	 * @param minValue the minValue to set
	 */
	public void setMinValue(Double minValue) {
		this.minValue = minValue;
	}

	/**
	 * The getMaxValue getter method.
	 * 
	 * @return the maxValue
	 */
	public Double getMaxValue() {
		return maxValue;
	}

	/**
	 * The setMaxValue setter method.
	 * 
	 * @param maxValue the maxValue to set
	 */
	public void setMaxValue(Double maxValue) {
		this.maxValue = maxValue;
	}

	/**
	 * The getDateFormat getter method.
	 * 
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * The setDateFormat setter method.
	 * 
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * The getEmail getter method.
	 * 
	 * @return the email
	 */
	public Boolean getEmail() {
		return email;
	}

	/**
	 * The setEmail setter method.
	 * 
	 * @param email the email to set
	 */
	public void setEmail(Boolean email) {
		this.email = email;
	}

	/**
	 * The getUrl getter method.
	 * 
	 * @return the url
	 */
	public Boolean getUrl() {
		return url;
	}

	/**
	 * The setUrl setter method.
	 * 
	 * @param url the url to set
	 */
	public void setUrl(Boolean url) {
		this.url = url;
	}

	/**
	 * The getIsbn getter method.
	 * 
	 * @return the isbn
	 */
	public Boolean getIsbn() {
		return isbn;
	}

	/**
	 * The setIsbn setter method.
	 * 
	 * @param isbn the isbn to set
	 */
	public void setIsbn(Boolean isbn) {
		this.isbn = isbn;
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

	/**
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Validate [dateFormat=" + dateFormat + ", email=" + email + ", isbn=" + isbn + ", maxLength="
			+ maxLength + ", maxValue=" + maxValue + ", minLength=" + minLength + ", minValue=" + minValue
			+ ", regexp=" + regexp + ", required=" + required + ", url=" + url + "]";
	}

}