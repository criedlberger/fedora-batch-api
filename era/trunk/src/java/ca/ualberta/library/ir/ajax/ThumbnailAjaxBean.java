/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: ThumbnailAjaxBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.ajax;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.thumbnail.ThumbnailGenerator;

import ca.ualberta.library.ir.action.ActionConstants;
import ca.ualberta.library.ir.utils.FileUtils;

/**
 * The ThumbnailAjaxBean class provides methods for uploading and getting item/collection/community thumbnail.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class ThumbnailAjaxBean extends BaseAjaxBean {
	private static final Log log = LogFactory.getLog(ThumbnailAjaxBean.class);
	private String tmpPath;

	/**
	 * The ThumbnailAjaxBean class constructor.
	 */
	public ThumbnailAjaxBean() {
		super();
	}

	/**
	 * The get method is for thumbnail upload Ajax to get and show thumbnail.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void get(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		File thumbnail = null;
		FileInputStream fis = null;
		try {
			String filename = params[0];
			// log.debug("getting file: " + tmpPath + File.separator + filename);
			thumbnail = new File(tmpPath + File.separator + filename);
			fis = new FileInputStream(thumbnail);
			IOUtils.copy(fis, response.getOutputStream());
			response.setContentType(FileUtils.getContentType(thumbnail));
			response.getOutputStream().flush();
		} catch (Exception e) {
			log.error("Could not get generated thumbnail!", e);
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	/**
	 * The put method is for thumbnail upload Ajax to upload thumbnail file. The method write the file and filename.
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void put(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String filename = null;
		File tmp = null;
		File thumbnail = null;
		try {
			// log.debug("uploading file...");
			// Create a factory for disk-based file items
			FileItemFactory factory = new DiskFileItemFactory();

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			List<FileItem> items = upload.parseRequest(request);

			// Process the uploaded items
			for (FileItem item : items) {
				if (!item.isFormField()) {

					String ctype = item.getContentType();
					// log.debug("contentType: " + ctype);
					if (ThumbnailGenerator.canGenerate(ctype)) {

						// write upload file to tmp
						tmp = File.createTempFile("upload_", ".tmp", new File(tmpPath));
						item.write(tmp);
						item.delete();

						// generate thumbnail
						try {
							thumbnail = ThumbnailGenerator.generate(ctype, ActionConstants.thumbnailWidth,
								ActionConstants.thumbnailHeight, tmp);
							filename = thumbnail.getName();
						} catch (Exception e) {
							log.warn("Could not generate thumbnail!", e);
						}
					}
					break;
				}
			}
		} catch (Exception e) {
			log.error("Could not upload or transform thumbnail!", e);
		} finally {
			if (tmp != null) {
				tmp.delete();
			}
		}
		// log.debug("filename: " + filename);
		request.getSession().setAttribute("thumbnailFilename", filename);

		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.write("<div id=\"filename\">" + filename + "</div>");
		out.flush();
		// log.debug("transformation completed!");
	}

	/**
	 * The getFilename method.
	 * 
	 * @deprecated Use get method instead (to be deleted)
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	@Deprecated
	public void getFilename(HttpServletRequest request, HttpServletResponse response) throws ServletException,
		IOException {
		response.setContentType("image/jpeg");
		String filename = (String) request.getSession().getAttribute("thumbnailFilename");
		// log.debug("getting filename: " + filename);
		PrintWriter out = response.getWriter();
		out.write(filename == null ? "null" : filename);
	}

	/*
	 * (non-Javadoc)
	 * @see ca.ualberta.library.ir.ajax.BaseAjaxBean#setConfig(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config, HttpSession session) throws ServletException {
		super.init(config, session);
		tmpPath = config.getServletContext().getRealPath("/tmp");
	}

}
