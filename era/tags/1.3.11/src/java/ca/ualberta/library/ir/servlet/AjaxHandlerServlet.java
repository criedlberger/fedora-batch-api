/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: AjaxHandlerServlet.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The AjaxHandlerServlet class is used for handling client Ajax request. This class will not pass StripesFilter. It can
 * be use for Ajax calling on Wizard ActionBean.
 * 
 * url pattern: /{ajax}/{$ajaxBean}/{$event}/{params}/... Ex: /ajax/deposit/getCollectionList Imprementation:
 * DepositAjaxBean extends AjaxBean, DepositAjaxBean.getCollectionList(request, response)
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class AjaxHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = -8830455577079109429L;
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(AjaxHandlerServlet.class);

	private static final Pattern urlParserPattern;
	protected ServletConfig config;
	protected String action;
	protected String event;
	protected String[] params;

	static {
		// compile parameter parser pattern
		urlParserPattern = Pattern.compile("/");
	}

	/**
	 * The AjaxHandlerServlet class constructor.
	 */
	public AjaxHandlerServlet() {
		super();
	}

	/*
	 * The service method.
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
		IOException {
		String ajaxBeanPackage = config.getInitParameter("AjaxBean.Package");
		String pathInfo = request.getPathInfo();
		parsePathInfo(pathInfo);
		String bean = action.substring(0, 1).toUpperCase() + action.substring(1) + "AjaxBean";
		String className = ajaxBeanPackage + "." + bean;
		request.setAttribute("ajaxEvent", event);
		try {
			Class beanClass = Class.forName(className);
			Object ajaxBean = beanClass.newInstance();

			// invoke method setConfig(ServletConfig config)
			Method setConfig = Class.forName(className).getMethod("init", ServletConfig.class, HttpSession.class);
			setConfig.invoke(ajaxBean, config, request.getSession());

			// invoke method setParams(String[] params)
			if (params != null) {
				Method setParams = Class.forName(className).getMethod("setParams", String[].class);
				setParams.invoke(ajaxBean, (Object) params);
			}

			// invoke event method
			Method eventMethod = Class.forName(className).getMethod(event, HttpServletRequest.class,
				HttpServletResponse.class);
			eventMethod.invoke(ajaxBean, request, response);

		} catch (Exception e) {
			throw new ServletException("Invoke AjaxBean Method Error!", e);
		}
	}

	private void parsePathInfo(String pathInfo) throws ServletException {

		// split path info
		String[] path = urlParserPattern.split(pathInfo);
		if (path.length < 3) {
			throw new ServletException("Invalid URL Pattern: /ajax/{bean}/{event}/{params}/...");
		}
		action = path[1];
		event = path[2];
		if (path.length > 3) {

			// remove /action/event add store params in new string array
			String[] params = new String[path.length - 3];
			System.arraycopy(path, 3, params, 0, path.length - 3);
			this.params = params;
		} else {
			this.params = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		this.config = config;
	}
}
