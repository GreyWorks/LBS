import fu.keys.LSIClassCentreDB;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public ResultSet query(String queryString) {
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

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            System.out.println("Error closing DB: " + e.toString());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
