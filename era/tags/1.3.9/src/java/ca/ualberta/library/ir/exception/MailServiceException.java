/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: MailServiceException.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.exception;

/**
 * The MailServiceException class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class MailServiceException extends Exception {

	private static final long serialVersionUID = 4793795726826712L;

	/**
	 * The MailServiceException class constructor.
	 */
	public MailServiceException() {
		super();
		// TODO: Implement this constructor.
	}

	/**
	 * The MailServiceException class constructor.
	 * @param message
	 */
	public MailServiceException(String message) {
		super(message);
	}

	/**
	 * The MailServiceException class constructor.
	 * @param cause
	 */
	public MailServiceException(Throwable cause) {
		super(cause);
	}

	/**
	 * The MailServiceException class constructor.
	 * @param message
	 * @param cause
	 */
	public MailServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
