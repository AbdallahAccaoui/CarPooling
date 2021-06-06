import java.sql.*;
public class DatabaseConnection {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
     String connectionURL="jdbc:sqlserver://localhost:1433;"+
		          "databaseName=CarPooling;IntegratedSecurity=true;";
     Connection con=null;
     Statement stmt=null;
     ResultSet rs = null;
     try{
    	 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    	 con=DriverManager.getConnection(connectionURL);
    	 System.out.println("Connection Established");
    	 
    	 String SQL= "UPDATE Drivers SET PosxD = 15, PosyD = 14, passengers = 10 where DriverID = 500";
    	 stmt=con.createStatement();
    	  stmt.executeUpdate(SQL);
    	 
    	 
     }catch(Exception e){
    	 e.printStackTrace();
     }
	}

}
