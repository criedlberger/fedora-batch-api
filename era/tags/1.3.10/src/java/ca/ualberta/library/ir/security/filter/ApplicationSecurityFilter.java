package ca.ualberta.library.ir.security.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.utils.ApplicationProperties;

/**
 * The ApplicationSecurityFilter class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class ApplicationSecurityFilter implements Filter {
	private static final Log log = LogFactory.getLog(ApplicationSecurityFilter.class);

	protected FilterConfig config;
	protected static List<String> publicUrlPatterns = new ArrayList<String>();
	protected static List<Pattern> patterns = new ArrayList<Pattern>();
	protected static Pattern urlPattern = Pattern.compile(",");
	private static String httpServerUrl = ApplicationProperties.getString("http.server.url");
	private static String httpsServerUrl = ApplicationProperties.getString("https.server.url");

	/**
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {
		// log.trace("initializing application security filter...");
		this.config = filterConfig;
		String publicUrls = StringUtils.trimToNull(filterConfig.getInitParameter("publicUrlPatterns"));
		if (publicUrls != null) {
			String[] urls = urlPattern.split(publicUrls);
			for (String url : urls) {
				publicUrlPatterns.add(url.trim());
				patterns.add(Pattern.compile(url.trim()));
			}
		}
	}

	/**
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
	 *      javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
		throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		if (request.getSession().getAttribute("user") != null) {
			filterChain.doFilter(request, response);
		} else if (isPublicResource(request)) {
			filterChain.doFilter(request, response);
		} else {

			// Redirect the user to the login page, noting where they were coming from
			String targetUrl = URLEncoder.encode(httpServerUrl + request.getContextPath()
				+ (request.getServletPath() == null ? "" : request.getServletPath())
				+ (request.getPathInfo() == null ? "" : request.getPathInfo())
				+ (request.getQueryString() == null ? "" : "?" + request.getQueryString()), "UTF-8");
			// log.trace("targetUrl: " + targetUrl);
			response.sendRedirect(httpsServerUrl + request.getContextPath() + "/public/login?url=" + targetUrl);
		}
	}

	/**
	 * The isPublicResource method.
	 * 
	 * @param request
	 * @return
	 */
	protected boolean isPublicResource(HttpServletRequest request) {
		String path = request.getServletPath() + StringUtils.trimToEmpty(request.getPathInfo());
		// log.trace("path: " + path);
		if (publicUrlPatterns.contains(path)) {
			return true;
		} else {
			for (Pattern pattern : patterns) {
				if (pattern.matcher(path).find()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// log.trace("destroying application security filter...");
	}
}
