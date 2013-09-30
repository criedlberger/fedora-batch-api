/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ContentModelComparator.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.utils;

import java.util.Comparator;

import ca.ualberta.library.ir.model.fedora.ContentModel;

/**
 * The ContentModelComparator class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class ContentModelComparator implements Comparator<ContentModel> {

	/**
	 * The ContentModelComparator class constructor.
	 */
	public ContentModelComparator() {
		super();
	}

	/**
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(ContentModel cm1, ContentModel cm2) {
		return cm1.getContentModel().compareToIgnoreCase(cm2.getContentModel());
	}

}
