/**
 * Piyapong Charoenwattana
 * Project: era
 * $Id: StripesExceptionHandler.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.exception;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.stripes.controller.FileUploadLimitExceededException;
import net.sourceforge.stripes.exception.ActionBeanNotFoundException;
import net.sourceforge.stripes.exception.DefaultExceptionHandler;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The StripesExceptionHandler class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class StripesExceptionHandler extends DefaultExceptionHandler {
	private static final Log log = LogFactory.getLog(StripesExceptionHandler.class);

	/**
	 * The StripesExceptionHandler class constructor.
	 */
	public StripesExceptionHandler() {
		super();
	}

	/**
	 * 
	 * @see net.sourceforge.stripes.exception.DefaultExceptionHandler#handle(java.lang.Throwable,
	 *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void handle(Throwable t, HttpServletRequest req, HttpServletResponse resp) throws ServletException,
		IOException {
		if (t instanceof ActionBeanNotFoundException) {
			// req.getSession().invalidate();
			logWarn(req, t);
			log.warn("server response: " + HttpServletResponse.SC_MOVED_PERMANENTLY + " "
				+ HttpStatus.getStatusText(HttpServletResponse.SC_MOVED_PERMANENTLY));
			resp.sendError(HttpServletResponse.SC_MOVED_PERMANENTLY, HttpStatus
				.getStatusText(HttpServletResponse.SC_MOVED_PERMANENTLY));
		} else if (t instanceof StringIndexOutOfBoundsException) {
			// req.getSession().invalidate();
			log.warn("server response: " + HttpServletResponse.SC_NOT_FOUND + " "
				+ HttpStatus.getStatusText(HttpServletResponse.SC_NOT_FOUND));
			logWarn(req, t);
			resp
				.sendError(HttpServletResponse.SC_NOT_FOUND, HttpStatus.getStatusText(HttpServletResponse.SC_NOT_FOUND));
		} else if (t instanceof FileUploadLimitExceededException) {
			resp.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, HttpStatus
				.getStatusText(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE));
		} else {
			// req.getSession().invalidate();
			logError(req, t);
			// super.handle(t, req, resp);
			log.error("server response: " + HttpServletResponse.SC_INTERNAL_SERVER_ERROR + " "
				+ HttpStatus.getStatusText(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, HttpStatus
				.getStatusText(HttpServletResponse.SC_INTERNAL_SERVER_ERROR));
		}
	}

	private void logWarn(HttpServletRequest req, Throwable t) {
		log.warn("remote host: [" + req.getRemoteHost() + "] port: [" + req.getRemotePort() + "] address: ["
			+ req.getRemoteAddr() + "] User-Agent: [" + req.getHeader("User-Agent") + "]");
		log.warn("url: " + req.getRequestURL() + " query: " + req.getQueryString());
		log.warn("message: Could not locate an ActionBean that is bound to the URL [" + req.getRequestURL() + "].");
	}

	@SuppressWarnings("unchecked")
	private void logError(HttpServletRequest req, Throwable t) {
		log.warn("remote host: [" + req.getRemoteHost() + "] port: [" + req.getRemotePort() + "] address: ["
			+ req.getRemoteAddr() + "] User-Agent: [" + req.getHeader("User-Agent") + "]");
		log.warn("url: " + req.getRequestURL() + " query: " + req.getQueryString());
		log.warn("header:");
		Enumeration<String> hds = req.getHeaderNames();
		while (hds.hasMoreElements()) {
			String hd = hds.nextElement();
			log.warn("- " + hd + ": " + req.getHeader(hd));
		}
		log.warn("Stripes Exception Error!", t);
	}
}