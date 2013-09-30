/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: ThumbnailGenerator.java 5485 2012-08-13 17:40:46Z pcharoen $
 */

package ca.ualberta.library.ir.thumbnail;

import java.io.File;

/**
 * The ThumbnailGenerator class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5485 $ $Date: 2012-08-13 11:40:46 -0600 (Mon, 13 Aug 2012) $
 */
public interface ThumbnailGenerator {

	public void init();

	public boolean canGenerate(final String contentType);

	public File generate(String contentType, int thumbWidth, int thumbHeight, final File input) throws Exception;

	public void destroy();

}
