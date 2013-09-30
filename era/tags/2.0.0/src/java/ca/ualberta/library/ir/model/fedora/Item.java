/**
 * University of Alberta Libraries
 * Information Technology and Services
 * Project: era
 * $Id: Item.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.model.fedora;

import java.util.List;

import ca.ualberta.library.ir.model.metadata.Metadata;
import ca.ualberta.library.ir.model.solr.Collection;
import ca.ualberta.library.ir.model.solr.Community;

/**
 * The Item class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class Item extends Model {

	protected Properties properties;
	protected Datastream thumbnail;
	protected DublinCore dublinCore;
	protected Metadata metadata;
	protected Datastream license;
	protected List<Datastream> datastreams;
	protected List<Community> communities;
	protected List<Collection> collections;
	protected String comments;

	/**
	 * The Item class constructor.
	 */
	public Item() {
		super();
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
	 * The getcollections getter method.
	 * 
	 * @return the collections
	 */
	public List<Collection> getCollections() {
		return collections;
	}

	/**
	 * The setcollections setter method.
	 * 
	 * @param collections the collections to set
	 */
	public void setCollections(List<Collection> collections) {
		this.collections = collections;
	}

	/**
	 * The getcommunities getter method.
	 * 
	 * @return the communities
	 */
	public List<Community> getCommunities() {
		return communities;
	}

	/**
	 * The setcommunities setter method.
	 * 
	 * @param communities the communities to set
	 */
	public void setCommunities(List<Community> communities) {
		this.communities = communities;
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
	 * The getThumbnail getter method.
	 * 
	 * @return the thumbnail
	 */
	public Datastream getThumbnail() {
		return thumbnail;
	}

	/**
	 * The setThumbnail setter method.
	 * 
	 * @param thumbnail the thumbnail to set
	 */
	public void setThumbnail(Datastream thumbnail) {
		this.thumbnail = thumbnail;
	}

	/**
	 * The getComments getter method.
	 * 
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * The setComments setter method.
	 * 
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	/**
	 * The getMetdata getter method.
	 * 
	 * @return the metadata
	 */
	public Metadata getMetadata() {
		return metadata;
	}

	/**
	 * The setMetdata setter method.
	 * 
	 * @param metadata the metadata to set
	 */
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}
}
