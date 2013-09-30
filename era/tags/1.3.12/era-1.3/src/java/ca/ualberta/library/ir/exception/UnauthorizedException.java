/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir
 * $Id: UnauthorizedException.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.exception;

/**
 * The UnauthorizedException class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class UnauthorizedException extends Exception {

	private static final long serialVersionUID = -6806887485224226465L;

	/**
	 * The UnauthorizedException class constructor.
	 */
	public UnauthorizedException() {
		super();
	}

	/**
	 * The UnauthorizedException class constructor.
	 * @param message
	 */
	public UnauthorizedException(String message) {
		super(message);
	}

	/**
	 * The UnauthorizedException class constructor.
	 * @param cause
	 */
	public UnauthorizedException(Throwable cause) {
		super(cause);
	}

	/**
	 * The UnauthorizedException class constructor.
	 * @param message
	 * @param cause
	 */
	public UnauthorizedException(String message, Throwable cause) {
		super(message, cause);
	}

}
