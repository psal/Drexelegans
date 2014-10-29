/**
 * 
 */
package helpers;

import java.sql.ResultSet;
import java.sql.SQLException;

import generic.WormwiringDatabaseManager;

/**
 * @author Andrew W.E. McDonald
 *
 */
public class ExtraneousThings { 
	
	public static void main (String[] args){

		WormwiringDatabaseManager wdm = new WormwiringDatabaseManager();
		
		// create an array of neuron names for copy and paste purposes
		ResultSet names = wdm.select_neuron_names();
		System.out.print("[");
		int count = 0;
		try {
			while (names.next()){
				if (count > 0){
					System.out.print(", ");// so we don't print one at the end.. not a huge deal, but it's easily avoided.
				}
				System.out.print("'"+names.getString(1)+"'");
				count++;
			}
			System.out.print("]");
			System.out.println("\n"+count);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
