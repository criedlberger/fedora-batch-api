/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: BookmarkActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.FacetField;

import fedora.server.types.gen.ObjectFields;

import ca.ualberta.library.ir.domain.Bookmark;
import ca.ualberta.library.ir.domain.Tag;

/**
 * The TagActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/bookmark/{$event}/{bookmark.id}/{bookmark.pid}/{next}/{mode}")
public class BookmarkActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(BookmarkActionBean.class);

	@ValidateNestedProperties({ @Validate(field = "pid", required = true),
		@Validate(field = "title", required = true, minlength = 3, maxlength = 255) })
	private Bookmark bookmark;

	@Validate(required = true, minlength = 3, maxlength = 255)
	private String tags;

	private FacetField tagCloud;
	private String pid;
	private String next;
	private String mode;

	/**
	 * The TagActionBean class constructor.
	 */
	public BookmarkActionBean() {
		super();
	}

	@DefaultHandler
	@HandlesEvent("edit")
	@DontValidate
	public Resolution edit() {
		try {
			// log.debug("bookmark.pid: " + bookmark.getPid());
			Bookmark bm = services.getUserBookmarkByPid(bookmark.getPid(), user.getId());
			if (bm == null) {
				ObjectFields obj = services.findObjectByPid(resultFields, bookmark.getPid());
				bm = new Bookmark();
				bm.setId(0);
				bm.setTitle(obj.getTitle(0));
				bm.setPid(bookmark.getPid());
			}
			bookmark = bm;
			StringBuffer sb = new StringBuffer();
			Set<Tag> set = bookmark.getTags();
			for (Tag tag : set) {
				sb.append(sb.length() > 0 ? ", " : "").append(tag.getTag());
			}
			tags = sb.toString();
			// log.debug("bookmark.id: " + bookmark.getId());
			return new ForwardResolution(uiPath + "/protected/editBookmark.jsp");
		} catch (Exception e) {
			log.error("Could not edit bookmark!", e);
			return forwardExceptionError("Could not edit bookmark!", e);
		}
	}

	@HandlesEvent("add")
	@DontValidate
	public Resolution add() {
		try {
			// log.debug("bookmark.pid: " + bookmark.getPid());
			Bookmark bm = services.getUserBookmarkByPid(bookmark.getPid(), user.getId());
			if (bm == null) {
				ObjectFields obj = services.findObjectByPid(resultFields, bookmark.getPid());
				bm = new Bookmark();
				bm.setId(null);
				bm.setTitle(obj.getTitle(0));
				bm.setPid(bookmark.getPid());
				bm.setUser(user);
				bm.setCreatedDate(new Date());
				services.saveOrUpdateBookmark(bm);
				services.addBookmarkIndex(bm);
			}
			bookmark = bm;
			pid = bm.getPid();
			return new ForwardResolution(uiPath + "/public/bookmarkAjax.jsp");
		} catch (Exception e) {
			log.error("Could not edit bookmark!", e);
			return forwardExceptionError("Could not edit bookmark!", e);
		}
	}

	@HandlesEvent("save")
	public Resolution save() {
		try {
			if (bookmark.getId().intValue() > 0) {
				Bookmark bm = services.getBookmark(bookmark.getId());
				services.deleteTags(bm.getTags());
			} else {
				bookmark.setId(null);
				bookmark.setCreatedDate(new Date());
				bookmark.setUser(user);
				services.saveOrUpdateBookmark(bookmark);
			}
			Set<Tag> set = new LinkedHashSet<Tag>();
			String[] s = commaPattern.split(StringUtils.trimToEmpty(tags));
			for (String tag : s) {
				if (StringUtils.trimToNull(tag) != null) {
					set.add(new Tag(bookmark, tag.trim()));
				}
			}
			bookmark.setTags(set);
			bookmark.setUser(user);
			services.saveOrUpdateBookmark(bookmark);
			services.addBookmarkIndex(bookmark);
			services.commit(false);
			context.getMessages().add(new LocalizableMessage("bookmark.saveSuccess", bookmark.getTitle()));
			if (next != null) {
				return new RedirectResolution(next);
			} else {
				return new RedirectResolution("/action/myaccount/bookmarks");
			}
		} catch (Exception e) {
			log.error("Could not save bookmark!", e);
			return forwardExceptionError("Could not save bookmark!", e);
		}
	}

	@HandlesEvent("remove")
	@DontValidate
	public Resolution remove() {
		try {
			// remove bookmark for popup
			Bookmark bm = services.getBookmark(bookmark.getId());
			pid = bm.getPid();
			services.deleteBookmark(bookmark.getId());
			services.deleteBookmarkIndex(bm);
			bookmark = null;
			return new ForwardResolution(uiPath + "/public/bookmarkAjax.jsp");
		} catch (Exception e) {
			log.error("Could not delete bookmark!", e);
			return forwardExceptionError("Could not delete bookmark!", e);
		}
	}

	@HandlesEvent("delete")
	@DontValidate
	public Resolution delete() {
		try {
			Bookmark bm = services.getBookmark(bookmark.getId());
			services.deleteBookmark(bookmark.getId());
			services.deleteBookmarkIndex(bm);
			bookmark = null;

			if (StringUtils.trimToNull(next) != null) {
				return new RedirectResolution(next);
			} else {
				return new RedirectResolution("/public/view/item/" + bookmark.getPid());
			}
		} catch (Exception e) {
			log.error("Could not delete bookmark!", e);
			return forwardExceptionError("Could not delete bookmark!", e);
		}
	}

	@HandlesEvent("getBookmarkStatus")
	@DontValidate
	public Resolution getBookmarkStatus() {
		try {
			// log.debug("pid: " + pid);
			if (user != null) {
				bookmark = services.getUserBookmarkByPid(pid, user.getId());
			}
			return new ForwardResolution(uiPath + "/public/bookmarkAjax.jsp");
		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/bookmarkAjax.jsp");
		}
	}

	@HandlesEvent("getBookmarkToolbar")
	@DontValidate
	public Resolution getBookmarkToolbar() {
		return getBookmarkStatus();
	}

	/**
	 * The getBookmark getter method.
	 * 
	 * @return the bookmark
	 */
	public Bookmark getBookmark() {
		return bookmark;
	}

	/**
	 * The setBookmark setter method.
	 * 
	 * @param bookmark the bookmark to set
	 */
	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}

	/**
	 * The getTags getter method.
	 * 
	 * @return the tags
	 */
	public String getTags() {
		return tags;
	}

	/**
	 * The setTags setter method.
	 * 
	 * @param tags the tags to set
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}

	/**
	 * The getTagCloud getter method.
	 * 
	 * @return the tagCloud
	 */
	public FacetField getTagCloud() {
		return tagCloud;
	}

	/**
	 * The setTagCloud setter method.
	 * 
	 * @param tagCloud the tagCloud to set
	 */
	public void setTagCloud(FacetField tagCloud) {
		this.tagCloud = tagCloud;
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
	 * The getNext getter method.
	 * 
	 * @return the next
	 */
	public String getNext() {
		return next;
	}

	/**
	 * The setNext setter method.
	 * 
	 * @param next the next to set
	 */
	public void setNext(String next) {
		this.next = next;
	}

	/**
	 * The getMode getter method.
	 * 
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * The setMode setter method.
	 * 
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
}
