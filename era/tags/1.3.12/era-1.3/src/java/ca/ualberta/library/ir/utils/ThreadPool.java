/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: ThreadPool.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The ThreadPool class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class ThreadPool {
	private static final Log log = LogFactory.getLog(ThreadPool.class);

	// cached thread pool
	private static final ExecutorService cachedThreadPool;

	static {
		// log.debug("initializing cached thread pool...");
		cachedThreadPool = Executors.newCachedThreadPool();
	}

	public static ExecutorService getCachedThreadPool() {
		return cachedThreadPool;
	}

	public static void shutdown() {
		// log.debug("shutting down thread pool...");
		cachedThreadPool.shutdown();
	}

}
