
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server{
	ServerSocket ss;
	ArrayList<ServerConnection> connections = new ArrayList<ServerConnection>();
    boolean shouldRun=true;
    
    
public static void main (String []args){
	new Server();
}
public Server(){
	try{
		ss= new ServerSocket(5481);	
		while(shouldRun){
		Socket s= ss.accept();
		ServerConnection sc=new ServerConnection(s,this);
		sc.start();
		connections.add(sc);
		
		
		}
		
		
	}catch(IOException e){
		e.printStackTrace();
	}
	
}






}
