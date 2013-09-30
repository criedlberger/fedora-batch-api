package ca.ualberta.library.fedora.postgressql;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Handle {

	Connection connection = null;
	
	private static final Log log = LogFactory.getLog(Handle.class);
	
	public Handle() {
		
		String userName = null;
		String password = null;
		String databaseURL = null;
		String mySqlClass  = null;
		
		Properties props = new Properties();
		
		try
		{
			props.load(new FileInputStream("PostgresSql.properties"));
			  
			userName = props.getProperty("userName");
			password = props.getProperty("password");
			databaseURL = props.getProperty("databaseURL");
			mySqlClass = props.getProperty("PostgresSqlClass");
			
			Class.forName(mySqlClass).newInstance();
			connection = DriverManager.getConnection (databaseURL, userName, password);
			log.info("Database connection established");
	   }
	   catch (Exception e)
	   {
	       log.error("Cannot connect to database server");
	       log.error(e.getMessage());
	   }
		
	}
	
	public void insertHandle(String uuid) {
		
		String id = " ";
		String type = "2";
		
		if (connection != null) {
			try {
				Statement statement = connection.createStatement();
			
				uuid = "\"" + uuid + "\"";
				String update = "INSERT INTO handle(type, pid) VALUES (" + type + "," + uuid + ")";
			
				statement.executeUpdate(update);
		   
				statement.close();
			}
			catch (SQLException e) {
				log.error("SQL Exception: " + e.getMessage() + " for pid: " + uuid);
				System.exit(-1);
			}
		}	
	}
	
	public String readHandle(String uuid) {
		
		String id = null;
		
		if (connection != null)
		{
			try {
				Statement statement = connection.createStatement();
			   
				uuid = "\"" + uuid + "\"";
				String query = "SELECT id FROM handle where pid = " + uuid;
		   
				statement.executeQuery(query);
		   
				ResultSet resultSet = statement.getResultSet();
				while (resultSet.next())
				{
					id = resultSet.getString("id");
				}
				
				resultSet.close();
				
				statement.close();
			}
			catch (SQLException e) {
				log.error("SQL Exception: " + e.getMessage() + " for pid: " + uuid);
				System.exit(-1);
			}
		}
		
		return id;
	}
}
