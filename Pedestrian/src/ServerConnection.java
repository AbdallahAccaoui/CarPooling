import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.*;




public class ServerConnection extends Thread {
	   Socket socket;
	   Server server;
	   DataInputStream din;
	   DataOutputStream dout;
	   boolean shouldRun=true;
	   int numberOfDrivers=0;
	   int numberOfPedestrian=0;
	   
	   static ConcurrentHashMap<Integer,ServerConnection> map = new ConcurrentHashMap<>();
	   String connectionURL="jdbc:sqlserver://localhost:1433;"+
		          "databaseName=CarPooling;IntegratedSecurity=true;";
	   
	   
	 public synchronized void addToMap(int ID, ServerConnection s){
		 map.put(ID, s);
		 
	 }
	   
	 public synchronized ServerConnection getFromMap(int ID){
		 ServerConnection s =map.get(ID);
		 return s;
	 } 
	   
	   //Constructor class 
     
	   public ServerConnection(Socket socket, Server server){
    	 super("ServerConnectionThread");
    	 this.socket= socket;
    	 this.server=server;
     }
     //sending to the client
     public void sendStringToClient(String text){
    	 try {
			dout.writeUTF(text);
			dout.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	 
    	 
     }
     //if we have multiple clients
     public void sendStringToAllClients(String text){
    	 for(int index=0; index<server.connections.size();index++){
    		 ServerConnection sc=server.connections.get(index);
    		 sc.sendStringToClient(text);
    	 }
    	 
     }
     
     
     // run
     public void run(){
    	 try {
			din = new DataInputStream(socket.getInputStream());
			dout= new DataOutputStream(socket.getOutputStream());
			 while(shouldRun){
			   while(din.available()==0){
				   try {
					Thread.sleep(1);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			  }
		  //Read from the input Stream (from the client)	   
	      String textIn= din.readUTF();
	      //Process the string (Is it from the client or the driver?)
	      //sendStringToAllClients();
	      this.sendStringToClient("I received your command");
	      ProcessInFromClients(textIn);
	      
		  
		   
		   }
		   din.close();
		   dout.close();
		   socket.close();
    	 } catch (IOException e) {
			e.printStackTrace();
		}
    	 	
     }
     public void ProcessInFromClients(String textIn){
    	 String command= textIn.substring(0,textIn.indexOf(" "));
    	 switch(command){
    	 case "CAR_ORDER":
    		 int PedestrianID= Integer.parseInt(StringUtils.substringBetween(textIn, ",0", ";0").trim());
    		 double PosXP = Double.parseDouble(StringUtils.substringBetween(textIn, ",1", ";1").trim());
             double PosYP = Double.parseDouble(StringUtils.substringBetween(textIn, ",2", ";2").trim());
             double DestXP = Double.parseDouble(StringUtils.substringBetween(textIn, ",3", ";3").trim());
             double DestYP = Double.parseDouble(StringUtils.substringBetween(textIn, ",4", ";4").trim());
             String NoSmokeP="";
             String NoPetsP="";
             String NoKidsP="";
             String QuietRideP="";
             if (textIn.contains("NS")) {
                  NoSmokeP = "NS";  
             }
             if(textIn.contains("NP")) {
                 NoPetsP = "NP";
             }
             if (textIn.contains("NK")) {
                  NoKidsP = "NK";  
                 }
             if(textIn.contains("QR")) {
                  QuietRideP= "QR";
                 }
             String PreferencesP=NoSmokeP+" "+ NoPetsP+" "+NoKidsP+" "+QuietRideP;
             
             this.addToMap(PedestrianID, this);
             System.out.println(map.get(PedestrianID).toString());
             storePedestrianData(PedestrianID,  PosXP,  PosYP,  DestXP, DestYP,  PreferencesP );
             
           break;
    	 case "DRIVER":
    		 int DriverID = Integer.parseInt(StringUtils.substringBetween(textIn, ",0", ";0").trim());
    		 double PosxD = Double.parseDouble(StringUtils.substringBetween(textIn, ",1", ";1").trim());
             double PosyD = Double.parseDouble(StringUtils.substringBetween(textIn, ",2", ";2").trim());
             double DestxD = Double.parseDouble(StringUtils.substringBetween(textIn, ",3", ";3").trim());
             double DestyD = Double.parseDouble(StringUtils.substringBetween(textIn, ",4", ";4").trim());
             int passengers = Integer.parseInt(StringUtils.substringBetween(textIn, ",5", ";5").trim()) ;
             int capacity = Integer.parseInt(StringUtils.substringBetween(textIn, ",6", ";6").trim());
             double chargePerKilo = Double.parseDouble(StringUtils.substringBetween(textIn, ",7", ";7").trim());
             String NoSmokeD="";
             String NoPetsD="";
             String NokidsD="";
             String QuietRideD="";
             if (textIn.contains("NS")) {
                 NoSmokeD = "NS";  
             }
             if(textIn.contains("NP")) {
                 NoPetsD= "NP";
             }
             if (textIn.contains("NK")) {
                  NokidsD = "NK";  
                 }
             if(textIn.contains("QR")) {
                 QuietRideD= "QR";
                 }
             String PreferencesD = NoSmokeD+" "+NoPetsD+" "+NokidsD +" "+QuietRideD;
             this.addToMap(DriverID, this);
             storeDriverData(DriverID, PosxD, PosyD, DestxD, DestyD, passengers, capacity, chargePerKilo, PreferencesD);
             comparePositionstoPedestrian(DriverID, PosxD, PosyD, DestxD, DestyD, passengers, capacity, chargePerKilo, PreferencesD);
             //After comparing and sending text to Pedestrians we wait for his choice.
             break;
    	 case "UPDATE":
    		 int DriverIDU = Integer.parseInt(StringUtils.substringBetween(textIn, ",0", ";0").trim());
    		 double PosxDU = Double.parseDouble(StringUtils.substringBetween(textIn, ",1", ";1").trim());
             double PosyDU = Double.parseDouble(StringUtils.substringBetween(textIn, ",2", ";2").trim());
             int passengersU = Integer.parseInt(StringUtils.substringBetween(textIn, ",3", ";3").trim()) ;
             updateDriverPosition(DriverIDU, PosxDU, PosyDU, passengersU);
             break;
    	 case "ACCEPT":
    		 System.out.println("RECEIVED ACCEPT");
    		 int DriverIDA = Integer.parseInt(StringUtils.substringBetween(textIn, ",0", ";0").trim());
    		 int PedestrianIDA=Integer.parseInt(StringUtils.substringBetween(textIn, ",1", ";1").trim());
             int passengersA = Integer.parseInt(StringUtils.substringBetween(textIn, ",2", ";2").trim());
             UpdatePassagersAfterAccORRej(DriverIDA,passengersA);
             ServerConnection PedestrianSocket1 = map.get(PedestrianIDA);
             PedestrianSocket1.sendStringToClient("ACCEPTED DriverID "+DriverIDA);
             deletePedestrian(PedestrianIDA);
             System.out.println("RECEIVED ACCEPT");
             break;
    	 case "REJECT":
    		 int DriverIDR = Integer.parseInt(StringUtils.substringBetween(textIn, ",0", ";0").trim());
    		 int PedestrianIDR=Integer.parseInt(StringUtils.substringBetween(textIn, ",1", ";1").trim());
    		 int passengersR = Integer.parseInt(StringUtils.substringBetween(textIn, ",2", ";2").trim());
    		 UpdatePassagersAfterAccORRej(DriverIDR, passengersR);
    		 ServerConnection PedestrianSocket2 = map.get(PedestrianIDR);
             PedestrianSocket2.sendStringToClient("REJECTED DriverID "+DriverIDR); 
             break;
    	 case "COST":
    		 int DriverIDC= Integer.parseInt(StringUtils.substringBetween(textIn, ",0", ";0").trim());
    		 int PedestrianIDC=Integer.parseInt(StringUtils.substringBetween(textIn, ",1", ";1").trim());
    		 double cost= Double.parseDouble(StringUtils.substringBetween(textIn, ",2", ";2").trim());
    		 int Passengers =Integer.parseInt(StringUtils.substringBetween(textIn, ",3", ";3").trim());
    		 ServerConnection PedestrianSocket3= map.get(PedestrianIDC);
    		 PedestrianSocket3.sendStringToClient("Cost "+cost);
    		 UpdatePassagersAfterAccORRej(DriverIDC, Passengers);
    		 
    	 case "CHOICE":
    		 System.out.println("CHOIIIIIIICE");
    		 int PedestrianIDChoice = Integer.parseInt(StringUtils.substringBetween(textIn, ",0", ";0").trim());
    		 int DriverIDChoice=Integer.parseInt(StringUtils.substringBetween(textIn, ",1", ";1").trim());
    		 if(DriverIDChoice != -1){
    			 sendToDriver(PedestrianIDChoice, DriverIDChoice);
    			 System.out.println("SEND TO DRIVER CHOIIIIIIIIIIICE");
    		 }
    		 break;
         default:
             break;

    	 
    	 }
    	 
     }
     public void storeDriverData(int DriverID, double PosxD, double PosyD, double DestxD, double DestyD, int passengers, int capacity, double chargePerKilo, String PreferencesD){
    
    	 Connection con=null;
         Statement stmt=null;
         try{
        	 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        	 con=DriverManager.getConnection(connectionURL);
        	 System.out.println("Connection Established to Insert Driver Data");
        	 
        	 String SQL= "INSERT INTO Drivers Values("+DriverID+", "+PosxD+", "+PosyD+", "+DestxD+", "+DestyD+", "+passengers+","+capacity+","+chargePerKilo+", '"+PreferencesD+"')";
        	 stmt=con.createStatement();
        	 stmt.executeUpdate(SQL);
        	 
        	 
         }catch(Exception e){
        	 e.printStackTrace();
         }
     }
    
     public void storePedestrianData(int PedestrianID, double PosXP, double PosYP, double DestXP,double DestYP, String PreferencesP ){
    	 Connection con=null;
         Statement stmt=null;
         try{
        	 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        	 con=DriverManager.getConnection(connectionURL);
        	 System.out.println("Connection Established to Insert Pedestrian Data");
        	 
        	 String SQL= "INSERT INTO Pedestrians Values("+PedestrianID+", "+PosXP+", "+PosYP+", "+DestXP+", "+DestYP+", '"+PreferencesP+"')";
        	 stmt=con.createStatement();
        	  stmt.executeUpdate(SQL);
        	 
        	 
         }catch(Exception e){
        	 e.printStackTrace();
         }
     
     
     }
    public void updateDriverPosition(int DriverIDU, double PosxDU, double PosyDU, int passengersU){
    	 Connection con=null;
         Statement stmt=null;
         Statement stmt2=null;
         ResultSet rs = null;
         try{
        	 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        	 con=DriverManager.getConnection(connectionURL);
       // 	 System.out.println("Connection Established to Update Driver Data");
        	 
        	 String SQL= "UPDATE Drivers SET PosxD = "+PosxDU+", PosyD = "+PosyDU+", passengers = "+passengersU+" where DriverID = "+DriverIDU;
        	 stmt=con.createStatement();
        	 stmt.executeUpdate(SQL);
        	 String SQL2 = "Select * from Drivers where DriverID= "+DriverIDU;
        	 stmt2=con.createStatement();
        	 rs=stmt2.executeQuery(SQL2);
        	 double destxd=0;
        	 double destyd=0;
        	 int capacity=0;
        	 double chargePerkilo=0;
        	 String pref="";
        	 while(rs.next()){
        	   destxd =Double.parseDouble(rs.getString(4).trim());
        	   destyd= Double.parseDouble(rs.getString(5).trim());
        	   capacity=Integer.parseInt(rs.getString(7).trim());
        	   chargePerkilo=Double.parseDouble(rs.getString(8).trim()) ;
        	   pref=rs.getString(9);
        	 }
        	 comparePositionstoPedestrian(DriverIDU, PosxDU, PosyDU,destxd ,destyd ,passengersU,capacity ,chargePerkilo,pref);
         }catch(Exception e){
        	 e.printStackTrace();
         }
    	
    }
     
     public boolean checkPedDriverRange(double PosxD, double PosyD,double DestxD, double DestyD, double PosxP, double PosyP, double DestxP, double DestyP){
    	 double currentRange = Math.sqrt(Math.pow(PosxD-PosxP,2)+Math.pow(PosyD-PosyP,2));
    	 double destinationRange=Math.sqrt(Math.pow(DestxD-DestxP,2)+Math.pow(DestyD-DestyP,2));
    	 boolean destinationCase= ((currentRange <=1) && (destinationRange <=1));
    	 double a= DestyD-PosyD;
    	 double b= DestxD- PosxD;
    	 double c= (PosxD-DestxD)*PosyD + (DestyD-PosyD)*PosxD;
    	 double temp= Math.sqrt(Math.pow(a,2)+Math.pow(b,2));
    	 double temp2 = Math.abs(a*DestxP + b*DestyP + c);
    	 double distancePerpend= temp2/temp;
    	 boolean destinationCase2= ((currentRange <= 1) && (distancePerpend <=1));
    	 return destinationCase2 || destinationCase;
     }
     //Driver position in respect to the pedestrians(wa2ta l driver ya3mol connect mnet2akkad eza fi 7ada 7addo de8re ye5do
     public void comparePositionstoPedestrian(int DriverID, double PosxD, double PosyD,double DestxD, double DestyD,int passengers, int capacity, double chargePerKilo, String PreferencesD){
    	 Connection con=null;
         Statement stmt=null;
         ResultSet rs = null;
         String textToAClient="OFFER DriverID,0 "+ DriverID +";0 PosxD,1 "+PosxD+";1 PosyD,2 "+PosyD+";2 Passengers,3 "+passengers+";3 capacity,4 "+capacity+";4 ChargePerKilo,5 "+chargePerKilo+";5 Preferences,6 "+PreferencesD+";6";
         try{
        	 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        	 con=DriverManager.getConnection(connectionURL);
       // 	 System.out.println("Connection Established with SQL");
        	 
        	 String SQL= "Select * from Pedestrians";
        	 stmt=con.createStatement();
        	 rs= stmt.executeQuery(SQL);
        	 while(rs.next()){
        		 
        		 boolean check =checkPedDriverRange(PosxD, PosyD, DestxD, DestyD, Double.parseDouble(rs.getString(2).trim()),Double.parseDouble(rs.getString(3).trim()),Double.parseDouble(rs.getString(4).trim()),Double.parseDouble(rs.getString(5).trim()));
        	     if(check && (passengers < capacity)){
        	    	 System.out.println("inside check in comparePositions");
        	    	 int TempID=Integer.parseInt(rs.getString(1).trim());
        	    	 if(map.containsKey(TempID)){
        	    		 System.out.println("Found KEY");
        	    	 }
        	    	 ServerConnection PedestrianSocket =this.getFromMap(TempID);
        	    	 System.out.println(Integer.parseInt(rs.getString(1).trim()));
        	    	 System.out.println(PedestrianSocket.toString());
        	         PedestrianSocket.sendStringToClient(textToAClient);
        	         System.out.println("I could send it");
        	         
        	     } 
        	 }
        	 
         }catch(Exception e){
        	 e.printStackTrace();
         }
     }
     public void sendToDriver(int PedestrianIDChoice, int DriverIDChoice){
    	 Connection con=null;
         Statement stmt=null;
         ResultSet rs = null;
         try{
        	 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        	 con=DriverManager.getConnection(connectionURL);
        	 System.out.println("Connection Established to Insert Pedestrian Data");
        	 
        	 String SQL= "Select * from Pedestrians where PedestrianID = "+PedestrianIDChoice;
        	 stmt=con.createStatement();
        	 rs= stmt.executeQuery(SQL);
        	 double PosxP=0;
        	 double PosyP=0;
        	 double DesxP=0;
        	 double DesyP=0;
        	 String pref="";
        	 
        	 while(rs.next()){
        		 PosxP = Double.parseDouble(rs.getString(2).trim());
        		 PosyP=Double.parseDouble(rs.getString(3).trim());
        		 DesxP= Double.parseDouble(rs.getString(4).trim());
        		 DesyP= Double.parseDouble(rs.getString(5).trim());
        		 pref=rs.getString(6);
        	 }
        	 String command=",0 "+PedestrianIDChoice+";0 ,1 "+PosxP+";1 ,2 "+ PosyP+";2 ,3 "+DesxP+";3 ,4 "+DesyP+";4 ,5 "+pref+";5";
        	 ServerConnection DriverSocket= this.getFromMap(DriverIDChoice);
        	 DriverSocket.sendStringToClient(command);
        	 
         }catch(Exception e){
        	 e.printStackTrace();
         }
     }
     public void UpdatePassagersAfterAccORRej(int Driverid, int passengersUP){
    	 Connection con=null;
         Statement stmt=null;
      
         try{
        	 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        	 con=DriverManager.getConnection(connectionURL);
        	 System.out.println("Connection Established to Update passengers");
        	 
        	 String SQL= "UPDATE Drivers SET passengers = "+passengersUP+" where DriverID ="+Driverid;
        	 stmt=con.createStatement();
        	  stmt.executeUpdate(SQL);
     }catch(Exception e){
    	 e.printStackTrace();
     }
     }
     public void deletePedestrian(int PedestrianID){
    	 Connection con=null;
         Statement stmt=null;
      
         try{
        	 Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        	 con=DriverManager.getConnection(connectionURL);
        	 System.out.println("Connection Established to Update passengers");
        	 
        	 String SQL= "DELETE FROM Pedestrians where PedestrianID = "+PedestrianID;
        	 stmt=con.createStatement();
        	 stmt.executeUpdate(SQL);
     }catch(Exception e){
    	 e.printStackTrace();
     }
     
     }
}
