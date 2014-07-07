package sql;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class DBAdapter {
    
    private int xSize, ySize;
    private ArrayList<String> orders;
    private ArrayList<String> fahrer;
    
    private Random rand;
    
    private String SQLCommand;
    

	public DBAdapter(int xSize, int ySize) {
        this.xSize = xSize;
        this.ySize = ySize;
        rand = new Random();
        }
        
        private void performInsertOperation(){
		
		try{

		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String url = "jdbc:mysql://132.199.139.24:3306/mmdb14_aponbhuiya?user=a.bhuiya&password=mmdb";
		Connection dbConnection = DriverManager.getConnection(url);
		Statement SQLStatement = dbConnection.createStatement();
                
                SQLStatement.executeUpdate(SQLCommand);
		dbConnection.close();
		} catch(Exception e){
			System.out.println("hello");
			System.out.println(e);
		}
	}
        
        private String getSingleString(String col){
		String result = "";
		try{

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
		} catch(Exception e){
			System.out.println("hello");
			System.out.println(e);
		}
                return result;
	}
        
        private ArrayList<String> queryFahrer(){
            
            ArrayList<String> result;
            result = new ArrayList<String>();
            
            try{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String url = "jdbc:mysql://132.199.139.24:3306/mmdb14_aponbhuiya?user=a.bhuiya&password=mmdb";
		Connection dbConnection = DriverManager.getConnection(url);
		Statement SQLStatement = dbConnection.createStatement();
                		
		ResultSet Results = SQLStatement.executeQuery(SQLCommand);

		while (Results.next()) {
			result.add(Results.getString("VNAME" ) + " " + Results.getString("NNAME"));
		}
		
		dbConnection.close();
		} catch(Exception e){
			System.out.println("hello");
			System.out.println(e);
		}
            return result;
        }
        
        private int getLastId(String relation, String col){
           SQLCommand = "select MAX( " + col + " ) as " + col + " from " + relation;
           int result = -1;
            
            try{
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String url = "jdbc:mysql://132.199.139.24:3306/mmdb14_aponbhuiya?user=a.bhuiya&password=mmdb";
		Connection dbConnection = DriverManager.getConnection(url);
		Statement SQLStatement = dbConnection.createStatement();
                		
		ResultSet Results = SQLStatement.executeQuery(SQLCommand);

		while (Results.next()) {
			result = Results.getInt(col);
		}
		
		dbConnection.close();
		} catch(Exception e){
			System.out.println("hello");
			System.out.println(e);
		}
            return result;
        }
        
        public ArrayList<String> getFahrer(){
            SQLCommand = "select * from Fahrer";
            fahrer = null;
            fahrer = queryFahrer();
            return fahrer;
        }
        
        public ArrayList<String> getOrders(){
            return orders;
        }
        
        public void createOrder(int startX, int startY, int goalX, int goalY){
            // TODO
        }

    public void updateFahrer(String fahrerNewFName, String fahrerNewLName, int index) {
        //index = 1;
        int fahrerX = rand.nextInt(xSize);
        int fahrerY = rand.nextInt(ySize);
       
        if(index == -1){
            SQLCommand = "INSERT INTO Fahrer (VNAME, NNAME) VALUES ( \"" + fahrerNewFName + "\" , \"" + fahrerNewLName + "\" )";
            performInsertOperation();
            
            int fahrerID = getLastId("Fahrer", "PNR");
      
            SQLCommand = "INSERT INTO Adresse(X, Y) VALUES (\"" + fahrerX + "\" , \"" + fahrerY + "\")";
            performInsertOperation();
            
            int adressID = getLastId("Adresse", "ID");
          
            SQLCommand = "INSERT INTO Standort VALUES ( \"" + fahrerID + "\",\"" + adressID + "\")";
            performInsertOperation();

        } else {
            SQLCommand = "UPDATE Fahrer SET VNAME = \""+fahrerNewFName+"\", NNAME = \"" + fahrerNewLName + "\" WHERE PNR = " + (index + 1);
            performInsertOperation();           
        }  
    }

    public String getFahrerVNameAt(int fahrerListIndexOfSelected) {
        System.out.println("getVName, index: " + fahrerListIndexOfSelected);
        SQLCommand = "select * from Fahrer where PNR = " + (fahrerListIndexOfSelected + 1);
        return getSingleString("VNAME");
    }

    public String getFahrerNNameAt(int fahrerListIndexOfSelected) {
        System.out.println("getNName , index: " + fahrerListIndexOfSelected);
        SQLCommand = "select * from Fahrer where PNR = " + (fahrerListIndexOfSelected + 1);
        return getSingleString("NNAME");    }
}


