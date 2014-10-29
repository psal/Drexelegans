package generic;

/**
 * Stores and returns a 3 dimensional coordinate/location vector
 * @author Andrew W.E. McDonald
 *
 */
public class Location {
	
	double x;
	double y;
	double z;
	
	/**
	 * Location constructor
	 * @param x
	 * @param y
	 * @param z
	 */
	public Location (double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * @return
	 * double array, [x,y,z]
	 */
	public double[] get_location(){
		return new double[]{x,y,z};
	}
	
	public double get_x(){
		return x;
	}
	
	public double get_y(){
		return y;
	}
	
	public double get_z(){
		return z;
	}
}
