/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: AjaxBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.ajax;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

/**
 * The AjaxBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public interface AjaxBean {

	/**
	 * The setParams method will be called by AjaxHandlerServlet when creating bean.
	 * 
	 * @param params
	 */
	public void setParams(String[] params);

	/**
	 * The init method will be called by AjaxHandlerServlet when creating bean.
	 * 
	 * @param config
	 */
	public void init(ServletConfig config, HttpSession session) throws ServletException;

	// service method template for request path: /ajax/{$bean}/{$event}/get/param_1/param_2/..
	// same sinature as HttpServlet.service method
	// public void get(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
