/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: FavoriteActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.RedirectResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.domain.Favorite;
import fedora.server.types.gen.ObjectFields;

/**
 * The FavoriteActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/action/favorite/{$event}/{pid}/{next}")
public class FavoriteActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(FavoriteActionBean.class);

	private String pid;

	private int index;

	private String next;

	private boolean favorite;

	/**
	 * The FavoriteActionBean class constructor.
	 */
	public FavoriteActionBean() {
		super();
	}

	@DefaultHandler
	@HandlesEvent("add")
	public Resolution add() {
		try {
			Favorite fav = services.getFavoriteByPid(pid, user.getId());
			if (fav == null) {
				fav = new Favorite();
				fav.setPid(pid);
				fav.setUser(user);
				ObjectFields obj = services.findObjectByPid(BaseActionBean.resultFields, pid);
				fav.setTitle(obj.getTitle(0));
			}
			services.addFavoriteIndex(fav);
			services.saveOrUpdateFavorite(fav);
		} catch (Exception e) {
			log.error("Could not add favorite!", e);
		}
		// log.debug("next: " + next);
		if (StringUtils.trimToNull(next) != null) {
			return new RedirectResolution(next);
		} else {
			return new ForwardResolution(uiPath + "/public/favoriteAjax.jsp");
		}
	}

	@HandlesEvent("remove")
	public Resolution remove() {
		try {
			Favorite fav = services.getFavoriteByPid(pid, user.getId());
			services.deleteFavoriteIndex(fav);
			services.deleteFavorite(fav.getId());
			return new ForwardResolution(uiPath + "/public/favoriteAjax.jsp");
		} catch (Exception e) {
			log.error("Could not remove favorite!", e);
		}
		if (StringUtils.trimToNull(next) != null) {
			return new RedirectResolution(next);
		} else {
			return new ForwardResolution(uiPath + "/public/favoriteAjax.jsp");
		}
	}

	@HandlesEvent("getFavoriteStatus")
	@DontValidate
	public Resolution getFavoriteStatus() {
		try {
			// log.debug("pid: " + pid + " index: " + index);
			if (user != null) {
				favorite = services.isFavorite(pid, user.getId());
				// log.debug("favorite: " + favorite);
			}
			return new ForwardResolution(uiPath + "/public/favoriteAjax.jsp");
		} catch (Exception e) {
			log.error("Could not search!", e);
			return new ForwardResolution(uiPath + "/public/favoriteAjax.jsp");
		}
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
	 * The getIndex getter method.
	 * 
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * The setIndex setter method.
	 * 
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
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
}
