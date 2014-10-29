package generic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * class for reading the wormwiring.db database, created by a python script i wrote using
 * scrapy. So, wormwiring.db is read-only... even though there are no mechanisms in pace to
 * force that.
 * @author Andrew W.E. McDonald
 *
 */
public class WormwiringDatabaseManager {

	Connection conn;
	Statement statement;
	
	public WormwiringDatabaseManager(){
			try {
				Class.forName("org.sqlite.JDBC");
				conn = DriverManager.getConnection("jdbc:sqlite:wormwiring.db");
				statement = conn.createStatement();
			} catch (ClassNotFoundException e) {
				// NOTE Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// NOTE Auto-generated catch block
				e.printStackTrace();
			}

	}

	/*
	 * selects
	 */
	
	/**
	 * 
	 * @param pre_name
	 * @return
	 */
	public ResultSet select_records(String pre_name){
		String query = "select * from neurons where name like '"+pre_name+"%'";
		try {
			ResultSet results = statement.executeQuery(query);
			return results;
		} catch (SQLException e) {
			// NOTE Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	
	
	public ResultSet select_neuron_names(){
		String query = "select distinct name from neurons";
		try {
			ResultSet results = statement.executeQuery(query);
			return results;
		} catch (SQLException e) {
			// NOTE Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	
	
		
}

