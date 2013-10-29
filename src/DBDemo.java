
import gao.tools.SQL;

import java.util.ArrayList;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.Point;

import fu.keys.LSIClassCentreDB;
import fu.util.DoubleMetaphone;
import fu.util.ConcaveHullGenerator;


public class DBDemo {

    private static GeometryFactory geomfact=new GeometryFactory();


    public static void main(String args[]) {
        Connection connection=null;  

        String dbhost="localhost";
        int dbport=5432;
        String dbuser="dbuser";
        String dbpasswd="dbuser";
        String dbname="???";

        long time;
        int cnt;
        ResultSet resultSet;
        Statement statement;


        if (args.length==2) {
            dbhost=args[0];
            dbname=args[1];
        }


        try {
            /* Zugang zur Datenbank einrichten */
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

        try {


// ************* DEMO-ABFRAGE 1: Domain-Tabelle (alle Objekt in einem Dreieck) *************
// Ueber SQL.createIndexQuery wird eine SQL-Bedingung generiert (stellt aber ein Rechteck - die Bounding Box dar)
// Spaeter werden die Resultate anhand der exakten Dreiecks-Geomtrie ueberprueft: geom.within(triangle)

            // KONSTRUKTION EINER VERGLEICHSGEOMETRIE

            Coordinate[] coords=new Coordinate[4];
            coords[0]=new Coordinate(11.097026,49.460811);
            coords[1]=new Coordinate(11.104676,49.460811);
            coords[2]=new Coordinate(11.101730,49.455367);
            coords[3]=coords[0];
            Geometry triangle=geomfact.createPolygon(geomfact.createLinearRing(coords),new LinearRing[0]);

            Envelope boundingBox=triangle.getEnvelopeInternal(); // Bounding Box berechnen

            System.out.println("Abfrage: Alle Objekte Strassen im Bereich des angegebenen Dreiecks (Naehe Informatik-Gebaeude)");
            
            int[] lcStrassen=LSIClassCentreDB.lsiClassRange("STRASSEN_WEGE");
            
            time=System.currentTimeMillis(); // Zeitmessung beginnen

            statement=connection.createStatement();
            statement.setFetchSize(1000);
            resultSet = statement.executeQuery("SELECT realname, geodata_line FROM domain WHERE (geometry='L' OR geometry='C') AND lsiclass1 BETWEEN "+lcStrassen[0]+" AND "+lcStrassen[1]+" AND"+
                                               SQL.createIndexQuery(boundingBox.getMinX(),boundingBox.getMaxY(),boundingBox.getMaxX(),boundingBox.getMinY(),SQL.COMPLETELY_INSIDE)
                                              );

            cnt=0;
 
            while (resultSet.next()) {
                String realname=resultSet.getString(1);
                byte[] geodata_line=resultSet.getBytes(2);
                Geometry geom=SQL.wkb2Geometry(geodata_line);

                if (geom.within(triangle)) {                       // Exact geometrisch testen, ob die Geometry im Dreieck liegt
                    System.out.println(realname);
                    dumpGeometry(geom);
                    cnt++;
                 }
                 else
                     System.out.println(realname+" ist nicht exakt in der gesuchten Geometry");
            }
            resultSet.close();
            System.out.println("Anzahl der Resultate: "+cnt);
            System.out.println("Zeit "+(System.currentTimeMillis()-time)/1000+" s");
            System.out.println("Ende Abfrage");
            System.out.println("=====================================================");


// ************* DEMO-ABFRAGE 2: Domain-Tabelle  (alle Objekt in einem Rechteck) *************
// Achtung: bei Abfragegeometrien die nicht rechteckig sind, muesste das Resultat spaeter noch genau ueberprueft werden
// Das ist in diesem Beispiel entfallen, da die Abfragegeometrie ein Rechteck ist.

            System.out.println("Abfrage: Domains mit Linien-Geometrie in einem Rechteck");
            time=System.currentTimeMillis(); // Zeitmessung beginnen

            statement=connection.createStatement();
            statement.setFetchSize(1000);
            resultSet = statement.executeQuery("SELECT realname, geodata_line FROM domain WHERE (geometry='L' OR geometry='C') AND "+
                                               SQL.createIndexQuery(11.10069333,49.45987833,11.10392667,49.45725500,SQL.COMPLETELY_INSIDE)
                                              );

            cnt=0;
 
            while (resultSet.next()) {
                String realname=resultSet.getString(1);
                byte[] geodata_line=resultSet.getBytes(2);
                Geometry geom=SQL.wkb2Geometry(geodata_line);
                System.out.println(realname);
                dumpGeometry(geom);
                cnt++;
            }
            resultSet.close();
            System.out.println("Anzahl der Resultate: "+cnt);
            System.out.println("Zeit "+(System.currentTimeMillis()-time)/1000+" s");
            System.out.println("Ende Abfrage");
            System.out.println("=====================================================");

// ************* DEMO-ABFRAGE 3: CROSSING-TABELLE *************
// Alle Kreuzungen innerhalb eines Rechtecks
// Das Kreuzungen keinen raeumlichen Index haben, kann die Rechteck-Bedingung direkt als SQL formuliert werden

            System.out.println("Abfrage: Topologie-Kreuzungspunkte im Rechteck");
            time=System.currentTimeMillis(); // Zeitmessung beginnen

            statement=connection.createStatement();
            statement.setFetchSize(1000);
            resultSet = statement.executeQuery("SELECT id,partnr,long,lat FROM crossing WHERE long<=11.10392667 AND long>=11.10069333 AND lat>=49.45725500 AND lat<=49.45987833 ORDER BY id,partnr");

            cnt=0;
 
            while (resultSet.next()) {
                int db_id=(int)resultSet.getLong(1);
                int db_partnr=(int)resultSet.getLong(2);
                double db_long=resultSet.getDouble(3);
                double db_lat=resultSet.getDouble(4);
                System.out.println(""+db_id+" "+db_partnr+" "+db_long+" "+db_lat);
                cnt++;
            }
            resultSet.close();
            System.out.println("Anzahl der Resultate: "+cnt);
            System.out.println("Zeit "+(System.currentTimeMillis()-time)/1000+" s");
            System.out.println("Ende Abfrage");
            System.out.println("=====================================================");

// ************* DEMO-ABFRAGE 4: LINK-TABELLE *************
// Streckenabschnitte, deren "Start" innerhalb eines Rechtecks liegen
// Das Streckenabschnitte keinen raeumlichen Index haben, kann die Rechteck-Bedingung direkt als SQL formuliert werden

            System.out.println("Abfrage: Topologie-Kanten im Rechteck");
            time=System.currentTimeMillis(); // Zeitmessung beginnen

            statement=connection.createStatement();
            statement.setFetchSize(1000);
            resultSet = statement.executeQuery("SELECT id,crossing_id_from,crossing_id_to,meters,lsiclass,tag,maxspeed,deadend,long_from,lat_from FROM link WHERE long_from<=11.10392667 AND long_from>=11.10069333 AND lat_from>=49.45725500 AND lat_from<=49.45987833 ORDER BY id");

            cnt=0;
 
            while (resultSet.next()) {
                int db_id=(int)resultSet.getLong(1);
                int db_crossing_id_from=(int)resultSet.getLong(2);
                int db_crossing_id_to=(int)resultSet.getLong(3);
                int db_meters=(int)resultSet.getLong(4);
                int db_lsiclass=(int)resultSet.getLong(5);
                String db_tag=resultSet.getString(6);
                int db_maxspeed=(int)resultSet.getLong(7);
                String db_deadend=resultSet.getString(8);


                String lsikeyStr="#"+db_lsiclass;

                try {                
                    lsikeyStr=LSIClassCentreDB.className(db_lsiclass);
                }
                catch (Exception e) {}  // LSIClass not found

                System.out.print(""+db_id+", ");
                System.out.print(db_meters+"m, ");
                if (db_maxspeed>0)
                    System.out.print("max. "+db_maxspeed+"km/h, ");
                if (db_tag.equals("C"))
                    System.out.print("oneway, ");
                if (db_deadend.equals("D"))
                    System.out.print("deadend, ");


                System.out.println(lsikeyStr);
                cnt++;
            }
            resultSet.close();
            System.out.println("Anzahl der Resultate: "+cnt);
            System.out.println("Zeit "+(System.currentTimeMillis()-time)/1000+" s");
            System.out.println("Ende Abfrage");
            System.out.println("=====================================================");



// ************* DEMO-ABFRAGE 5: LINK-TABELLE *************
// Wie ABFRAGE 4 mit zwei Erweiterungen
// 1. Es werden nur "befahrbare Strassen" geladen
// 2. Ueber die Domain-Tabelle wird per Join der Strassenname geladen

            System.out.println("Abfrage: Topologie-Kanten im Rechteck");
            time=System.currentTimeMillis(); // Zeitmessung beginnen

            int[] lcStrassenBefahrbar=LSIClassCentreDB.lsiClassRange("KRAFTFAHRZEUGSTRASSEN");

            statement=connection.createStatement();
            statement.setFetchSize(1000);
            resultSet = statement.executeQuery("SELECT L.id,L.crossing_id_from,L.crossing_id_to,L.meters,L.lsiclass,L.tag,L.maxspeed,L.deadend,D.realname,D.nametype,L.long_from,lat_from FROM link L, domain D WHERE long_from<=11.10392667 AND long_from>=11.10069333 AND lat_from>=49.45725500 AND lat_from<=49.45987833 AND lsiclass BETWEEN "+lcStrassenBefahrbar[0]+" AND "+lcStrassenBefahrbar[1]+" AND L.d_id=D.d_id ORDER BY id");

            cnt=0;
 
            while (resultSet.next()) {
                int db_id=(int)resultSet.getLong(1);
                int db_crossing_id_from=(int)resultSet.getLong(2);
                int db_crossing_id_to=(int)resultSet.getLong(3);
                int db_meters=(int)resultSet.getLong(4);
                int db_lsiclass=(int)resultSet.getLong(5);
                String db_tag=resultSet.getString(6);
                int db_maxspeed=(int)resultSet.getLong(7);
                String db_deadend=resultSet.getString(8);
                String db_realname=resultSet.getString(9);
                int db_nametype=(int)resultSet.getLong(10);

                String lsikeyStr="#"+db_lsiclass;

                try {                
                    lsikeyStr=LSIClassCentreDB.className(db_lsiclass);
                }
                catch (Exception e) {}  // LSIClass not found

                System.out.print(""+db_id+", ");
                System.out.print(db_meters+"m, ");
                if (db_maxspeed>0)
                    System.out.print("max. "+db_maxspeed+"km/h, ");
                if (db_tag.equals("C"))
                    System.out.print("oneway, ");
                if (db_deadend.equals("D"))
                    System.out.print("deadend, ");

                if (db_nametype<100)   // Nur nametype<100 sind echte Namen, sonst kuenstlich vergebene
                    System.out.print(db_realname+", ");

                System.out.println(lsikeyStr);
                cnt++;
            }
            resultSet.close();
            System.out.println("Anzahl der Resultate: "+cnt);
            System.out.println("Zeit "+(System.currentTimeMillis()-time)/1000+" s");
            System.out.println("Ende Abfrage");
            System.out.println("=====================================================");



// ************* DEMO-ABFRAGE 6: Domain-Tabelle nach aehnlichen Objektnamen fragen  *************
// Hiermit wird der Double-Metaphone getestet

            String searchString="Burgberg";  // Alle Domains fragen, die aehnlich klingen

            DoubleMetaphone dm=new DoubleMetaphone();
            dm.setMaxCodeLen(6);
            String dmPrimary=dm.doubleMetaphone(searchString,false);
            String dmAlternate=dm.doubleMetaphone(searchString,true);


            System.out.println("Abfrage: Domains in einem Rechteck, die so aehnlich heissen wie '"+searchString);
            time=System.currentTimeMillis(); // Zeitmessung beginnen

            statement=connection.createStatement();
            statement.setFetchSize(1000);
            resultSet = statement.executeQuery("SELECT realname FROM domain WHERE (dmetaphone_primary='"+dmPrimary+"' OR dmetaphone_alternate='"+dmAlternate+"') AND "+
                                               SQL.createIndexQuery(11.0,49.8,11.2,49.2,SQL.COMPLETELY_INSIDE)
                                              );

            cnt=0;
 
            while (resultSet.next()) {
                String realname=resultSet.getString(1);
                System.out.println(realname);
                cnt++;
            }
            resultSet.close();
            System.out.println("Anzahl der Resultate: "+cnt);
            System.out.println("Zeit "+(System.currentTimeMillis()-time)/1000+" s");
            System.out.println("Ende Abfrage");
            System.out.println("=====================================================");


// ************* DEMO-ABFRAGE 7: Concave Hull *************
// Wie erzeugt man eine Concave-Hull aus einer Punktwolke

            System.out.println("Abfrage: berechne ein Cancave Hull aus Demopunkten");


            double[][] demoPointsRaw={{1,1}, {1,10}, {20,10}, {5,5}, {7,7}, {18, 2},{12,7},{9,6},{10,2},{6,3},{1,5}};
            ArrayList<double[]> demoPoints=new ArrayList<double[]>();
            for (double[] point:demoPointsRaw)
                 demoPoints.add(point);

            ArrayList<double[]> concaveHull=ConcaveHullGenerator.concaveHull(demoPoints,1.0d);
            for (double[] hullPoint:concaveHull)
                System.out.println(hullPoint[0]+"/"+hullPoint[1]);



        }
        catch (Exception e) {
            System.out.println("Error processing DB queries: "+e.toString());
            e.printStackTrace();
            System.exit(1);
        }   
    }



    public static void dumpGeometry(Geometry geom) {
        if (geom instanceof Polygon) {
            System.out.println("Class: Polygon");
            LineString extring=((Polygon)geom).getExteriorRing();
            System.out.println(extring.getNumPoints()+" exterior ring points");
            int n=3;
            if (n>=extring.getNumPoints())
                n=extring.getNumPoints();
            System.out.println("First "+n+" poly points:");
            for (int i=0;i<n;i++) {
                 Coordinate coord=extring.getCoordinateN(i);
                 System.out.print(coord.x+","+coord.y+" ");
            }
            System.out.println("");
        }

        else if (geom instanceof MultiPolygon) {
            System.out.println("Class: MultiPolygon");
            System.out.println(geom.getNumGeometries()+" part geometries");
            Geometry firstgeom=geom.getGeometryN(0);
            LineString extring=((Polygon)firstgeom).getExteriorRing();
            System.out.println(extring.getNumPoints()+" exterior ring points (geometry 0)");
            int n=3;
            if (n>=extring.getNumPoints())
                n=extring.getNumPoints();
            System.out.println("First "+n+" poly points (geometry 0):");
            for (int i=0;i<n;i++) {
                 Coordinate coord=extring.getCoordinateN(i);
                 System.out.print(coord.x+","+coord.y+" ");
            }
            System.out.println("");

        }

        else if (geom instanceof LineString) {
            System.out.println("Class: LineString");
            LineString listring=((LineString)geom);
            System.out.println(listring.getNumPoints()+" line points");
            int n=3;
            if (n>=listring.getNumPoints())
                n=listring.getNumPoints();
            System.out.println("First "+n+" line points:");
            for (int i=0;i<n;i++) {
                 Coordinate coord=listring.getCoordinateN(i);
                 System.out.print(coord.x+","+coord.y+" ");
            }
            System.out.println("");
        }

        else if (geom instanceof MultiLineString) {
            System.out.println("Class: MultiLineString");
            System.out.println(geom.getNumGeometries()+" part geometries");
            Geometry firstgeom=geom.getGeometryN(0);
            LineString listring=((LineString)firstgeom);
            System.out.println(listring.getNumPoints()+" line points (geometry 0)");
            int n=3;
            if (n>=listring.getNumPoints())
                n=listring.getNumPoints();
            System.out.println("First "+n+" line points  (geometry 0):");
            for (int i=0;i<n;i++) {
                 Coordinate coord=listring.getCoordinateN(i);
                 System.out.print(coord.x+","+coord.y+" ");
            }
            System.out.println("");

        }

        else if (geom instanceof Point) {
            System.out.println("Class: Point");
            Coordinate coord=((Point)geom).getCoordinate();
            System.out.println(coord.x+","+coord.y);
        }
        else {
            System.out.println("don't know how to tell something about "+geom.getClass().getName());        
        }
    }

}