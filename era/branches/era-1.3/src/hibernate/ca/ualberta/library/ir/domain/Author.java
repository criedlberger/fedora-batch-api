package ca.ualberta.library.ir.domain;

// Generated 12-Jan-2009 12:26:53 PM by Hibernate Tools 3.2.4.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Author generated by hbm2java
 */
public class Author implements java.io.Serializable {

	private Integer id;
	private User user;
	private String description;
	private String institution;
	private String contact;
	private byte[] picture;
	private byte[] cv;
	private String filename;
	private boolean published;
	private Date createdDate;
	private Date modifiedDate;
	private Set<AuthorProfile> authorProfiles = new HashSet<AuthorProfile>(0);

	public Author() {
	}

	public Author(User user, String institution, String filename, Date createdDate, Date modifiedDate) {
		this.user = user;
		this.institution = institution;
		this.filename = filename;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

	public Author(User user, String description, String institution, String contact, byte[] picture, byte[] cv,
		String filename, Date createdDate, Date modifiedDate, Set<AuthorProfile> authorProfiles) {
		this.user = user;
		this.description = description;
		this.institution = institution;
		this.contact = contact;
		this.picture = picture;
		this.cv = cv;
		this.filename = filename;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.authorProfiles = authorProfiles;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getInstitution() {
		return this.institution;
	}

	public void setInstitution(String institution) {
		this.institution = institution;
	}

	public String getContact() {
		return this.contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public byte[] getPicture() {
		return this.picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public byte[] getCv() {
		return this.cv;
	}

	public void setCv(byte[] cv) {
		this.cv = cv;
	}

	public String getFilename() {
		return this.filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Date getCreatedDate() {
		return this.createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return this.modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Set<AuthorProfile> getAuthorProfiles() {
		return this.authorProfiles;
	}

	public void setAuthorProfiles(Set<AuthorProfile> authorProfiles) {
		this.authorProfiles = authorProfiles;
	}

	/**
	 * The isPublished getter method.
	 * 
	 * @return the published
	 */
	public boolean isPublished() {
		return published;
	}

	/**
	 * The setPublished setter method.
	 * 
	 * @param published the published to set
	 */
	public void setPublished(boolean published) {
		this.published = published;
	}

}