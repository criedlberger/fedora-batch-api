/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: UploadAjaxBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.ajax;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The UploadAjaxBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class UploadAjaxBean extends BaseAjaxBean {
	private static final Log log = LogFactory.getLog(UploadAjaxBean.class);
	private String tmpPath;

	/**
	 * The UploadAjaxBean class constructor.
	 */
	public UploadAjaxBean() {
		super();
	}

	public void get(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String filename = params[0];
		// log.debug("getting file: " + filename);
		FileInputStream tmp = new FileInputStream(tmpPath + "/" + filename);
		IOUtils.copy(tmp, resp.getOutputStream());
		resp.getOutputStream().flush();
		tmp.close();
	}

	@SuppressWarnings("unchecked")
	public void put(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			List<FileItem> items = upload.parseRequest(req);

			// Process the uploaded items
			String filename = null;
			for (FileItem item : items) {
				if (!item.isFormField()) {
					File tmp = File.createTempFile(item.getContentType().replace('/', '_') + "_upload_", ".tmp",
						new File(tmpPath));
					filename = tmp.getName();
					// log.debug("writing file: " + filename + " contentType: " + item.getContentType());
					FileOutputStream fos = new FileOutputStream(tmp);
					IOUtils.copy(item.getInputStream(), fos);
					fos.flush();
					fos.close();
					item.getInputStream().close();
					tmp.deleteOnExit();
				}
			}
			resp.getWriter().write("<div id=\"filename\">" + filename + "</div>");
		} catch (FileUploadException e) {
			// log.debug("Could not upload!", e);
			throw new ServletException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ca.ualberta.library.ir.ajax.BaseAjaxBean#setConfig(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config, HttpSession session) throws ServletException {
		super.init(config, session);
		tmpPath = config.getServletContext().getRealPath("/tmp");
	}

}
