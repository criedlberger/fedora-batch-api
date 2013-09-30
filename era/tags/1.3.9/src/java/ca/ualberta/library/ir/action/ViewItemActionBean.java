/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ViewItemActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidationErrors;
import net.sourceforge.stripes.validation.ValidationMethod;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.Bookmark;
import ca.ualberta.library.ir.enums.PartOfRelationship;
import ca.ualberta.library.ir.enums.State;
import ca.ualberta.library.ir.enums.SystemPermissions;
import ca.ualberta.library.ir.model.fedora.Datastream;
import ca.ualberta.library.ir.model.fedora.DublinCore;
import ca.ualberta.library.ir.model.fedora.Item;
import ca.ualberta.library.ir.model.fedora.Properties;
import ca.ualberta.library.ir.model.inputform.Form;
import ca.ualberta.library.ir.model.metadata.Field;
import ca.ualberta.library.ir.model.metadata.Field.Key;
import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;
import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The ViewActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/view/item/{pid}/{$event}")
public class ViewItemActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(ViewItemActionBean.class);

	@Validate(required = true)
	private String pid;

	private String title;

	private Community community;

	private Collection collection;

	private String imagePath;

	private List<Community> communities;

	private List<Community> memberOfCommunities;

	private Properties properties;

	private List<Datastream> datastreams;

	private DublinCore dublinCore;

	private List<Collection> memberOfCollections;

	private Datastream license;

	private String ownerName;

	private boolean favorite;

	private Bookmark bookmark;

	private HashMap<Object, Boolean> partOfs;

	private PartOfRelationship[] partOfList;

	private Item item;

	private String htmlTitle;

	private String metaDescription;

	// metadata fields to display (from application.properties)
	private List<String> fields;

	private String typeOfItem;

	private int noOfTransactions;

	/**
	 * The ViewActionBean class constructor.
	 */
	public ViewItemActionBean() {
		super();
	}

	@ValidationMethod
	public void validate(ValidationErrors errors) {
	}

	/**
	 * 
	 * @see ca.ualberta.library.ir.action.BaseActionBean#getObjectPID()
	 */
	@Override
	public String getObjectPID() {
		return pid;
	}

	@HandlesEvent("foxml")
	public Resolution foxml() {
		return foxml(pid);
	}

	@HandlesEvent("solrxml")
	public Resolution solrxml() {
		return solrxml(pid);
	}

	@HandlesEvent("view")
	@DefaultHandler
	@Secure(roles = "/item/read,/object/dark")
	public Resolution view() {
		try {
			try {
				item = getItemByPid(pid);

				// user not logged in
				if (user == null) {
					if (!item.getProperties().getState().equals(State.A.toString())) {
						return forwardUnauthorized();
					}
				}

				// set field list for metadata display format
				String form = item.getProperties().getFormName() == null ? Form.Name.DEFAULT.toString() : item
					.getProperties().getFormName();
				fields = Arrays.asList(commaPattern.split(ApplicationProperties.getString(form + ".metadata.fields")));

				htmlTitle = buildHtmlTitle();
				for (Collection col : item.getCollections()) {
					if (col.isMetaDescription()) {
						metaDescription = buildMetaDescription();
						break;
					}
				}
				noOfTransactions = services.getNoOfTransactions(pid);
			} catch (Exception e) {
				log.error("Exception Error!", e);
				context.getValidationErrors().addGlobalError(new LocalizableError("itemNotFound"));
				return new ForwardResolution(uiPath + "/public/viewMessage.jsp");
			}

			if (State.D.toString().equals(item.getProperties().getState())) {
				if (user == null) {
					context.getValidationErrors().addGlobalError(
						new LocalizableError("deletedItem", trimTitle(item.getProperties().getLabel())));
					return new ForwardResolution(uiPath + "/public/viewMessage.jsp");
				} else if (!isUserInRoles(user, SystemPermissions.ADMIN_DELETED.getPermission())) {
					context.getValidationErrors().addGlobalError(
						new LocalizableError("deletedItem", trimTitle(item.getProperties().getLabel())));
					return new ForwardResolution(uiPath + "/public/viewMessage.jsp");
				}
			}

			return new ForwardResolution(uiPath + "/public/item.jsp");

		} catch (Exception e) {
			log.error("Could not view this item!", e);
			return forwardExceptionError("Could not view this item!", e);
		}
	}

	@HandlesEvent("workflow")
	@Secure(roles = "/item/read,/object/dark,/object/ccid,/admin/approve")
	public Resolution workflow() {
		try {
			return view();
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	private String buildHtmlTitle() {
		try {

			// build html title
			Map<String, List<Field>> fieldMap = item.getMetadata().getFieldMap();
			String author = fieldMap.get(Key.creator.toString()).get(0).getValue();
			String title = fieldMap.get(Key.title.toString()).get(0).getValue();
			String type = fieldMap.get(Key.type.toString()).get(0).getValue();
			String date = fieldMap.get(Key.date.toString()) != null ? fieldMap.get(Key.date.toString()).get(0)
				.getValue() : null;
			return new StringBuilder().append(author).append(", ").append(title).append(", ").append(type)
				.append(date == null ? "" : ", " + date).toString();
		} catch (Exception e) {
			// log.warn("Could not build html title for: " + item.getProperties().getPid() + "!");
			return "";
		}
	}

	private String buildMetaDescription() {
		try {

			// build html title
			Map<String, List<Field>> fieldMap = item.getMetadata().getFieldMap();
			String author = fieldMap.get(Key.creator.toString()).get(0).getValue();
			String date = fieldMap.get(Key.date.toString()) != null ? fieldMap.get(Key.date.toString()).get(0)
				.getValue() : null;
			String description = fieldMap.get(Key.description.toString()) != null ? fieldMap
				.get(Key.description.toString()).get(0).getValue() : null;
			return new StringBuilder().append(author).append(", ").append(", ").append(date == null ? "" : ", " + date)
				.append(description == null ? "" : ", " + description).toString();
		} catch (Exception e) {
			// log.warn("Could not build meta description for: " + item.getProperties().getPid() + "!");
			return "";
		}
	}

	/**
	 * The getCommunity getter method.
	 * 
	 * @return the community
	 */
	public Community getCommunity() {
		return community;
	}

	/**
	 * The setCommunity setter method.
	 * 
	 * @param community the community to set
	 */
	public void setCommunity(Community community) {
		this.community = community;
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
	 * The getCollection getter method.
	 * 
	 * @return the collection
	 */
	public Collection getCollection() {
		return collection;
	}

	/**
	 * The setCollection setter method.
	 * 
	 * @param collection the collection to set
	 */
	public void setCollection(Collection collection) {
		this.collection = collection;
	}

	/**
	 * The getCommunities getter method.
	 * 
	 * @return the communities
	 */
	public List<Community> getCommunities() {
		return communities;
	}

	/**
	 * The setCommunities setter method.
	 * 
	 * @param communities the communities to set
	 */
	public void setCommunities(List<Community> communities) {
		this.communities = communities;
	}

	/**
	 * The getMemberOfCommunities getter method.
	 * 
	 * @return the memberOfCommunities
	 */
	public List<Community> getMemberOfCommunities() {
		return memberOfCommunities;
	}

	/**
	 * The setMemberOfCommunities setter method.
	 * 
	 * @param memberOfCommunities the memberOfCommunities to set
	 */
	public void setMemberOfCommunities(List<Community> memberOfCommunities) {
		this.memberOfCommunities = memberOfCommunities;
	}

	/**
	 * The getProperties getter method.
	 * 
	 * @return the properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * The setProperties setter method.
	 * 
	 * @param properties the properties to set
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	/**
	 * The getDatastreams getter method.
	 * 
	 * @return the datastreams
	 */
	public List<Datastream> getDatastreams() {
		return datastreams;
	}

	/**
	 * The setDatastreams setter method.
	 * 
	 * @param datastreams the datastreams to set
	 */
	public void setDatastreams(List<Datastream> datastreams) {
		this.datastreams = datastreams;
	}

	/**
	 * The getDublinCore getter method.
	 * 
	 * @return the dublinCore
	 */
	public DublinCore getDublinCore() {
		return dublinCore;
	}

	/**
	 * The setDublinCore setter method.
	 * 
	 * @param dublinCore the dublinCore to set
	 */
	public void setDublinCore(DublinCore dublinCore) {
		this.dublinCore = dublinCore;
	}

	/**
	 * The getMemberOfCollections getter method.
	 * 
	 * @return the memberOfCollections
	 */
	public List<Collection> getMemberOfCollections() {
		return memberOfCollections;
	}

	/**
	 * The setMemberOfCollections setter method.
	 * 
	 * @param memberOfCollections the memberOfCollections to set
	 */
	public void setMemberOfCollections(List<Collection> memberOfCollections) {
		this.memberOfCollections = memberOfCollections;
	}

	/**
	 * The getLicense getter method.
	 * 
	 * @return the license
	 */
	public Datastream getLicense() {
		return license;
	}

	/**
	 * The setLicense setter method.
	 * 
	 * @param license the license to set
	 */
	public void setLicense(Datastream license) {
		this.license = license;
	}

	/**
	 * The getTitle getter method.
	 * 
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * The setTitle setter method.
	 * 
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The getOwnerName getter method.
	 * 
	 * @return the ownerName
	 */
	public String getOwnerName() {
		return ownerName;
	}

	/**
	 * The setOwnerName setter method.
	 * 
	 * @param ownerName the ownerName to set
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	/**
	 * The isFavorite getter method.
	 * 
	 * @return the favorite
	 */
	public boolean isFavorite() {
		return favorite;
	}

	/**
	 * The setFavorite setter method.
	 * 
	 * @param favorite the favorite to set
	 */
	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
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
	 * The getPartOfs getter method.
	 * 
	 * @return the partOfs
	 */
	public HashMap<Object, Boolean> getPartOfs() {
		return partOfs;
	}

	/**
	 * The setPartOfs setter method.
	 * 
	 * @param partOfs the partOfs to set
	 */
	public void setPartOfs(HashMap<Object, Boolean> partOfs) {
		this.partOfs = partOfs;
	}

	/**
	 * The getPartOfList getter method.
	 * 
	 * @return the partOfList
	 */
	public PartOfRelationship[] getPartOfList() {
		return partOfList;
	}

	/**
	 * The setPartOfList setter method.
	 * 
	 * @param partOfList the partOfList to set
	 */
	public void setPartOfList(PartOfRelationship[] partOfList) {
		this.partOfList = partOfList;
	}

	/**
	 * The getItem getter method.
	 * 
	 * @return the item
	 */
	public Item getItem() {
		return item;
	}

	/**
	 * The setItem setter method.
	 * 
	 * @param item the item to set
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * The getHtmlTitle getter method.
	 * 
	 * @return the htmlTitle
	 */
	public String getHtmlTitle() {
		return htmlTitle;
	}

	/**
	 * The getMetaDescription getter method.
	 * 
	 * @return the metaDescription
	 */
	public String getMetaDescription() {
		return metaDescription;
	}

	/**
	 * The getFields getter method.
	 * 
	 * @return the fields
	 */
	public List<String> getFields() {
		return fields;
	}

	/**
	 * The getTypeOfItem getter method.
	 * 
	 * @return the typeOfItem
	 */
	public String getTypeOfItem() {
		return typeOfItem;
	}

	/**
	 * The getNoOfTransactions getter method.
	 * 
	 * @return the noOfTransactions
	 */
	public int getNoOfTransactions() {
		return noOfTransactions;
	}
}
