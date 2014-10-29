package generic;
import java.io.*;
import java.text.*;
import java.util.*;


/**
 *	Logger taken from Ariel Stolerman's JStylo, very slightly tweaked
 * @author Ariel Stolerman
 * @author Andrew W.E. McDonald
 *
 */
public class Log {
	
	public static final boolean loggerFlag = true;
	public static boolean logFile = false;
	
	// time
	private static SimpleDateFormat tf = new SimpleDateFormat("HH-mm-ss");
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	private static Calendar cal = null;
	
	// file
	private static String fileDirPath = "log";
	private static String filePrefix = "c_elegans_v1";
	private static String out;
	private static BufferedWriter bw = null;
	
	/**
	 * @return
	 * 		The current time.
	 */
	private static String time() {
		cal = Calendar.getInstance();
		return tf.format(cal.getTime());
	}
	
	/**
	 * Returns the current date.
	 * @return
	 * 		The current date.
	 */
	private static String date() {
		cal = Calendar.getInstance();
		return df.format(cal.getTime());
	}

	private static void initLogFile() {
		if (loggerFlag && logFile) {
			out = fileDirPath+"/"+filePrefix+"_"+date()+"_"+time()+".txt";
			String msg = "Started log "+out+"\n===================================================\n";
			try {
				if (logFile) {
					bw = new BufferedWriter(new FileWriter(out));
					bw.write(msg);
				}
			} catch (IOException e) {
				System.err.println("Failed opening log file!");
				System.exit(0);
			}
			System.out.println(msg);
		}
	}
	
	/**
	 * Enumerator for logger output.
	 */
	private enum Type {
		OUT,
		ERR
	}

	private static void log(String msg, Type target) {
		if (loggerFlag) {
			if (bw == null)	initLogFile();
			String timedMsg = time()+": "+msg;

			// write to logger
			switch (target) {
			case OUT:
				System.out.print(timedMsg);	
				break;
			case ERR:
				System.err.print(timedMsg);
				break;
			}

			// write to file
			try {
				if (logFile) {
					bw.write(timedMsg);
					bw.flush();
				}
			} catch (IOException e) {
				System.err.println("Failed writing to log file!");
			}
		}
	}
	
	public static void out(String msg) {
		if (loggerFlag) {
			log(msg,Type.OUT);
			System.out.println();	
			// write to file
			try {
				if (logFile) {
					bw.write("\n");
					bw.flush();
				}
			} catch (IOException e) {
				System.err.println("Failed writing to log file!");
			}
		}
	}
	
	public static void err(String msg) {
		if (loggerFlag) {
			log(msg,Type.ERR);
			System.err.println();	
			// write to file
			try {
				if (logFile) {
					bw.write("\n");
					bw.flush();
				}
			} catch (IOException e) {
				System.err.println("Failed writing to log file!");
			}
		}
	}

	public static void close() {
		if (loggerFlag) {
			try {
				bw.close();
			} catch (IOException e) {
				System.err.println("Failed closing log file!");
			}
		}
	}

	public static String getFileDirPath() {
		return fileDirPath;
	}

	public static void setFileDirPath(String fileDirPath) {
		Log.fileDirPath = fileDirPath;
	}

	public static String getFilePrefix() {
		return filePrefix;
	}

	public static void setFilePrefix(String filePrefix) {
		Log.filePrefix = filePrefix;
	}
	
}