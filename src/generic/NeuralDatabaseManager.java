package generic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NeuralDatabaseManager {

	Connection conn;
	Statement statement;
	
	//public static String[] tables = new String[]{"neuron_connectome", "neuron_fixed_points", "neuron_type","neurons_to_muscle", "sensory"};
	public static String[] tables = new String[]{"n2u", "jse"};
	
	public NeuralDatabaseManager(boolean drop_tables){
			try {
				Class.forName("org.sqlite.JDBC"); 

				conn = DriverManager.getConnection("jdbc:sqlite:neural.db");
				statement = conn.createStatement();
				if (drop_tables){
					for (int i = 0; i < this.tables.length; i++){
						String drop = "drop table if exists "+this.tables[i];
						statement.execute(drop);
					}
				}
				String[] tables = new String[this.tables.length];
				tables[0] = "create table if not exists "+this.tables[0]+" (id integer auto_increment primary key, pre text, post text, sections integer, type text, description text, neurotrans text)";
				tables[1] = "create table if not exists "+this.tables[1]+" (id integer auto_increment primary key, pre text, post text, sections integer, type text, description text, neurotrans text)";
				//tables[0] = "create table if not exists "+this.tables[0]+" ( id integer auto_increment primary key, origin text, target text, synapse_type text, num_synapses integer)";
				//tables[1] = "create table if not exists "+this.tables[1]+" ( id integer auto_increment primary key, neuron text, soma_position real, soma_region text, span text, ambiguity text, num_head_synapses integer, num_tail_synapses integer, num_mid_synapses integer, num_head_sending_synapses integer, num_head_receiving_synapses integer , num_mid_sending_synapses integer, num_mid_receiving_synapses integer, num_tail_sending_synapses integer, num_tail_receiving_synapses integer, AY_ganglion_designation text, AY_number integer)";
				//tables[2] = "create table if not exists "+this.tables[2]+" ( id integer auto_increment primary key, neuron text, landmark text, landmark_position real, weight real)";
				for (int i = 0; i < tables.length; i++){
					statement.execute(tables[i]);
				}
			} catch (ClassNotFoundException e) {
				// NOTE Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// NOTE Auto-generated catch block
				e.printStackTrace();
			}

	}

	public Connection get_conn(){
		return conn;
	}

	public Statement get_statement(){
		return statement;
	}

	/*
	 * selects
	 */
	
	public ResultSet select_n2u_records(){
		String query = "select * from "+tables[0];
		try {
			ResultSet results = statement.executeQuery(query);
			return results;
		} catch (SQLException e) {
			// NOTE Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	
	
	public ResultSet select_jse_records(){
		String query = "select * from "+tables[1];
		try {
			ResultSet results = statement.executeQuery(query);
			return results;
		} catch (SQLException e) {
			// NOTE Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	
	
	/*
	public ResultSet select_evaporation_records(double evaporation, int step){
		String query = "select * from "+tables[4]+" where time_step = "+step+" AND evaporation = "+evaporation;
		try {
			ResultSet results = statement.executeQuery(query);
			return results;
		} catch (SQLException e) {
			// NOTE Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	
	
	public ResultSet select_num_clusters_records(int step){
		String query = "select * from "+tables[3]+" where time_step = "+step;
		try {
			ResultSet results = statement.executeQuery(query);
			return results;
		} catch (SQLException e) {
			// NOTE Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ResultSet select_avg_dist_to_others_vs_ideal_temp_records(double low, double high){
		String query = "select * from "+tables[2]+" where ideal_temp > "+low+" AND ideal_temp <= "+high; // we will miss records at 17000 (if any exist), but there shouldn't be enough of those to make any different with what we're doing.
		try {
			ResultSet results = statement.executeQuery(query);
			return results;
		} catch (SQLException e) {
			// NOTE Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}
	
	public ResultSet select_avg_dist_to_neighbors_records(int num_neighbors){
		String query = "select * from "+tables[1]+" where x = "+num_neighbors;
		try {
			ResultSet results = statement.executeQuery(query);
			return results;
		} catch (SQLException e) {
			// NOTE Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	public ResultSet select_avg_dist_vs_time_step_records(int step){
		String query = "select * from "+tables[0]+" where time_step = "+step;
		try {
			ResultSet results = statement.executeQuery(query);
			return results;
		} catch (SQLException e) {
			// NOTE Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	*/
	
	
	/*
	 * inserts
	 */

	public boolean insert_n2u_record(String[] record){
		String query = "insert into "+tables[0]+" (pre, post, sections, type)"
				+ "values ("+record[0]+","+record[1]+","+Integer.parseInt(record[2])+","+record[3]+")";
		try {
			statement.executeUpdate(query);
		} catch (SQLException sqle){
			sqle.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean insert_jse_record(String[] record){
		// again, note that the record will have an index in the first column.. we don't want that, so we start at column 1 instead of 0.
		String query = "insert into "+tables[0]+" (pre, post, sections, type)"
				+ "values ("+record[1]+","+record[2]+","+Integer.parseInt(record[3])+","+record[4]+")";
		try {
			statement.executeUpdate(query);
		} catch (SQLException sqle){
			sqle.printStackTrace();
			return false;
		}
		return true;
	}
				
	
	/*
	public boolean insert_neuron_connection_record(String[] record){
		String query = "insert into "+tables[0]+" (neuron_1, neuron_2 , synapse_type, num_synapses)"
				+ "values ("+record[0]+","+record[1]+","+record[2]+","+Integer.parseInt(record[3])+")";
		try {
			statement.executeUpdate(query);
		} catch (SQLException sqle){
			sqle.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean insert_neuron_fixed_point_record(String[] record){
		String query = "insert into "+tables[1]+" (neuron, soma_position, soma_region, span, ambiguity, "
				+ "num_head_synapses, num_tail_synapses, num_mid_synapses, num_head_sending_synapses, "
				+ "num_head_receiving_synapses, num_mid_sending_synapses, num_mid_receiving_synapses, "
				+ "num_tail_sending_synapses, num_tail_receiving_synapses, AY_ganglion_designation, AY_number)"
				+ "values ("+record[0]+","+Double.parseDouble(record[1])+","+record[2]+","+record[3]+","+record[4]+","
				+Integer.parseInt(record[5])+","+Integer.parseInt(record[6])+","+Integer.parseInt(record[7])+","+Integer.parseInt(record[8])+","
				+Integer.parseInt(record[9])+","+Integer.parseInt(record[10])+","+Integer.parseInt(record[11])+","
				+Integer.parseInt(record[12])+","+Integer.parseInt(record[13])+","+record[14]+","+Integer.parseInt(record[15])+")";
		try {
			statement.executeUpdate(query);
		} catch (SQLException sqle){
			sqle.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean insert_neuron_type_record(String[] record){
		String query = "insert into "+tables[2]+"( neuron text, landmark text, landmark_position real, weight real)"
				+ "values ("+record[0]+","+record[1]+","+Double.parseDouble(record[2])+","+Double.parseDouble(record[3])+")";
		try {
			statement.executeUpdate(query);
		} catch (SQLException sqle){
			sqle.printStackTrace();
			return false;
		}
		return true;
	}
	*/
	
}

