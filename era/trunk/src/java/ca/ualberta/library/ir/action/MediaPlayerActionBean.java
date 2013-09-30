/**
 * Information Technology Services
 * University of Alberta Libraries
 * Project: ir
 * $Id: MediaPlayerActionBean.java 5606 2012-10-10 16:45:09Z pcharoen $
 */
package ca.ualberta.library.ir.action;

import java.io.IOException;

import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.security.action.Secure;
import net.sourceforge.stripes.validation.Validate;
import net.sourceforge.stripes.validation.ValidateNestedProperties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.ualberta.library.ir.model.fedora.Datastream;

/**
 * The ViewActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5606 $ $Date: 2012-10-10 10:45:09 -0600 (Wed, 10 Oct 2012) $
 */
@UrlBinding("/public/mediaPlayer/{$event}/{datastream.pid}/{datastream.dsId}")
public class MediaPlayerActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(MediaPlayerActionBean.class);

	@ValidateNestedProperties({ @Validate(field = "pid", required = true), @Validate(field = "dsId", required = true) })
	private Datastream datastream;

	private String type;

	/**
	 * The ViewActionBean class constructor.
	 */
	public MediaPlayerActionBean() {
		super();
	}

	@HandlesEvent("play")
	@Secure(roles = "/community/read")
	public Resolution play() throws IOException {
		try {
			org.fcrepo.server.types.gen.Datastream dstm = services.getDatastream(datastream.getPid(),
				datastream.getDsId());
			datastream.setLabel(new String(dstm.getLabel()));
			datastream.setMimeType(dstm.getMIMEType());
			datastream.setLocation(services.getRestServiceUrl() + "/get/" + datastream.getPid() + "/" + dstm.getID());
			// log.debug(datastream.toString());
		} catch (Exception e) {
			log.error("Could not find object!", e);
			throw new IOException("Could not find object!");
		}
		return new ForwardResolution(uiPath + "/public/mediaPlayer.jsp");
	}

	/**
	 * The getDatastream getter method.
	 * 
	 * @return the datastream
	 */
	public Datastream getDatastream() {
		return datastream;
	}

	/**
	 * The setDatastream setter method.
	 * 
	 * @param datastream the datastream to set
	 */
	public void setDatastream(Datastream datastream) {
		this.datastream = datastream;
	}

	/**
	 * The getType getter method.
	 * 
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * The setType setter method.
	 * 
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

}
