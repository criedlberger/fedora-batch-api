/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: ZipUtils.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.utils;

import static java.lang.System.out;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * The ZipUtils class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class ZipUtils {
	private static String path;

	/**
	 * The zipDir method compresses directory tree with full path.
	 * 
	 * @param dir2zip
	 * @param zos
	 * @throws Exception
	 */
	public static void zipDir(String dir2zip, ZipOutputStream zos) throws Exception {
		try {
			// create a new File object based on the directory we have to zip
			File zipDir = new File(dir2zip);
			// get a listing of the directory content
			String[] dirList = zipDir.list();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;
			// loop through dirList, and zip the files
			for (int i = 0; i < dirList.length; i++) {
				File f = new File(zipDir, dirList[i]);
				if (f.isDirectory()) {
					// if the File object is a directory, call this
					// function again to add its content recursively
					String filePath = f.getPath();
					zipDir(filePath, zos);
					// loop again
					continue;
				}
				// if we reached here, the File object f was not a directory
				// create a FileInputStream on top of f
				FileInputStream fis = new FileInputStream(f);
				// create a new zip entry
				ZipEntry anEntry = new ZipEntry(f.getPath());
				// place the zip entry in the ZipOutputStream object
				zos.putNextEntry(anEntry);
				// now write the content of the file to the ZipOutputStream
				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				// close the Stream
				fis.close();
			}
		} catch (Exception e) {
			throw new Exception("Could not zip directory: " + dir2zip + "!", e);
		}
	}

	/**
	 * The zipDirectory method compresses directory tree as root path.
	 * 
	 * @param dir2zip
	 * @param zipFile
	 * @return
	 * @throws Exception
	 */
	public static File zipDirectory(String dir2zip, String zipFile) throws Exception {
		path = dir2zip;
		File zip = new File(zipFile);
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zip));
		zipDirTree(dir2zip, zos);
		zos.close();
		return zip;
	}

	private static void zipDirTree(String dir2zip, ZipOutputStream zos) throws Exception {
		try {

			// create a new File object based on the directory we have to zip
			File zipDir = new File(dir2zip);
			// get a listing of the directory content
			String[] dirList = zipDir.list();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;
			// loop through dirList, and zip the files
			for (int i = 0; i < dirList.length; i++) {
				File f = new File(zipDir, dirList[i]);
				if (f.isDirectory()) {
					// if the File object is a directory, call this
					// function again to add its content recursively
					String filePath = f.getPath();
					zipDirTree(filePath, zos);
					// loop again
					continue;
				}
				// if we reached here, the File object f was not a directory
				// create a FileInputStream on top of f
				FileInputStream fis = new FileInputStream(f);
				// create a new zip entry
				out.println("zipEntry: " + f.getPath().substring(path.length() + 1));
				ZipEntry anEntry = new ZipEntry(f.getPath().substring(path.length() + 1));
				// place the zip entry in the ZipOutputStream object
				zos.putNextEntry(anEntry);
				// now write the content of the file to the ZipOutputStream
				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				// close the Stream
				fis.close();
			}
		} catch (Exception e) {
			throw new Exception("Could not zip directory: " + dir2zip + "!", e);
		} finally {
		}
	}
}