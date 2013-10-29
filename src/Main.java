
import fu.geo.LatLongPosition;
import fu.geo.Spherical;

public class Main {

	public static void main(String[] args) {
		
		
		// DEBUG test generating start box
		double[] coords = getAreaBox(49.458167, 11.10222, 10, 120);
		for(double i:coords) {
			System.out.println(i);
		}

	}
	
	
	/**
	 * Calculates the bounding edge points for an area around a point
	 * 
	 * @param lat	latitude
	 * @param lon	longitude
	 * @param time	time in minutes
	 * @param speed	speed in kmh
	 * @return	lat1, lon1, lat2, lon2
	 */
	public static double[] getAreaBox(double lat, double lon, int time, int speed) {
		double meters = (speed / 1000.0) * (time*60);
		double[] coords = new double[4]; // lat1, lon1, lat2, lon2
		coords[0] = fu.geo.Spherical.latitudeNorthOf(lat, lon, meters);
		coords[1] = fu.geo.Spherical.longitudeEastOf(lat, lon, -meters);
		coords[2] = fu.geo.Spherical.latitudeNorthOf(lat, lon, -meters);
		coords[3] = fu.geo.Spherical.longitudeEastOf(lat, lon, meters);
		return coords;
	}

}
