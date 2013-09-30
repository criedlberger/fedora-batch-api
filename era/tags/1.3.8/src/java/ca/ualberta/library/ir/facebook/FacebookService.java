/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: FacebookService.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.facebook;

import com.restfb.types.Page;

import ca.ualberta.library.ir.exception.ServiceException;
import ca.ualberta.library.ir.model.solr.Item;

/**
 * The FacebookService class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public interface FacebookService {

	public String postMessage(Item item, String message, String language) throws ServiceException;

	public Page getPage() throws ServiceException;

	public int removeAllPosts() throws ServiceException;

}
