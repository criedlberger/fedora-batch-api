/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: PDFUtils.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;

/**
 * The PDFUtils class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class PDFUtils {
	private static final Log log = LogFactory.getLog(PDFUtils.class);

	public static boolean isPDFDocument(File file) {
		PDDocument doc = null;
		try {
			doc = PDDocument.load(file);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException e) {
					log.error("Could not close document!", e);
				}
			}
		}
	}

	public static boolean isPDFDocument(InputStream input) {
		PDDocument doc = null;
		try {
			PDFParser parser = new PDFParser(input);
			doc = parser.getPDDocument();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (doc != null) {
				try {
					doc.close();
				} catch (IOException e) {
					log.error("Could not close document!", e);
				}
			}
		}
	}
}
