/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ResearcherActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.Author;

/**
 * The ResearcherActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/researcher/{$event}/{author.id}")
public class ResearcherActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(ResearcherActionBean.class);

	private List<Author> authors;
	private Map<Character, Boolean> nameMap;
	private List<Character> nameList;
	private Author author;

	/**
	 * The ResearcherActionBean class constructor.
	 */
	public ResearcherActionBean() {
		super();
	}

	@DefaultHandler
	@HandlesEvent("list")
	public Resolution list() {
		try {
			authors = services.getAllAuthors();
			nameList = new ArrayList<Character>();
			for (char ch = 'A'; ch <= 'Z'; ++ch) {
				nameList.add(ch);
			}
			nameMap = new HashMap<Character, Boolean>();
			char ch = ' ';
			for (Author au : authors) {
				char ln = au.getUser().getLastName().toUpperCase().charAt(0);
				if (ch != ln) {
					nameMap.put(ln, true);
					ch = ln;
				}
			}
			return new ForwardResolution(uiPath + "/public/researcherList.jsp");
		} catch (Exception e) {
			log.error("Could not find author!", e);
			return forwardExceptionError("Could not find authors!", e);
		}
	}

	@HandlesEvent("getPicture")
	@DontValidate
	public Resolution getPicture() {
		try {
			if (author == null) {
				FileInputStream space = new FileInputStream(context.getServletContext().getRealPath(
					"/images/era-thumbnail-lite.png"));
				return new StreamingResolution(null, space);
			}
			author = services.getAuthor(author.getId());
			if (author.getPicture() != null) {
				return new StreamingResolution(null, new ByteArrayInputStream(author.getPicture()));
			} else {
				FileInputStream space = new FileInputStream(context.getServletContext().getRealPath(
					"/images/era-thumbnail-lite.png"));
				return new StreamingResolution(null, space);
			}
		} catch (Exception e) {
			log.error("Could not find picture!", e);
			return forwardExceptionError("Could not edit picture!", e);
		}
	}

	@HandlesEvent("downloadCv")
	@DontValidate
	public Resolution downloadCv() {
		try {
			author = services.getAuthor(author.getId());
			if (author.getCv() != null) {
				return new StreamingResolution(null, new ByteArrayInputStream(author.getCv())).setFilename(author
					.getFilename());
			} else {
				return new StreamingResolution(null, new ByteArrayInputStream(new byte[] {}));
			}
		} catch (Exception e) {
			log.error("Could not find picture!", e);
			return forwardExceptionError("Could not edit picture!", e);
		}
	}

	/**
	 * The getAuthors getter method.
	 * 
	 * @return the authors
	 */
	public List<Author> getAuthors() {
		return authors;
	}

	/**
	 * The setAuthors setter method.
	 * 
	 * @param authors the authors to set
	 */
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	/**
	 * The getNameMap getter method.
	 * 
	 * @return the nameMap
	 */
	public Map<Character, Boolean> getNameMap() {
		return nameMap;
	}

	/**
	 * The setNameMap setter method.
	 * 
	 * @param nameMap the nameMap to set
	 */
	public void setNameMap(Map<Character, Boolean> nameMap) {
		this.nameMap = nameMap;
	}

	/**
	 * The getNameList getter method.
	 * 
	 * @return the nameList
	 */
	public List<Character> getNameList() {
		return nameList;
	}

	/**
	 * The setNameList setter method.
	 * 
	 * @param nameList the nameList to set
	 */
	public void setNameList(List<Character> nameList) {
		this.nameList = nameList;
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
}
