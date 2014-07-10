package sql;

import java.sql.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class DBAdapter {

    private Statement SQLStatement;
    private Connection dbConnection;

    private int xSize, ySize;
    private int numOfOrders;

    private ArrayList<String> orders;
    private ArrayList<String> fahrer;

    private Random rand;

    private String SQLCommand;

    DateFormat timeFormat;
    DateFormat dateFormat;

    private String actTime;
    private String actDate;

    Date date;

    public DBAdapter(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        rand = new Random();
        timeFormat = new SimpleDateFormat("HH:mm:ss");
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    private void setTimeDate() {
        date = new Date();
        actTime = timeFormat.format(date);
        actDate = dateFormat.format(date);
    }

    private void makeConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://132.199.139.24:3306/mmdb14_aponbhuiya?user=a.bhuiya&password=mmdb";
            dbConnection = DriverManager.getConnection(url);
            SQLStatement = dbConnection.createStatement();

        } catch (Exception e) {
            System.out.println("hello_1");
            System.out.println(e);
        }
    }

    private void performInsertOperation() {
        try {
            makeConnection();
            SQLStatement.executeUpdate(SQLCommand);
            dbConnection.close();
        } catch (Exception e) {
            System.out.println("hello_1");
            System.out.println(e);
        }
    }

    private String getSingleString(String col) {
        String result = "Fehler";
        try {
            makeConnection();
            SQLStatement.executeQuery(SQLCommand);
            ResultSet Results = SQLStatement.executeQuery(SQLCommand);
            while (Results.next()) {
                result = Results.getString(col);
                System.out.println(result);
            }
            dbConnection.close();
        } catch (Exception e) {
            System.out.println("hello_2");
            System.out.println(e);
        }
        return result;
    }

    private ArrayList<String> queryDBPutColsInStringList(String[] cols) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            makeConnection();
            ResultSet Results = SQLStatement.executeQuery(SQLCommand);
            while (Results.next()) {
                String str = "";
                for (int i = 0; i < cols.length; i++) {
                    str += Results.getString(cols[i]) + "   ";
                }
                result.add(str);
            }
            dbConnection.close();
        } catch (Exception e) {
            System.out.println("hello_3");
            System.out.println(e);
        }
        return result;
    }

    private int getLastId(String relation, String col) {
        SQLCommand = "select MAX( " + col + " ) as " + col + " from " + relation;
        int result = -1;

        try {
            makeConnection();
            ResultSet Results = SQLStatement.executeQuery(SQLCommand);
            while (Results.next()) {
                result = Results.getInt(col);
            }
            dbConnection.close();
        } catch (Exception e) {
            System.out.println("hello_4");
            System.out.println(e);
        }
        return result;
    }

    private int getIntFrom3Relations(String table1, String table1Col, String table2, String table3, String col, int index) {
        int val = -1;
        SQLCommand = "select * from " + table1 + "," + table2 + "," + table3
                + " where " + table1 + "." + table1Col + " = " + table2 + "." + table1Col
                + " and " + table2 + ".ID =" + table3 + ".ID "
                + " and " + table1 + "." + table1Col + " = " + (index + 1);
        try {
            makeConnection();
            ResultSet Results = SQLStatement.executeQuery(SQLCommand);
            while (Results.next()) {
                val = Results.getInt(col);
            }
            dbConnection.close();
        } catch (Exception e) {
            System.out.println("hello_4");
            System.out.println(e);
        }
        return val;
    }

    public ArrayList<String> getAllFahrerAsStringList() {
        SQLCommand = "select * from Fahrer";

        // ArrayList<String> temp = new ArrayList<String>();
        String[] cols = {"PNR", "VNAME", "NNAME"};
        //fahrer = queryDBPutColsInStringList(temp, cols);
        return queryDBPutColsInStringList(cols);
    }

    public ArrayList<String> getOrders() {
        SQLCommand = "select Auftrag.ANR, PNR, DATE, TIME, STATUS, StartX, StartY, ZielX,  ZielY "
                + "from Auftrag, "
                + "(select ANR, X as StartX, Y as StartY from Adresse, Abholadresse"
                + " where Adresse.ID = Abholadresse.ID) as StartAdr, "
                + "(select ANR, X as ZielX, Y as ZielY from Adresse as a2, Ziel "
                + "where a2.ID = Ziel.ID) as ZielAdr "
                + "where Auftrag.ANR = StartAdr.ANR "
                + "and Auftrag.ANR = ZielAdr.ANR";

        String[] cols = {"ANR", "PNR", "DATE", "TIME", "StartX", "StartY", "ZielX", "ZielY", "STATUS"};
        
        return queryDBPutColsInStringList(cols);
    }

    private String getOrderForShortestWay(int aNr) {
        aNr += 1;
        String order = "";

        SQLCommand = "select Auftrag.ANR, PNR, DATE, TIME, STATUS, StartX, StartY, ZielX,  ZielY "
                + "from Auftrag, "
                + "(select ANR, X as StartX, Y as StartY from Adresse, Abholadresse"
                + " where Adresse.ID = Abholadresse.ID) as StartAdr, "
                + "(select ANR, X as ZielX, Y as ZielY from Adresse as a2, Ziel "
                + "where a2.ID = Ziel.ID) as ZielAdr "
                + "where Auftrag.ANR = StartAdr.ANR "
                + "and Auftrag.ANR = ZielAdr.ANR "
                // + "and Auftrag.STATUS = \"nicht in Bearbeitung\" "
                + "and Auftrag.ANR = " + aNr;

        String[] cols = {"ANR", "DATE", "TIME", "StartX", "StartY", "ZielX", "ZielY"};
        orders = queryDBPutColsInStringList(cols);
        numOfOrders = orders.size();
        System.out.println("num of orders: " + numOfOrders);
        if (orders.size() > 0) {
            order = orders.get(0);
        } else {
            order = "Keine offenen Aufträge im System";
        }
        return order;
    }

    public void createOrder(int startX, int startY, int goalX, int goalY) {
        setTimeDate();
        SQLCommand = "insert into Auftrag (DATE, TIME, STATUS) "
                + "values ( \"" + actDate + "\" , \"" + actTime + "\" , \"nicht in Bearbeitung\")";
        performInsertOperation();
        int aNr = getLastId("Auftrag", "ANR");

        SQLCommand = "insert into Adresse (X, Y) "
                + "values ( " + startX + ", " + startY + ")";
        performInsertOperation();
        int startAdrId = getLastId("Adresse", "ID");

        SQLCommand = "insert into Adresse (X, Y) "
                + "values ( " + goalX + ", " + goalY + ")";
        performInsertOperation();
        int zielAdrId = getLastId("Adresse", "ID");

        SQLCommand = "insert into Abholadresse (ANR, ID) "
                + "values ( " + aNr + ", " + startAdrId + ")";
        performInsertOperation();

        SQLCommand = "insert into Ziel (ANR, ID) "
                + "values ( " + aNr + ", " + zielAdrId + ")";
        performInsertOperation();
    }

    public void updateFahrer(String fahrerNewFName, String fahrerNewLName, int index) {
        //index = 1;
        int fahrerX = rand.nextInt(xSize);
        int fahrerY = rand.nextInt(ySize);

        if (index == -1) {
            SQLCommand = "INSERT INTO Fahrer (VNAME, NNAME) "
                    + "VALUES ( \"" + fahrerNewFName + "\" , \"" + fahrerNewLName + "\" )";
            performInsertOperation();

            int fahrerID = getLastId("Fahrer", "PNR");

            SQLCommand = "INSERT INTO Adresse(X, Y) "
                    + "VALUES (\"" + fahrerX + "\" , \"" + fahrerY + "\")";
            performInsertOperation();

            int adressID = getLastId("Adresse", "ID");

            SQLCommand = "INSERT INTO Standort "
                    + "VALUES ( \"" + fahrerID + "\",\"" + adressID + "\")";
            performInsertOperation();

        } else {
            SQLCommand = "UPDATE Fahrer "
                    + "SET VNAME = \"" + fahrerNewFName + "\", NNAME = \"" + fahrerNewLName + "\" WHERE PNR = " + (index + 1);
            performInsertOperation();
        }
    }

    public String getFahrerVNameAt(int fahrerListIndexOfSelected) {
        System.out.println("getVName, index: " + fahrerListIndexOfSelected);
        SQLCommand = "select * "
                + "from Fahrer "
                + "where PNR = " + (fahrerListIndexOfSelected + 1);
        return getSingleString("VNAME");
    }

    public String getFahrerNNameAt(int fahrerListIndexOfSelected) {
        System.out.println("getNName , index: " + fahrerListIndexOfSelected);
        SQLCommand = "select * "
                + "from Fahrer "
                + "where PNR = " + (fahrerListIndexOfSelected + 1);
        return getSingleString("NNAME");
    }

    public String findClosestOrder(int fahrerComboIndexOfSelected) {
        String closestOrder = "";

        int fahrerX = getIntFrom3Relations("Fahrer", "PNR", "Standort", "Adresse", "X", fahrerComboIndexOfSelected);
        int fahrerY = getIntFrom3Relations("Fahrer", "PNR", "Standort", "Adresse", "Y", fahrerComboIndexOfSelected);

        System.out.println("x: " + fahrerX);
        System.out.println("y: " + fahrerY);

        int lowestDist = 100;
        int indexOfLowest = -1;
        for (int i = 0; i < numOfOrders; i++) {
            int x = getIntFrom3Relations("Auftrag", "ANR", "Abholadresse", "Adresse", "X", i);
            int y = getIntFrom3Relations("Auftrag", "ANR", "Abholadresse", "Adresse", "Y", i);
            int dist = Math.abs(fahrerX - x) + Math.abs(fahrerY - y);
            if (dist < lowestDist) {
                lowestDist = dist;
                indexOfLowest = i;
            }
            System.out.println("x: " + x + " , y: " + y + " , dist: " + dist);

        }
        System.out.println("lowest dist: " + lowestDist + " , index: " + indexOfLowest);
        closestOrder = getOrderForShortestWay(indexOfLowest);
        setStatus(indexOfLowest);
        return closestOrder;
    }

    private void setStatus(int aNr) {
        aNr += 1;
        SQLCommand = "UPDATE Auftrag "
                + "SET STATUS = \"in Bearbeitung\" "
                + "WHERE ANR = " + aNr;
        performInsertOperation();
        System.out.println("update performed");
    }

}
