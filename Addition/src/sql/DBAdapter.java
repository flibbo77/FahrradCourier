package sql;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import util.Fahrer;
import util.Order;

public class DBAdapter {

    private Statement SQLStatement;
    private Connection dbConnection;

    private int xSize, ySize;
    private int numOfOrders;

    private ArrayList<String> orders;
    private ArrayList<String> fahrer;
    private ArrayList<Fahrer> fahrerList;
    private ArrayList<Order> ordersList;

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

    /*private String getSingleString(String col) {
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
     }*/
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

    public ArrayList<String> getAllFahrerAsStringList() {
        updateFahrerList();
        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < fahrerList.size(); i++) {
            temp.add(fahrerList.get(i).getFahrerAsString());
        }
        return temp;
    }

    public ArrayList<String> getOrders() {
        updateOrdersList();
        ArrayList<String> temp = new ArrayList<String>();
        for (int i = 0; i < ordersList.size(); i++) {
            temp.add(ordersList.get(i).getOrderAsString());
        }
        numOfOrders = temp.size();
        return temp;
    }

    private String getOrderById(int aNr) {
        aNr += 1;
        String order = "";
        for (int i = 0; i < ordersList.size(); i++) {
            if (ordersList.get(i).getANr() == aNr) {
                order = ordersList.get(i).getOrderAsString();
            }
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

    public String findClosestOrder(int fahrerComboIndexOfSelected) {
        String closestOrder = "";

        int fahrerX = fahrerList.get(fahrerComboIndexOfSelected).getX();
        int fahrerY = fahrerList.get(fahrerComboIndexOfSelected).getY();

        System.out.println("x: " + fahrerX);
        System.out.println("y: " + fahrerY);

        int lowestDist = 100;
        int indexOfLowest = -1;
        for (int i = 0; i < numOfOrders; i++) {
            int x = ordersList.get(i).getStartX();
            int y = ordersList.get(i).getStartY();
            int dist = Math.abs(fahrerX - x) + Math.abs(fahrerY - y);
            if (dist < lowestDist) {
                lowestDist = dist;
                indexOfLowest = i;
            }
            System.out.println("x: " + x + " , y: " + y + " , dist: " + dist);

        }
        System.out.println("lowest dist: " + lowestDist + " , index: " + indexOfLowest);
        setStatus(indexOfLowest);
        setDriver(fahrerComboIndexOfSelected, indexOfLowest);
        updateOrdersList();
        updateFahrerList();
        closestOrder = getOrderById(indexOfLowest);

        return closestOrder;
    }

    private void setDriver(int index, int aNr) {
        index += 1;
        aNr += 1;
        SQLCommand = "update Auftrag "
                + "set PNR = " + index + " "
                + "where ANR = " + aNr;
        performInsertOperation();
        //setOrderStatusInFahrerList(index, aNr);

        System.out.println("update performed");
    }

    private void setStatus(int aNr) {
        aNr += 1;
        SQLCommand = "UPDATE Auftrag "
                + "SET STATUS = \"in Bearbeitung\" "
                + "WHERE ANR = " + aNr;
        performInsertOperation();
        System.out.println("update performed");
    }

    public String getActualOrderForDriver(int driverIndex) {
        int orderNum = -1;
        for (int i = 0; i < fahrerList.size(); i++) {
            if (fahrerList.get(i).getPNR() == driverIndex + 1) {
                orderNum = fahrerList.get(i).getOrder();
            }
        }
        if(orderNum > 0){
            for(int j = 0; j < ordersList.size(); j++){
                if(ordersList.get(j).getANr() == orderNum){
                    return ordersList.get(j).getOrderAsString();
                }
            }
        }
        return "kein Auftrag zugeteilt";
    }

    public int fahrerOrderNum(int driverIndex) {
        for (int i = 0; i < fahrerList.size(); i++) {
            if (fahrerList.get(i).getPNR() == driverIndex + 1) {
                return fahrerList.get(i).getOrder();
            }
        }
        return -2;
    }

    private void updateOrdersList() {
        ordersList = new ArrayList<Order>();
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
                Order temp = new Order(Results.getInt("ANR"), Results.getInt("PNR"), Results.getInt("StartX"), Results.getInt("StartY"),
                        Results.getInt("Zielx"), Results.getInt("ZielY"), Results.getString("STATUS"));
                ordersList.add(temp);
                System.out.println(temp.getOrderAsString());
            }
            dbConnection.close();
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("Exception during update ordersList");
        }
    }

    private void updateFahrerList() {
        fahrerList = new ArrayList<Fahrer>();
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
                System.out.println(temp.getFahrerAsString());
            }
            dbConnection.close();
            setOrderStatusInFahrerList();
        } catch (Exception e) {
            System.out.println("hello_3");
            System.out.println(e);
        }
    }

    private void setOrderStatusInFahrerList() {
        for (int i = 0; i < ordersList.size(); i++) {
            if (ordersList.get(i).getStatus().equals("in Bearbeitung")) {
                int tempANr = ordersList.get(i).getANr();
                int tempPNR = ordersList.get(i).getPNr();
                for (int j = 0; j < fahrerList.size(); j++) {
                    if (fahrerList.get(j).getPNR() == tempPNR) {
                        fahrerList.get(j).setOrder(tempANr);
                    }
                }
            }
        }
    }

}
