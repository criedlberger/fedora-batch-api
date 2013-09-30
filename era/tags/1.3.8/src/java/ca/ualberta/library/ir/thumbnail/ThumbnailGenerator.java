/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: ThumbnailGenerator.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.thumbnail;

import java.io.File;

import net.bull.javamelody.MonitoredWithSpring;

/**
 * The ThumbnailGenerator class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@MonitoredWithSpring
public interface ThumbnailGenerator {

	public void init();

	public boolean canGenerate(final String contentType);

	public File generate(String contentType, int thumbWidth, int thumbHeight, final File input) throws Exception;

	public void destroy();

}
