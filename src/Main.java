
import fu.geo.LatLongPosition;
import fu.geo.Spherical;
import fu.keys.LSIClassCentreDB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		
		
		// DEBUG test generating start box
		double[] coords = getAreaBox(49.458167, 11.10222, 5, 20);
		for(double i:coords) {
			System.out.println(i);
		}
        
        Main.getSomeCrossings();
        
	}
	
	
	/**
	 * Calculates the bounding edge points for an area around a point
	 * 
	 * @param lat	latitude
	 * @param lon	longitude
	 * @param time	time in minutes
	 * @param speed	speed in kmh
	 * @return latNothWest, lonNorthWest, latSouthEast, lonSouthEast
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
    
    public static void getSomeCrossings() {
        ArrayList<Crossing> crossings = new ArrayList<>();
        Connection connection=null;  
    
        String dbhost="geo.informatik.fh-nuernberg.de";
        int dbport=5432;
        String dbuser="dbuser";
        String dbpasswd="dbuser";
        String dbname="deproDB13";
        
        long time;
        int cnt;
        ResultSet resultSet;
        Statement statement;


        try {
            // Zugang zur Datenbank einrichten 
            String connectionString="jdbc:postgresql://"+dbhost+":"+dbport+"/"+dbname;
            connection=DriverManager.getConnection(connectionString,dbuser,dbpasswd);
            connection.setAutoCommit(false);  //Getting results based on a cursor
            LSIClassCentreDB.initFromDB(connection);
        }
        catch (Exception e) {
            System.out.println("Error initialising DB access: "+e.toString());
            e.printStackTrace();
            System.exit(1);
        }
        
        //get crossings near point
        try {
         statement=connection.createStatement();
            statement.setFetchSize(1000);
            resultSet = statement.executeQuery(
                "SELECT id,partnr,long,lat FROM crossing WHERE long BETWEEN 11.102203397098235 AND 11.103336602901767  AND lat BETWEEN 49.458156208142064 AND 49.46827779185794 ORDER BY id,partnr");

            cnt=0;
 
            while (resultSet.next()) {
                
                int db_id=(int)resultSet.getLong(1);
                int db_partnr=(int)resultSet.getLong(2);
                crossings.add(new Crossing(resultSet.getDouble(4), resultSet.getDouble(3)));
                //double db_long=resultSet.getDouble(3);
                //double db_lat=resultSet.getDouble(4);
                //System.out.println(""+db_id+" "+db_partnr+" "+db_long+" "+db_lat);
                cnt++;
            }
            resultSet.close();
        }
        catch (Exception e){
            System.out.println("Error sql query: "+e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
    