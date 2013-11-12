
import fu.geo.LatLongPosition;
import fu.keys.LSIClassCentreDB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    private String host, user, password, dbName;
    private int port;
    private Connection connection = null;

    public Database(String host, int port, String user, String password, String dbName) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.dbName = dbName;
    }

    public void openConnection() {
        try {
            // Zugang zur Datenbank einrichten 
            String connectionString = "jdbc:postgresql://" + this.host + ":" + this.port + "/" + this.dbName;
            this.connection = DriverManager.getConnection(connectionString, this.user, this.password);
            this.connection.setAutoCommit(false);  //Getting results based on a cursor
            LSIClassCentreDB.initFromDB(this.connection);
        } catch (Exception e) {
            System.out.println("Error initialising DB access: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            System.out.println("Error closing DB: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private ResultSet query(String queryString) {
        try {
            Statement statement = connection.createStatement();
            statement.setFetchSize(1000);
            return statement.executeQuery(queryString);
        } catch (SQLException e) {
            System.out.println("Error query DB: " + e.toString());
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    public Map<Integer, Crossing> getCrossings(AreaBox box) {
        String queryString;
        double smallLong;
        double bigLong;
        double smallLat;
        double bigLat;
        ResultSet resultSet = null;
        HashMap<Integer, Crossing> crossings;
        crossings = new HashMap<>();

        LatLongPosition smallValues = box.getPosSmallValues();
        LatLongPosition largeValues = box.getPosLargeValues();
        queryString = String.format("SELECT id,long,lat FROM crossing WHERE"
                + " long BETWEEN %f AND %f AND lat BETWEEN %f AND %f",
                smallValues.getLongitude(), largeValues.getLongitude(), smallValues.getLatitude(), largeValues.getLatitude());

        resultSet = this.query(queryString);
        try {
            while (resultSet.next()) {
                int db_id = (int) resultSet.getLong(1);
                double db_lat = resultSet.getDouble(3);
                double db_long = resultSet.getDouble(2);
                crossings.put(new Integer(db_id), new Crossing(db_lat, db_long));
            }

            resultSet.close();
        } catch (SQLException e) {
            System.out.println("Error query DB: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
        
        return crossings;
    }
}
