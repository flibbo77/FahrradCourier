package sql;

import java.sql.*;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

public class DBAdapter {

    private int xSize, ySize;
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

    private void performInsertOperation() {

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://132.199.139.24:3306/mmdb14_aponbhuiya?user=a.bhuiya&password=mmdb";
            Connection dbConnection = DriverManager.getConnection(url);
            Statement SQLStatement = dbConnection.createStatement();

            SQLStatement.executeUpdate(SQLCommand);
            dbConnection.close();
        } catch (Exception e) {
            System.out.println("hello_1");
            System.out.println(e);
        }
    }

    private String getSingleString(String col) {
        String result = "";

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://132.199.139.24:3306/mmdb14_aponbhuiya?user=a.bhuiya&password=mmdb";
            Connection dbConnection = DriverManager.getConnection(url);
            Statement SQLStatement = dbConnection.createStatement();

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

    private ArrayList<String> queryDB(ArrayList<String> result, String[] cols) {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://132.199.139.24:3306/mmdb14_aponbhuiya?user=a.bhuiya&password=mmdb";
            Connection dbConnection = DriverManager.getConnection(url);
            Statement SQLStatement = dbConnection.createStatement();

            ResultSet Results = SQLStatement.executeQuery(SQLCommand);

            while (Results.next()) {
                System.out.println("hier");
                //result.add(Results.getString(cols[0]) + " " + Results.getString(cols[1]));
                String str = "";
                for(int i = 0; i < cols.length; i++){
                    str += Results.getString(cols[i]) + "   ";
                }
                result.add(str);
            }

            dbConnection.close();
        } catch (Exception e) {
            System.out.println("hello_3");
            System.out.println(e);
            System.out.println(SQLCommand);
        }
        return result;
    }

    private int getLastId(String relation, String col) {
        SQLCommand = "select MA"
                + "X( " + col + " ) as " + col + " from " + relation;
        int result = -1;

        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            String url = "jdbc:mysql://132.199.139.24:3306/mmdb14_aponbhuiya?user=a.bhuiya&password=mmdb";
            Connection dbConnection = DriverManager.getConnection(url);
            Statement SQLStatement = dbConnection.createStatement();

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

    public ArrayList<String> getFahrer() {
        SQLCommand = "select * from Fahrer";
        ArrayList<String> temp = new ArrayList<String>();
        String[] cols = {"VNAME", "NNAME"};
        fahrer = queryDB(temp, cols);
        return fahrer;
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
                    
        
        ArrayList<String> temp = new ArrayList<String>();
        String[] cols = {"ANR", "PNR", "DATE", "TIME", "StartX", "StartY", "ZielX", "ZielY", "STATUS"};
        orders = queryDB(temp, cols);
        System.out.println("orders: " + orders);
        return orders;
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
}
