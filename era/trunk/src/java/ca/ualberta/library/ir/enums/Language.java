/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir-2.0
 * $Id: Language.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.enums;

/**
 * The Language class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public enum Language {
	en("English")/* , fr("Française"), it("Italiano"), de("Deutsch") , th("Thai") */;

	private String name;

	private Language(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
