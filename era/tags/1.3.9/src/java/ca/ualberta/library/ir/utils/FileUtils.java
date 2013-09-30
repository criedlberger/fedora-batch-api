/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir-2.0
 * $Id: FileUtils.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.utils;

import java.io.File;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

/**
 * The FileUtils class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class FileUtils {
	private static final Pattern windowsFilenamePattern = Pattern.compile("\\\\|\\/|:|\\*|\\?|\"|<|>|\\|");

	/**
	 * The FileUtils class constructor.
	 */
	public FileUtils() {
		super();
	}

	public static String toWindowsFilename(String filename) {
		return windowsFilenamePattern.matcher(filename).replaceAll("-");
	}

	public static String getContentType(File file) {
		return new MimetypesFileTypeMap().getContentType(file);
	}
}