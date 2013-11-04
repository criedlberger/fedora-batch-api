package ca.ualberta.library.ir.domain;

// Generated 4-Jul-2008 10:10:02 PM by Hibernate Tools 3.2.0.CR1

/**
 * License generated by hbm2java
 */
public class License implements java.io.Serializable {

	private Integer id;
	private String title;
	private byte[] contents;
	private String url;
	private String mimeType;

	public License() {
	}

	public License(String title, byte[] contents) {
		this.title = title;
		this.contents = contents;
	}

	public License(String title, byte[] contents, String url, String mimeType) {
		this.title = title;
		this.contents = contents;
		this.url = url;
		this.mimeType = mimeType;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public byte[] getContents() {
		return this.contents;
	}

	public void setContents(byte[] contents) {
		this.contents = contents;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMimeType() {
		return this.mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

}