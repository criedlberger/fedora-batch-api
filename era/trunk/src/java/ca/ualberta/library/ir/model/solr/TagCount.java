/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: TagCount.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.model.solr;


/**
 * The TagCount class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class TagCount extends Model {

	private String tag;
	private int count;

	/**
	 * The TagCount class constructor.
	 */
	public TagCount() {
		super();
	}

	/**
	 * The TagCount class constructor.
	 * 
	 * @param tag
	 * @param count
	 */
	public TagCount(String tag, int count) {
		super();
		this.tag = tag;
		this.count = count;
	}

	/**
	 * The getTag getter method.
	 * 
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * The setTag setter method.
	 * 
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * The getCount getter method.
	 * 
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * The setCount setter method.
	 * 
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

}
