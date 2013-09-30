/**
 * University of Alberta Libraries
 * Information Technology and Services
 * Project: fedora
 * $Id: BaseDao.java 3587 2009-01-21 17:31:58Z pcharoen $
 */
package ca.ualberta.library.fedora.dao;

/**
 * The BaseDao class.
 *
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 3587 $ $Date: 2009-01-21 10:31:58 -0700 (Wed, 21 Jan 2009) $
 */
public interface BaseDao {
	public void init();

	public void destroy();

	public String getRestServiceUrl();
}
