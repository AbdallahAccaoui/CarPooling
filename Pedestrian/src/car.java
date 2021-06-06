import java.io.*; 
import java.net.*; 
import java.text.*; //just in case we need to use any of its classes or methods
import java.util.*;


import org.apache.commons.lang.StringUtils;

public class car {
//defining all the variables needed in the program for the car/driver AND the pedestrian
static double xloc; //x coordinate of driver location
static double yloc; //y coordinate of driver location
static double xdes;//x coordinate of driver destination
static double ydes; //y coordinate of driver destination
static int pcar; //nr. of passengers in the car CURRENTLY
static int ccar; //max capacity of the car
static String prefs;//the prefrences of the driver in their car
static double cost; // charging per kilometer
static String clientdrivercommand; //the dommand sent to server to define the driver with his id and all the characteristics
static double plocy; // xlocation of pedestrian
static double plocx; // ylocation of pedestrian
static int ID; // driver ID
static int PID; //pedestrian id (will be used when it is extracted from the info sent by the server
static double tcost;// 
static double sxloc;
static double syloc;
public static void main(String[] args)throws Exception {
Scanner S= new Scanner(System.in); //making scanner so user can input all the characteristics of his car e.g current location, destination, preferences capacity etc etc
System.out.println("Please enter the x and y coordinates of your current Location respectively");
xloc= S.nextDouble(); //
yloc=S.nextDouble();
System.out.println("please enter the x and y coordinates of your destination's location respectively");
xdes= S.nextDouble();
ydes=S.nextDouble();
System.out.println("Please enter the number of Passengers currently in the car");
pcar=S.nextInt();
System.out.println("Please enter the maximum number of passengers available");
ccar= S.nextInt();
System.out.println("Please enter your regulations from the following");
System.out.println("1. no smoking"); // the available  the driver can choose to add from. 
System.out.println("2. no pets");
System.out.println("3. no kids");
System.out.println("4. quiet ride");
System.out.println("5. exit/done");


Scanner S2=new Scanner(System.in); //needed a new scanner to read the string of preferences 
String prefs=S2.nextLine(); // 
System.out.println("please enter an ID number");
ID=S.nextInt();
System.out.println("How much would you like to charge per Kilometer?");
cost = S.nextDouble(); //reading the cost per kilometer
//S.close(); 
//S2.close(); //closing both scanners since the user will no longer input stuff for now
clientdrivercommand="DRIVER ID,0 "+ ID  + ";0 Posx,1"  + xloc+";1 Posy,2 " + yloc + ";2 Destx,3 "+ xdes +";3 Desty,4 " + ydes +";4 Pref, "+ prefs +", passengers,5 "+ pcar + ";5 capacity,6 "+ ccar + ";6 cost,7 "+cost+";7"; 
System.out.print(clientdrivercommand); // concatenating and printing the final result of all the inputs of the user driver using specific numbers to allow the server to extract specific info needed using these unique number/commas combinations
BufferedReader infromuser = new BufferedReader(new InputStreamReader(System.in));//might remove this not sure of its use for now, taken from slides
String hostname= new String("localhost");// creating a string with the IP address of the sever to use it in the created socket soon
Socket clientsocket = new Socket(hostname,5481); //creating our socket

DataOutputStream outtoserver=new DataOutputStream(clientsocket.getOutputStream());// this will be what we use in order to send stuff to the server
DataInputStream infromserver = new DataInputStream(clientsocket.getInputStream()); // this will be what we use in order to receive stuff from the server
//clientsentence = infromuser.readLine(); 
outtoserver.writeUTF(clientdrivercommand+'\n');  //sending the concatenated full drivercommand to the server inorder to infrom the server of our location destination and all our characteristics for the first time
  
String ack1= infromserver.readUTF();//acts as ack
System.out.println("command sent"); //making sure that the command is sent to the server by displaying text afterwards
System.out.println(ack1);
String pedestrianoffer="a"; // will store whatever offer we recieve from server here in this variable
String offerresp=""; // store the driver's response here (accept or reject the request) 
//long firsttime= System.nanoTime(); //uneccessary  
sxloc=xloc;
syloc=yloc;
//Scanner aa=new Scanner(System.in); //read the driver/user's response whether he accepted or rejected
while ((xloc!=xdes) && (yloc!=ydes)){ // making the main while loop, making sure it is only runing as long as the driver still has not reached the destination
//saving the initial start location of the car in syloc and sxloc
futurelocation(xloc,yloc,xdes,ydes,0,5);
// using the future location function to modify the current x and y positions of the driver
String update=formcommand(); 
outtoserver.writeUTF(update); //sending the new x locations and updated passenger count and the ID of the driver to the server
String ack2=infromserver.readUTF();//receive ack
//System.out.println(ack2);

//if (infromserver.available()!=0) {
while(pedestrianoffer.equals("a")){
pedestrianoffer= infromserver.readUTF(); //reading the request recieved from the server from the pedestrian
System.out.println(pedestrianoffer); //print the offer for the user to see and decide whether to take it or not(accept or reject)
PID = (int)Double.parseDouble(StringUtils.substringBetween(pedestrianoffer,",0 ",";0").trim()); //(extracting the pedestrian ID from the command recieved)
 
System.out.println("ACCEPT or REJECT?");//ask the user if he wants to accept or reject the offer
String catchh="";
do{
   
    Scanner console = new Scanner(System.in);
    
    try{
    	 offerresp=console.next();
    	
    }catch(InputMismatchException e){
    	catchh="ERROR";
    }
    
    }while(catchh.equals("ERROR"));;
System.out.println(offerresp);
//offerresp=offerresp+",0 "+ID +";0 ,1 "+ PID + ",2 "+ ;  
//outtoserver.writeUTF(offerresp);
//}

}

 
if((offerresp.equals("ACCEPT")== true)&&pcar<ccar){ //case where it is accepted
pcar=pcar+1; //incrementing the number of passengers since we ust accepted a new one
offerresp=offerresp+" ,0 "+ID +";0 ,1 "+ PID +";1 ,2 "+pcar+";2";  //send the message to server along with driver Id, pedestrian ID and the number of passengers in car at the moment
outtoserver.writeUTF(offerresp);// send the response along with the rest of the info of ID and passengers to the server
String ack3= infromserver.readUTF();//receive the ack
System.out.println(ack3);
//formcommand();
//outtoserver.writeUTF(clientdrivercommand);
//String pedestriancommand=infromserver.readUTF();
 
plocx = Double.parseDouble(StringUtils.substringBetween(pedestrianoffer, ",1 ", ";1").trim());//take the x and y location of the pedestrian from the command sent by the server in order to later determine the amount of time we need to wait for the pedestrian to arrive to the stopped care
            plocy = Double.parseDouble(StringUtils.substringBetween(pedestrianoffer, ",2 ", ";2").trim()); 
            int waiting=pedestriantime(plocx,plocy,xloc,yloc)*1000; //use the function to calculate the waiting time, it calculates it in seconds, we multiply it by 1000 to get it in milliseconds
            System.out.println("waiting time is "+waiting);
            Thread.sleep(waiting); //wait the calculated time
            offerresp="none";// so that we dont enter this if statement section again in the second and thrid etc... iterations of the loop, so we dont send the server an accept message for the same client every time
 
 
}
 
if((offerresp.equals("REJECT")== true)&&pcar<ccar) 
{
offerresp=offerresp+" ,0 "+ID +";0 ,1 "+ PID +";1 ,2 "+pcar+";2"; // if the response was a REJECT we need to inform the pedestrian so we send it to the server along with the driver ID, pedestrian ID and number of passengers 
outtoserver.writeUTF(offerresp); //send it to server
String ack4=infromserver.readUTF();//receive the ack
System.out.println(ack4);
offerresp="none"; // we change the offerresp so that when the loop is ran again(while loop) we dont re-enter the reject if loop again for another time for the same client, so we only go through it the first time 
}
 
 
}
pcar=pcar-1;

String Cost=formcost(); //finally once xloc and yloc equal to ydes and xdes and we exit the while loop, it means we reached destination so we send the total cost to the server so it can forward it to pedestrian.
outtoserver.writeUTF(Cost);
String ack5= infromserver.readUTF();//receive the ack
System.out.println(ack5);
}

static void futurelocation(double x, double y, double xe, double ye, double t1, double t2){
double slope= (ye-y)/(xe-x); //find the slope, xe means xend so destination x, same for xy
double timedif=(t2-t1);
//*(java.lang.Math.pow(10, -6)); //converting the time difference to seconds, from milliseconds to seconds
double distancetraveled= 0.02*timedif; //average speed is 20 meters a second, so its 0.02 kilom times the time difference
double k= distancetraveled/(java.lang.Math.sqrt(1+(slope*slope))); //applying the formula
xloc=x+k;
yloc=y+slope*k;
}

static String formcommand(){
return "UPDATE ID,0 " + ID +";0 Posx,1 " + xloc + ";1 Posy,2 "+ yloc +";2 pass,3 " + pcar +";3"; //forming the UPDATE COMMAND
}

 static int pedestriantime(double px, double py, double cx, double cy) { //calculating the waiting time or time needed for the pedestrian to reach the car
double distancetotravel=java.lang.Math.sqrt(((cx-px)*(cx-px))+ ((cy-py)*(cy-py)) );
int traveltime= (int)(distancetotravel*0.005);
return traveltime;
}

 static String formcost() { //calculating the total cost
double tcost= java.lang.Math.sqrt(((xdes-sxloc)*(xdes-sxloc))+ ((ydes-syloc)*(ydes-syloc)))*cost;
return "UPDATE ID,0 " + ID +";0 PID,1 " + PID + ";1 thecost,2 "+ tcost +";2 pass,3 " + pcar +";3";
 }
 
}


