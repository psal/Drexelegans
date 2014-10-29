package generic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	
	public static Date date = new Date();
	public static OutputStreamWriter writer;
	
	public Utils(){
		try {
			writer = new OutputStreamWriter(new FileOutputStream("motor_neuron_outputs.csv"), "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String timestamp(boolean human_readable){
		if (human_readable){
			return "("+(new Timestamp(date.getTime())).toString()+") -- ";
		}
		else{
			return String.valueOf(System.currentTimeMillis())+" -- ";
		}
	}
	
	/**
	 * returns true if a starts with b. Basically the same as String.contains(), 
	 * but this forces the substring to match starting the beginning of the string, via a regex.
	 * 
	 * case matters.
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean starts_with(String a, String b){
		Pattern starts_with = Pattern.compile("^"+b+".*");
		Matcher m = starts_with.matcher(a);
		return m.find();
	}
	
	public synchronized void write_file(long timestamp, String neuron_name){
		String line = timestamp + "," +neuron_name +"\n";
		try {
			writer.write(line);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void flush_writer(){
		try { 
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	public static void main (String[] args){
		
	}
	*/

}
