/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: era
 * $Id: SolrActionBean.java 5430 2012-07-12 22:30:19Z pcharoen $
 */

package ca.ualberta.library.ir.action;

import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;
import net.sourceforge.stripes.validation.Validate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The SolrActionBean class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca>Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
@UrlBinding("/public/solr/{$event}/{q}")
public class SolrActionBean extends BaseActionBean {
	private static final Log log = LogFactory.getLog(SolrActionBean.class);

	@Validate(required = true)
	private String q;

	private String fq;

	private String sort;

	private String order;

	private int start;

	private int rows;

	/**
	 * The SolrActionBean class constructor.
	 */
	public SolrActionBean() {
		super();
	}

	@HandlesEvent("select")
	public Resolution select() {
		try {
			String result = services.select(q);
			return new StreamingResolution("text/xml", new String(result));
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	@HandlesEvent("query")
	public Resolution query() {
		try {
			String result = services.query(q, fq, sort, order, start, rows);
			return new StreamingResolution("text/xml", new String(result));
		} catch (Exception e) {
			log.error("Could not process this request!", e);
			return forwardExceptionError("Could not process this request!", e);
		}
	}

	/**
	 * The setQ setter method.
	 * 
	 * @param q the q to set
	 */
	@Override
	public void setQ(String q) {
		this.q = q;
	}

	/**
	 * The setFq setter method.
	 * 
	 * @param fq the fq to set
	 */
	@Override
	public void setFq(String fq) {
		this.fq = fq;
	}

	/**
	 * The setSort setter method.
	 * 
	 * @param sort the sort to set
	 */
	@Override
	public void setSort(String sort) {
		this.sort = sort;
	}

	/**
	 * The setOrder setter method.
	 * 
	 * @param order the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
	}

	/**
	 * The setStart setter method.
	 * 
	 * @param start the start to set
	 */
	public void setStart(int start) {
		this.start = start;
	}

	/**
	 * The setRows setter method.
	 * 
	 * @param rows the rows to set
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}
}
