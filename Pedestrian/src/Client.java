import java.io.*;
import java.net.*;

import java.util.*;

import org.apache.commons.lang.StringUtils;

public class Client{
	Socket s;
	DataInputStream din;
	DataOutputStream dout;
	int ID=0;
	public static void main (String [] args){
		new Client();
	}
	
	public Client(){
		try{
			 s = new Socket("localhost",5481);
			 din = new DataInputStream(s.getInputStream());
			 dout = new DataOutputStream(s.getOutputStream());
			
			listenForInput();
		}catch(UnknownHostException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void listenForInput(){
		
          String input="";
		
		    do{
		    	Scanner console = new Scanner(System.in);
		        Scanner consoleString = new Scanner(System.in);
		    	 input = command(console,consoleString);
			    
		    }while(input.equals("Input Mismatch"));
			if(input.toLowerCase().equals("quit")){
		
			}
			
			try {
				dout.writeUTF(input);
				while(din.available()==0){
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				String reply = din.readUTF();
				System.out.println(reply);
				String offer = din.readUTF();
				ProcessInFromServer(offer);
				
				String AcceptORReject=din.readUTF();
				 System.out.println("need to receive response");
				if(AcceptORReject.contains("ACCEPTED")|| AcceptORReject.contains("REJECTED") ){
		             System.out.println(AcceptORReject);
		             String CostAccept=din.readUTF();
		             System.out.println("Price "+ CostAccept);
		        }
				System.out.println("need to receive response");
				while (AcceptORReject.contains("OFFER")){
				System.out.println("need to receive response2");	
				String ac=din.readUTF();
				String az=din.readUTF();
				        if(ac.contains("ACCEPTED")|| ac.contains("REJECTED") || az.contains("ACCEPTED")|| az.contains("REJECTED") ){
				             System.out.println(az);
				             System.out.println(ac);
				             String CostAccept=din.readUTF();
				             System.out.println("Price "+ CostAccept);
				}
				AcceptORReject=ac;
				}
			} catch (IOException e) {
				e.printStackTrace();
		    }
		try {
			din.close();
			dout.close();
	     	s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String command(Scanner console, Scanner consoleString){
	   
		double posx=0;
		double posy=0;
		double destx=0;
		double destY=0;
		String pref="";
		String order="";
		 try{
	    System.out.print("Enter Your ID: ");
	    ID = console.nextInt();
	    System.out.println();
		System.out.print("Enter your posX: ");
		posx =console.nextDouble();
		posx=Math.round(posx*1000)/1000.0;
		System.out.println();
		System.out.print("Enter your posY: ");
		posy= console.nextDouble();
		posy=Math.round(posy*1000)/1000.0;
		System.out.println();
		System.out.print("Enter your DestX: ");
		destx=console.nextDouble();
		destx=Math.round(destx*1000)/1000.0;
		System.out.println();
		System.out.print("Enter your DestY: ");
		destY= console.nextDouble();
		destY=Math.round(destY*1000)/1000.0;
		System.out.println();
		System.out.print("Enter Your preferences: ");
		pref= consoleString.nextLine();
		
		}catch(InputMismatchException e)
		{
			order="Input Mismatch";
			System.out.println(order);
		}
		if(order.equals("Input Mismatch")){
			return order;
		}
		 order= "CAR_ORDER ID,0 "+ID+";0 PosX,1 "+posx+";1 PosY,2 "+posy+";2 DestX,3 "+destx+";3 DestY,4 "+destY+";4 Prefs, "+pref;
		
		return order;
	}
	
	public void ProcessInFromServer(String offer){
		 String command= offer.substring(0,offer.indexOf(" "));
		 switch (command){
		 case "OFFER":
			int DriverID = Integer.parseInt(StringUtils.substringBetween(offer, ",0", ";0").trim()); 
			double PosXP = Double.parseDouble(StringUtils.substringBetween(offer, ",1", ";1").trim());
            double PosYP = Double.parseDouble(StringUtils.substringBetween(offer, ",2", ";2").trim());
            int passengers = Integer.parseInt(StringUtils.substringBetween(offer, ",3", ";3").trim()); 
            int capacity= Integer.parseInt(StringUtils.substringBetween(offer, ",4", ";4").trim());
            double ChargePerKilo = Double.parseDouble(StringUtils.substringBetween(offer, ",5", ";5").trim());
            String Preferences = StringUtils.substringBetween(offer, ",6", ";6").trim();
            System.out.println("Driver ID | PosXP  |  PosYP  |  passengers | capacity |   ChargePerkilo |  Preferences");
            System.out.println(DriverID+"   "+PosXP+"   "+PosYP+"   "+passengers+"   "+capacity+"   "+ChargePerKilo+"   "+Preferences);
            
            String catchh="";
            int DriveID=0;
            do{
            System.out.println("If you want this driver send me his ID else enter -1 :");
            Scanner console = new Scanner(System.in);
            
            try{
            	 DriveID=console.nextInt();
            	
            }catch(InputMismatchException e){
            	catchh="ERROR";
            }
            
            }while(catchh.equals("ERROR"));
            String sendToServer="CHOICE ,0 "+ID+";0 ,1"+DriverID+";1";
            try{
            	dout.writeUTF(sendToServer);
            }catch(IOException e){
            	e.printStackTrace();
            }
            break;
          default:
        	  break;
		  }
		 
	   }
	
	
}




