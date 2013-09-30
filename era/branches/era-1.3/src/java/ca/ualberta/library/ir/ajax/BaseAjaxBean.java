/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: BaseAjaxBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.ajax;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import ca.ualberta.library.ir.service.ServiceFacade;

/**
 * The BaseAjaxBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class BaseAjaxBean implements AjaxBean {
	protected ServletConfig config;
	protected HttpSession session;
	protected String[] params;
	protected ServiceFacade services;

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.ualberta.library.ir.ajax.AjaxBean#setParams(java.lang.String[])
	 */
	public void setParams(String[] params) {
		this.params = params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.ualberta.library.ir.ajax.AjaxBean#setConfig(javax.servlet.ServletConfig)
	 */
	public void init(ServletConfig config, HttpSession session) throws ServletException {
		this.config = config;
		this.session = session;
		this.services = (ServiceFacade) session.getAttribute("services");
	}
}
