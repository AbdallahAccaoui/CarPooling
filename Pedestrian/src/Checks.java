import java.util.*;

import org.apache.commons.lang.StringUtils;

public class Checks {

	public static void main(String[] args) {
		String chekc= "DRIVER ID,0 1 ;0 Posx,11.0;1 Posy,2 4.0;2 Destx,3 10.0;3 Desty,4 10.0;4 Pref, NS, passengers,5 2;5 capacity,6 5;6 cost,7 25.0;7 NS, NK , NP";
		 String command= chekc.substring(0,chekc.indexOf(" "));
		
		 String test = StringUtils.substringBetween(chekc, ",0", ";0").trim();
		 int a= Integer.parseInt(test);
		 String NoSmokeP="";
         String NoPetsP="";
         String NoKidsP="";
         String QuietRideP="";
         if (chekc.contains("NS")) {
              NoSmokeP = "NS";  
         }
         if(chekc.contains("NP")) {
             NoPetsP = "NP";
         }
         if (chekc.contains("NK")) {
              NoKidsP = "NK";  
             }
         if(chekc.contains("QR")) {
              QuietRideP= "QR";
             }
         String PreferencesP=NoSmokeP+" "+ NoPetsP+" "+NoKidsP+" "+QuietRideP;
        System.out.println("ID"+command);
        System.out.println(test);
        System.out.println(PreferencesP);
        System.out.println(a);
        HashMap<Integer,String> map = new HashMap<>();
        map.put(1, "HII");
        String m=map.get(1);
        System.out.println(m);
        

	}

}
