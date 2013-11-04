/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: ServiceException.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.exception;

/**
 * The ServiceException class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class ServiceException extends Exception {

	private static final long serialVersionUID = -3931572232703499037L;

	/**
	 * The ServiceException class constructor.
	 */
	public ServiceException() {
		super();
	}

	/**
	 * The ServiceException class constructor.
	 * @param message
	 */
	public ServiceException(String message) {
		super(message);
	}

	/**
	 * The ServiceException class constructor.
	 * @param cause
	 */
	public ServiceException(Throwable cause) {
		super(cause);
	}

	/**
	 * The ServiceException class constructor.
	 * @param message
	 * @param cause
	 */
	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}