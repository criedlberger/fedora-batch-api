package ca.ualberta.library.ir.domain;

// Generated 4-Jul-2008 10:10:02 PM by Hibernate Tools 3.2.0.CR1

/**
 * Tag generated by hbm2java
 */
public class Tag implements java.io.Serializable {

	private Integer id;
	private Bookmark bookmark;
	private String tag;

	public Tag() {
	}

	public Tag(Bookmark bookmark, String tag) {
		this.bookmark = bookmark;
		this.tag = tag;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Bookmark getBookmark() {
		return this.bookmark;
	}

	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}

	public String getTag() {
		return this.tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}