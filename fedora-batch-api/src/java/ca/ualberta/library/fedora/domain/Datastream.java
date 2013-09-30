/**
 * University of Alberta Libraries
 * Information Technology and Services
 * Project: fedora
 * $Id: Datastream.java 3587 2009-01-21 17:31:58Z pcharoen $
 */
package ca.ualberta.library.fedora.domain;

import java.io.InputStream;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * The Datastream class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 3587 $ $Date: 2009-01-21 10:31:58 -0700 (Wed, 21 Jan 2009) $
 */
public class Datastream extends BaseDomain {
	public static final String DC_ID = "DC";
	public static final String RELS_EXT_ID = "RELS-EXT";
	public static final String LICENSE_ID = "LICENSE";
	public static final String THUMBNAIL_ID = "THUMBNAIL";

	private String pid;
	private String dsId;
	private String label;
	private String controlGroup = "M";
	private String mimeType;
	private InputStream data;
	private String internalXml;
	private String externalLocation;
	private String location;
	private String createdDate;

	private String contentType = "file";
	private String file;
	private String url;

	/**
	 * The getControlGroup getter method.
	 * @return the controlGroup
	 */
	public String getControlGroup() {
		return controlGroup;
	}

	/**
	 * The setControlGroup setter method.
	 * @param controlGroup the controlGroup to set
	 */
	public void setControlGroup(String controlGroup) {
		this.controlGroup = controlGroup;
	}

	/**
	 * The Datastream class constructor.
	 */
	public Datastream() {
		super();
	}

	/**
	 * The getPid getter method.
	 * 
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * The setPid setter method.
	 * 
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * The getLabel getter method.
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * The setLabel setter method.
	 * 
	 * @param label the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
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
	 * The getDsId getter method.
	 * 
	 * @return the dsId
	 */
	public String getDsId() {
		return dsId;
	}

	/**
	 * The setDsId setter method.
	 * 
	 * @param dsId the dsId to set
	 */
	public void setDsId(String dsId) {
		this.dsId = dsId;
	}

	/**
	 * The getLocation getter method.
	 * 
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * The setLocation setter method.
	 * 
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * The getData getter method.
	 * @return the data
	 */
	public InputStream getData() {
		return data;
	}

	/**
	 * The setData setter method.
	 * @param data the data to set
	 */
	public void setData(InputStream data) {
		this.data = data;
	}

	/**
	 * The getExternalLocation getter method.
	 * @return the externalLocation
	 */
	public String getExternalLocation() {
		return externalLocation;
	}

	/**
	 * The setExternalLocation setter method.
	 * @param externalLocation the externalLocation to set
	 */
	public void setExternalLocation(String externalLocation) {
		this.externalLocation = externalLocation;
	}

	/**
	 * The getInternalXml getter method.
	 * @return the internalXml
	 */
	public String getInternalXml() {
		return internalXml;
	}

	/**
	 * The setInternalXml setter method.
	 * @param internalXml the internalXml to set
	 */
	public void setInternalXml(String internalXml) {
		this.internalXml = internalXml;
	}

	/**
	 * The getUrl getter method.
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * The setUrl setter method.
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * The getContentType getter method.
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * The setContentType setter method.
	 * @param contentType the contentType to set
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * The getFile getter method.
	 * @return the file
	 */
	public String getFile() {
		return file;
	}

	/**
	 * The setFile setter method.
	 * @param file the file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("pid", pid).append("dsId", dsId).append("label", label).append(
			"controlGroup", controlGroup).append("mimeType", mimeType).append("data", data).append("internalXml",
			internalXml).append("externalLocation", externalLocation).append("location", location).append(
			"contentType", contentType).append("file", file).append("url", url).toString();
	}

	/**
	 * The getCreatedDate getter method.
	 * @return the createdDate
	 */
	public String getCreatedDate() {
		return createdDate;
	}

	/**
	 * The setCreatedDate setter method.
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

}
