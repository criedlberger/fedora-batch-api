/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir-2.0
 * $Id: CartActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.LocalizableMessage;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.security.action.Secure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.solr.client.solrj.response.QueryResponse;

import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The CartActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/cart/{$event}/{pid}")
public class CartActionBean extends SearchBaseActionBean {

	private static final Log log = LogFactory.getLog(CartActionBean.class);

	protected static final int itemLimit = ApplicationProperties.getInt("cart.item.limit");

	private String pid;
	private List<String> cart;
	private List<String> cartItems;
	private boolean full;

	/**
	 * The CartActionBean class constructor.
	 */
	public CartActionBean() {
		super();
	}

	@HandlesEvent("view")
	@DefaultHandler
	public Resolution view() {
		try {
			cart = context.getCart();
			if (cart.size() > 0) {
				QueryResponse response = services.findObjectsByPids(cart);
				results = response.getResults();
				numFound = results.getNumFound();
				resultRows = results.size();
				qTime = response.getQTime();
				elapsedTime = response.getElapsedTime();
			} else {
				context.getMessages().add(new LocalizableMessage("cart.emptyMessage"));
			}
			return new ForwardResolution(uiPath + "/public/cart.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("getCartDetails")
	public Resolution getCartDetails() {
		try {
			cart = context.getCart();
			if (cart.size() > 0) {
				QueryResponse response = services.findObjectsByPids(cart);
				results = response.getResults();
				numFound = results.getNumFound();
				resultRows = results.size();
				qTime = response.getQTime();
				elapsedTime = response.getElapsedTime();
			} else {
				context.getMessages().add(new LocalizableMessage("cart.emptyMessage"));
			}
			return new ForwardResolution(uiPath + "/public/cartAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("isCartFull")
	public Resolution isCartFull() {
		try {
			cart = context.getCart();
			if (cart.size() < itemLimit) {
				return new StreamingResolution(TEXT_HTML, "false");
			} else {
				return new StreamingResolution(TEXT_HTML, "true");
			}
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("getCartFullMessage")
	public Resolution getCartFullMessage() {
		try {
			cart = context.getCart();
			context.getMessages().add(new LocalizableMessage("cart.fullMessage", cart.size()));
			return new ForwardResolution(uiPath + "/public/cartPopupMessage.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("add")
	@Secure(roles = "/item/read,/object/dark,/object/ccid")
	public Resolution add() {
		try {
			cart = context.getCart();
			if (!cart.contains(pid)) {
				if (cart.size() < itemLimit) {
					cart.add(pid);
				} else {
					full = true;
				}
			}
			return new ForwardResolution(uiPath + "/public/cartAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("remove")
	public Resolution remove() {
		try {
			cart = context.getCart();
			if (cart != null) {
				int i = cart.indexOf(pid);
				if (i > -1) {
					cart.remove(i);
				}
			}
			return new ForwardResolution(uiPath + "/public/cartAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("removeAll")
	public Resolution removeAll() {
		try {
			context.setCart(null);
			context.getMessages().add(new LocalizableMessage("cart.emptyMessage"));
			return new ForwardResolution(uiPath + "/public/cart.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("getItemCount")
	public Resolution getItemCount() {
		try {
			cart = context.getCart();
			return new ForwardResolution(uiPath + "/public/cartAjax.jsp");
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * 
	 * @see ca.ualberta.library.ir.action.BaseActionBean#getObjectPID()
	 */
	@Override
	public String getObjectPID() {
		return pid;
	}

	/**
	 * The getPid getter method.
	 * 
	 * @return the pid
	 */
	@Override
	public String getPid() {
		return pid;
	}

	/**
	 * The setPid setter method.
	 * 
	 * @param pid the pid to set
	 */
	@Override
	public void setPid(String pid) {
		this.pid = pid;
	}

	/**
	 * The getCartItems getter method.
	 * 
	 * @return the cartItems
	 */
	public List<String> getCartItems() {
		return cartItems;
	}

	/**
	 * The getCart getter method.
	 * 
	 * @return the cart
	 */
	public List<String> getCart() {
		return cart;
	}

	/**
	 * The getItemLimit getter method.
	 * 
	 * @return the itemLimit
	 */
	public int getItemLimit() {
		return itemLimit;
	}

	/**
	 * The isFull getter method.
	 * 
	 * @return the full
	 */
	public boolean isFull() {
		return full;
	}

}
