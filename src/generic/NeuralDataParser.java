package generic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * 
 * Reads .csv files, and inserts contents into database, via {@link NeuralDatabaseManager}
 * 
 * @author Andrew W.E. McDonald
 * 
 *
 */
public class NeuralDataParser {
	
	
	String dir = "./resources/";
	//String[] files = new String[]{"neuron_connect.csv", "neuron_fixed_points.csv", "neuron_type.csv", "neural_entities_descriptions"};
	String[] files = new String[]{"n2uconnectivity.csv", "jseconnectivity.csv"};
	String split_by = ",";
	String line = "";
	NeuralDatabaseManager neuro_db;
	boolean wipe_db = true;
	BufferedReader buff;
	
	public NeuralDataParser(){
		neuro_db = new NeuralDatabaseManager(wipe_db);
	}

	public void parseAndInsertN2U(){
		try {
			buff = new BufferedReader(new FileReader(dir+files[0]));
			int count = 0;
			while((line = buff.readLine()) != null){
				if (count <= 0)
					continue; // skip first line
				String[] parsed = line.split(split_by);
				if (neuro_db.insert_n2u_record(parsed) == false){
					System.out.println("Error inserting N2U record...");
					System.exit(1);
				}
			}
			buff.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void parseAndInsertJSE(){
		try {
			buff = new BufferedReader(new FileReader(dir+files[1]));
			int count = 0;
			while((line = buff.readLine()) != null){
				if (count <= 0)
					continue; // skip first line
				String[] parsed = line.split(split_by);
				if (neuro_db.insert_jse_record(parsed) == false){ // note that jse's first column is an index colum.. we don't want that.
					System.out.println("Error inserting JSE record...");
					System.exit(1);
				}
			}
			buff.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args){
		NeuralDataParser ndp = new NeuralDataParser();
		ndp.parseAndInsertN2U();
		ndp.parseAndInsertJSE();
		System.out.println("great success!");
	}
	
}
