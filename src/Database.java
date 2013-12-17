
import fu.keys.LSIClassCentreDB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

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
            statement.setFetchSize(5000);
            
            return statement.executeQuery(queryString);
        } catch (SQLException e) {
            System.out.println("Error query DB: " + e.toString());
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    public Map<Long, Crossing> getCrossings(AreaBox box) {
        String queryString;
        ResultSet resultSet = null;
        HashMap<Long, Crossing> crossings;
        crossings = new HashMap<Long, Crossing>();

        queryString = String.format("SELECT id,long,lat FROM crossing WHERE"
                + box.getSqlBetweenStatement("long", "lat"));

        resultSet = this.query(queryString);
        try {
            while (resultSet.next()) {
                long db_id = resultSet.getLong(1);
                double db_lat = resultSet.getDouble(3);
                double db_long = resultSet.getDouble(2);
                crossings.put(db_id, new Crossing(db_lat, db_long));
            }

            resultSet.close();
        } catch (SQLException e) {
            System.out.println("Error query DB: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }

        return crossings;
    }

    public Map<Long, Link> getLinks(AreaBox box) {
        String queryString;
        ResultSet resultSet = null;
        HashMap<Long, Link> links;
        links = new HashMap<Long, Link>();

        queryString = String.format("SELECT id, crossing_id_from, crossing_id_to, meters, lsiclass, tag, maxspeed, long_from, lat_from FROM link WHERE"
                + box.getSqlBetweenStatement("long_from", "lat_from"));

        resultSet = this.query(queryString);
        try {
            while (resultSet.next()) {
                long db_id = resultSet.getLong(1);
                long db_crossingFrom = resultSet.getLong(2);
                long db_crossingTo = resultSet.getLong(3);
                int db_meters = resultSet.getInt(4);
                int db_lsiClass = resultSet.getInt(5);
                String db_tag = resultSet.getString(6);
                int db_maxspeed = resultSet.getInt(7);
                // keine Einbahnstrassen und nur Autostrassen hinzufuegen
                if (!db_tag.equalsIgnoreCase("C") && (db_lsiClass / 100000 == 341)) {
                    links.put(db_id, new Link(db_crossingFrom, db_crossingTo, db_meters, db_lsiClass, db_maxspeed));
                }
            }

            resultSet.close();
        } catch (SQLException e) {
            System.out.println("Error query DB: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }

        return links;
    }
}
