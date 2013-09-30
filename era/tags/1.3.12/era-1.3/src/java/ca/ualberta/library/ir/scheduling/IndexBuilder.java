/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era-web
 * $Id: IndexBuilder.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.scheduling;

/**
 * The IndexBuilder class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public interface IndexBuilder extends Schedule {
	public void setDeleteIndex(boolean deleteIndex);

	public void setOptimizeIndex(boolean optimizeIndex);

	public void setFedoraIndex(boolean fedoraIndex);

	public void setBookmarkIndex(boolean bookmarkIndex);

	public void setFavoriteIndex(boolean favoriteIndex);

	public void setSubscriptionIndex(boolean subscriptionIndex);
}
