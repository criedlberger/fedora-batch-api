/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: HandleClientException.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.exception;

/**
 * The HandleClientException class.
 *
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class HandleClientException extends Exception {

	private static final long serialVersionUID = -8931491878304096649L;

	/**
	 * The HandleClientException class constructor.
	 */
	public HandleClientException() {
		super();
	}

	/**
	 * The HandleClientException class constructor.
	 * @param message
	 */
	public HandleClientException(String message) {
		super(message);
	}

	/**
	 * The HandleClientException class constructor.
	 * @param cause
	 */
	public HandleClientException(Throwable cause) {
		super(cause);
	}

	/**
	 * The HandleClientException class constructor.
	 * @param message
	 * @param cause
	 */
	public HandleClientException(String message, Throwable cause) {
		super(message, cause);
	}

}
