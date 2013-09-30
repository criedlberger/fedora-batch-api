/**
 * University of Alberta Libraries
 * Information Technology Services
 * Project: ir-2.0
 * $Id: ProcessDuration.java 5430 2012-07-12 22:30:19Z pcharoen $
 */
package ca.ualberta.library.ir.utils;

import java.util.Calendar;

import org.apache.commons.lang.time.DurationFormatUtils;

/**
 * The ProcessDuration class.
 * 
 * @author <a href="mailto:piyapong.charoenwattana@ualberta.ca">Piyapong Charoenwattana</a>
 * @version $Revision: 5430 $ $Date: 2012-07-12 16:30:19 -0600 (Thu, 12 Jul 2012) $
 */
public class ProcessDuration {
	private long start;
	private long end;

	/**
	 * The ProcessDuration class constructor.
	 */
	public ProcessDuration() {
		super();
	}

	public Calendar start() {
		Calendar calendar = Calendar.getInstance();
		start = calendar.getTimeInMillis();
		return calendar;
	}

	public Calendar stop() {
		Calendar calendar = Calendar.getInstance();
		end = calendar.getTimeInMillis();
		return calendar;
	}

	public String getDuration(String durationFormat) {
		return DurationFormatUtils.formatPeriod(start, end, durationFormat);
	}

	public String getDurationHMS() {
		return DurationFormatUtils.formatDurationHMS(end - start);
	}

}
