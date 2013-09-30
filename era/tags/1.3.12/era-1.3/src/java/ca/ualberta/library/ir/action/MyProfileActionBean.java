/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: MyProfileActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.io.FileInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.sourceforge.stripes.action.Before;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.controller.LifecycleStage;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.Author;
import ca.ualberta.library.ir.domain.AuthorProfile;
import ca.ualberta.library.ir.enums.AuthorProfileType;
import ca.ualberta.library.ir.enums.HandleType;

/**
 * The AuthorActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/myaccount/profile/{$event}/{author.id}")
public class MyProfileActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(MyProfileActionBean.class);

	@ValidateNestedProperties({ @Validate(field = "institution", required = true) })
	private Author author;
	private String filename;
	private String imagePath;
	private List<String> profiles;
	private HashMap<Object, String> profileMap;
	private AuthorProfileType[] profileList;
	private List<AuthorProfile> authorProfiles;
	private FileBean cv;
	private String cvPath;
	private boolean removePicture;
	private boolean removeCv;

	/**
	 * The AuthorActionBean class constructor.
	 */
	public MyProfileActionBean() {
		super();
	}

	@Before(on = { "save" }, stages = LifecycleStage.HandlerResolution)
	public void restoreFields() {
		init();
	}

	@DefaultHandler
	@HandlesEvent("view")
	@DontValidate
	public Resolution view() {
		author = user.getAuthor();
		if (author == null) {
			author = new Author();
		}
		return new ForwardResolution(uiPath + "/protected/myProfile.jsp");
	}

	@HandlesEvent("edit")
	@DontValidate
	@Secure(roles = "/user/profile/update")
	public Resolution edit() {
		try {
			profileMap = new HashMap<Object, String>();
			profileList = AuthorProfileType.values();
			author = user.getAuthor();
			if (author == null) {
				author = new Author();
			} else {
				imagePath = request.getContextPath() + "/public/researcher/getPicture/" + author.getId();
				cvPath = request.getContextPath() + "/public/researcher/downloadCv/" + author.getId();
				Set<AuthorProfile> profs = author.getAuthorProfiles();
				for (AuthorProfile prof : profs) {
					profileMap.put(prof.getType(), prof.getDescription());
				}
			}
			return new ForwardResolution(uiPath + "/protected/editMyProfile.jsp");
		} catch (Exception e) {
			log.error("Could not find profile!", e);
			return forwardExceptionError("Could not edit profile!", e);
		}
	}

	/**
	 * The init method.
	 */
	private void init() {
		profileMap = new HashMap<Object, String>();
		profileList = AuthorProfileType.values();
		author = user.getAuthor();
		if (author == null) {
			author = new Author();
		} else {
			imagePath = request.getContextPath() + "/public/researcher/getPicture/" + author.getId();
			cvPath = request.getContextPath() + "/public/researcher/downloadCv/" + author.getId();
			Set<AuthorProfile> profs = author.getAuthorProfiles();
			for (AuthorProfile prof : profs) {
				profileMap.put(prof.getType(), prof.getDescription());
			}
		}
	}

	@HandlesEvent("save")
	@Secure(roles = "/user/profile/update")
	public Resolution save() {
		try {
			if (author == null) {
				context.getValidationErrors().add("profile.institution", new LocalizableError("institutionRequired"));
				return edit();
			}
			Author au = null;
			if (author.getId() != null) {
				au = services.getAuthor(author.getId());
				if (removeCv) {
					author.setCv(null);
					author.setFilename(null);
				} else {
					author.setCv(au.getCv());
				}
				if (removePicture) {
					author.setPicture(null);
				} else {
					author.setPicture(au.getPicture());
				}
				author.setCreatedDate(au.getCreatedDate());
			} else {
				author.setCreatedDate(new Date());
			}
			if (StringUtils.trimToNull(filename) != null) {
				byte[] picture = IOUtils.toByteArray(new FileInputStream(tempPath + "/" + filename));
				author.setPicture(picture);
			}
			if (cv != null) {
				author.setCv(IOUtils.toByteArray(cv.getInputStream()));
				author.setFilename(cv.getFileName());
			}
			author.setUser(user);
			author.setModifiedDate(new Date());
			services.saveOrUpdateAuthor(author);
			for (AuthorProfile prof : authorProfiles) {
				prof.setAuthor(author);
				services.saveOrUpdateAuthorProfile(prof);
			}
			saveHandle(user.getUsername(), HandleType.AUTHOR);

			// update context variable
			user = services.getUser(user.getId());
			context.setUser(user);
			context.getMessages().add(new LocalizableMessage("profile.modifySuccess"));
			return view();
		} catch (Exception e) {
			log.error("Could not save profile!", e);
			return forwardExceptionError("Could not save profile!", e);
		}
	}

	/**
	 * The getAuthor getter method.
	 * 
	 * @return the author
	 */
	public Author getAuthor() {
		return author;
	}

	/**
	 * The setAuthor setter method.
	 * 
	 * @param author the author to set
	 */
	public void setAuthor(Author author) {
		this.author = author;
	}

	/**
	 * The getFilename getter method.
	 * 
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * The setFilename setter method.
	 * 
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * The getImagePath getter method.
	 * 
	 * @return the imagePath
	 */
	public String getImagePath() {
		return imagePath;
	}

	/**
	 * The setImagePath setter method.
	 * 
	 * @param imagePath the imagePath to set
	 */
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * The getProfiles getter method.
	 * 
	 * @return the profiles
	 */
	public List<String> getProfiles() {
		return profiles;
	}

	/**
	 * The setProfiles setter method.
	 * 
	 * @param profiles the profiles to set
	 */
	public void setProfiles(List<String> profiles) {
		this.profiles = profiles;
	}

	/**
	 * The getProfileMap getter method.
	 * 
	 * @return the profileMap
	 */
	public HashMap<Object, String> getProfileMap() {
		return profileMap;
	}

	/**
	 * The setProfileMap setter method.
	 * 
	 * @param profileMap the profileMap to set
	 */
	public void setProfileMap(HashMap<Object, String> profileMap) {
		this.profileMap = profileMap;
	}

	/**
	 * The getProfileList getter method.
	 * 
	 * @return the profileList
	 */
	public AuthorProfileType[] getProfileList() {
		return profileList;
	}

	/**
	 * The setProfileList setter method.
	 * 
	 * @param profileList the profileList to set
	 */
	public void setProfileList(AuthorProfileType[] profileList) {
		this.profileList = profileList;
	}

	/**
	 * The getAuthorProfiles getter method.
	 * 
	 * @return the authorProfiles
	 */
	public List<AuthorProfile> getAuthorProfiles() {
		return authorProfiles;
	}

	/**
	 * The setAuthorProfiles setter method.
	 * 
	 * @param authorProfiles the authorProfiles to set
	 */
	public void setAuthorProfiles(List<AuthorProfile> authorProfiles) {
		this.authorProfiles = authorProfiles;
	}

	/**
	 * The getCv getter method.
	 * 
	 * @return the cv
	 */
	public FileBean getCv() {
		return cv;
	}

	/**
	 * The setCv setter method.
	 * 
	 * @param cv the cv to set
	 */
	public void setCv(FileBean cv) {
		this.cv = cv;
	}

	/**
	 * The getCvPath getter method.
	 * 
	 * @return the cvPath
	 */
	public String getCvPath() {
		return cvPath;
	}

	/**
	 * The setCvPath setter method.
	 * 
	 * @param cvPath the cvPath to set
	 */
	public void setCvPath(String cvPath) {
		this.cvPath = cvPath;
	}

	/**
	 * The setRemovePicture setter method.
	 * 
	 * @param removePicture the removePicture to set
	 */
	public void setRemovePicture(boolean removePicture) {
		this.removePicture = removePicture;
	}

	/**
	 * The setRemoveCv setter method.
	 * 
	 * @param removeCv the removeCv to set
	 */
	public void setRemoveCv(boolean removeCv) {
		this.removeCv = removeCv;
	}

}