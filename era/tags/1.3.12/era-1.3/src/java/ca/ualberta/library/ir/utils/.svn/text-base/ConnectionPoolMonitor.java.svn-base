package ca.ualberta.library.ir.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.sourceforge.stripes.integration.spring.SpringBean;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConnectionPoolMonitor implements Runnable {

	private final Log log = LogFactory.getLog(ConnectionPoolMonitor.class);
	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private BasicDataSource ds;

	public ConnectionPoolMonitor() {
		super();
		log.info("Time, Number of Active Connections, Number of Idle Connections");
	}

	@SpringBean("dataSource")
	public void injectServiceFacade(BasicDataSource ds) {
		this.ds = ds;
	}

	public void writeLog() {
		log.info(format.format(new Date()) + ", " + ds.getNumActive() + ", " + ds.getNumIdle());
	}

	@Override
	public void run() {
		writeLog();
	}
}
