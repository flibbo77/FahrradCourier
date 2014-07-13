package sql;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import util.Fahrer;
import util.FahrerList;
import util.Order;
import util.OrdersList;
import util.Strings;

public class DBAdapter {

    private Statement SQLStatement;
    private Connection dbConnection;

    private int xSize, ySize;
    private int numOfOrders;

    private FahrerList fahrerList;
    private OrdersList ordersList;

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
        updateFahrerList();
        updateOrdersList();
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
            System.out.println("Error makeing Connection");
            System.out.println(e);
        }
    }

    private void performInsertOperation() {
        try {
            makeConnection();
            SQLStatement.executeUpdate(SQLCommand);
            dbConnection.close();
        } catch (Exception e) {
            System.out.println("error during insert operation");
            System.out.println(e);
        }
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
            System.out.println("error during get last id");
            System.out.println(e);
        }
        return result;
    }

    public ArrayList<String> getAllFahrerAsStringList() {
        updateFahrerList();
        return fahrerList.getAllFahrerAsStringList();
    }

    public ArrayList<String> getAllOrdersAsStringList() {
        updateOrdersList();
        ArrayList<String> temp = ordersList.getAllOrdersAsStringList();
        numOfOrders = temp.size();
        return temp;
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
        updateOrdersList();
    }

    public void updateFahrer(String fahrerNewFName, String fahrerNewLName, int index) {
        if (index == -1) {
            createNewFahrer(fahrerNewFName, fahrerNewLName);
        } else {
            changeFahrer(fahrerNewFName, fahrerNewLName, index);
        }
        updateFahrerList();
    }

    private void changeFahrer(String fahrerNewFName, String fahrerNewLName, int index) {
        SQLCommand = "UPDATE Fahrer "
                + "SET VNAME = \"" + fahrerNewFName + "\", NNAME = \"" + fahrerNewLName + "\" WHERE PNR = " + (index + 1);
        performInsertOperation();
    }

    private void createNewFahrer(String fahrerNewFName, String fahrerNewLName) {
        int fahrerX = rand.nextInt(xSize);
        int fahrerY = rand.nextInt(ySize);

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

    }

    public String getFahrerVNameAt(int fahrerListIndexOfSelected) {
        return fahrerList.get(fahrerListIndexOfSelected).getVName();
    }

    public String getFahrerNNameAt(int fahrerListIndexOfSelected) {
        return fahrerList.get(fahrerListIndexOfSelected).getNName();
    }

    public boolean alreadyHasJob(int fahrerComboIndexOfSelected) {
        return fahrerList.get(fahrerComboIndexOfSelected).hasActualJob();
    }

    public String findClosestOrder(int fahrerComboIndexOfSelected) {
        String closestOrder = "";
        int fahrerPNr = fahrerComboIndexOfSelected + 1;

        int fahrerX = fahrerList.getFahrerByPNR(fahrerPNr).getX();
        int fahrerY = fahrerList.getFahrerByPNR(fahrerPNr).getY();

        System.out.println("Fahrer x: " + fahrerX);
        System.out.println("Fahrer y: " + fahrerY);

        ordersList.findClosestFromPosition(fahrerX, fahrerY);
        int aNrOfLowest = ordersList.getANrOfClosest();
        System.out.println("anr of closest: " + aNrOfLowest);
        if (aNrOfLowest > 0) {
            setOrderStatusInDB(aNrOfLowest, Strings.BUSY);
            setDriverForOrderInDB(fahrerPNr, aNrOfLowest);
            updateOrdersList();
            updateFahrerList();
            closestOrder = ordersList.getOrderByANR(aNrOfLowest).getOrderAsString();
        } else {
            closestOrder = Strings.NO_ORDER_IN_DB;
        }
        return closestOrder;
    }

    private void setDriverForOrderInDB(int pNr, int aNr) {

        SQLCommand = "update Auftrag "
                + "set PNR = " + pNr + " "
                + "where ANR = " + aNr;
        performInsertOperation();

        System.out.println("set driver in Auftrag db performed");
    }

    private void setOrderStatusInDB(int aNr, String status) {

        SQLCommand = "UPDATE Auftrag "
                + "SET STATUS = '" + status + "' "
                + "WHERE ANR = " + aNr;
        performInsertOperation();
        System.out.println(" status update in db performed");
    }

    public String getActualOrderForDriver(int driverIndex) {
        int orderNum = -1;
        orderNum = fahrerList.getFahrerByPNR(driverIndex + 1).getOrder();

        if (orderNum > 0) {
            return ordersList.getOrderByANR(orderNum).getOrderAsString();
        }
        return "kein Auftrag zugeteilt";
    }

    public int fahrerOrderNum(int driverIndex) {
        return fahrerList.getFahrerByPNR(driverIndex + 1).getOrder();
    }

    private void updateOrdersList() {
        ordersList = new OrdersList();
        SQLCommand = "select Auftrag.ANR, PNR, DATE, TIME, STATUS, StartX, StartY, ZielX,  ZielY "
                + "from Auftrag, "
                + "(select ANR, X as StartX, Y as StartY from Adresse, Abholadresse"
                + " where Adresse.ID = Abholadresse.ID) as StartAdr, "
                + "(select ANR, X as ZielX, Y as ZielY from Adresse as a2, Ziel "
                + "where a2.ID = Ziel.ID) as ZielAdr "
                + "where Auftrag.ANR = StartAdr.ANR "
                + "and Auftrag.ANR = ZielAdr.ANR";
        try {
            makeConnection();
            ResultSet Results = SQLStatement.executeQuery(SQLCommand);

            while (Results.next()) {
                Order temp = new Order(Results.getInt("ANR"), Results.getInt("PNR"), Results.getString("DATE"), Results.getString("TIME"),
                        Results.getInt("StartX"), Results.getInt("StartY"),
                        Results.getInt("Zielx"), Results.getInt("ZielY"), Results.getString("STATUS"));
                ordersList.add(temp);
            }
            System.out.println("update orders list");
            dbConnection.close();
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Exception during update ordersList");
        }
    }

    private void updateFahrerList() {
        fahrerList = new FahrerList();
        SQLCommand = "Select Fahrer.PNR, VNAME, NNAME, X, Y "
                + "from Fahrer, Adresse, Standort "
                + "where Fahrer.PNR = Standort.PNR "
                + "and Standort.ID = Adresse.ID";
        try {
            makeConnection();
            ResultSet Results = SQLStatement.executeQuery(SQLCommand);
            while (Results.next()) {

                Fahrer temp = new Fahrer(Results.getString("VNAME"), Results.getString("NNAME"),
                        Results.getInt("PNR"), Results.getInt("X"), Results.getInt("Y"));

                fahrerList.add(temp);
            }
            dbConnection.close();

        } catch (Exception e) {
            System.out.println("error during update fahrer list");
            System.out.println(e);
        }
        if (ordersList != null) {
            setOrderStatusInFahrerList();
        }
    }

    private void setOrderStatusInFahrerList() {
        for (int i = 0; i < ordersList.size(); i++) {
            if (ordersList.size() > 0) {
                if (ordersList.get(i).getStatus().equals(Strings.BUSY)) {
                    int tempANr = ordersList.get(i).getANr();
                    int tempPNR = ordersList.get(i).getPNr();
                    fahrerList.getFahrerByPNR(tempPNR).setOrder(tempANr);
                }
                if (ordersList.get(i).getStatus().equals(Strings.FINISHED)) {
                    int tempPNR = ordersList.get(i).getPNr();
                    fahrerList.getFahrerByPNR(tempPNR).setOrder(-1);
                }
            }
        }
    }

    public void markOrderDone(int fahrerComboIndexOfSelected) {
        int pNr = fahrerComboIndexOfSelected + 1;
        Order order = ordersList.getOrderByANR(fahrerList.getFahrerByPNR(pNr).getOrder());
        int orderANr = order.getANr();
        int orderGoalX = order.getGoalX();
        int orderGoalY = order.getGoalY();
        setFahrerNewPosition(pNr, orderGoalX, orderGoalY);
        setOrderStatusInDB(orderANr, Strings.FINISHED);
        updateOrdersList();
        updateFahrerList();
    }

    private void setFahrerNewPosition(int pNr, int x, int y) {
        SQLCommand = "update Adresse set X = " + x + " ,Y = " + y + " "
                + "where ID = "
                + "(select ID  from Standort "
                + "where PNR = " + pNr + ")";
        performInsertOperation();
    }
}
