import java.util.Arrays;
/**
 * Write a description of class Simulation here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Simulation
{
   String worldAndRidesFileName;
   String allocationFileName; 
    
   public Simulation(String worldAndRidesFileName, String allocationFileName) {
       this.worldAndRidesFileName = worldAndRidesFileName;
       this.allocationFileName = allocationFileName;
   }
   
   public void run() {
       try{
           WorldAndRides worldAndRides = new WorldAndRides(worldAndRidesFileName);
           Allocation allocation = new Allocation(allocationFileName, worldAndRides);
           
           int score = 0;
           //compute score of allocation
           int t=0;
           int maxT=worldAndRides.getStep();
           for(int i=0;i<worldAndRides.getTotalVehicle();i++){//for every vehicle
               //allocation.getVehicleNo(i) -> ArrayList allocation.getRideListAllocated(i)
               int[] current={0,0};//every vehicle starts form [0,0]
               t=0;//when move on to next vehicle rest time
               boolean bonus=false;
               for(int r:allocation.getRideListAllocated(i)){//{arraylist} of ridenums allocated to "vehicle i"
                   while(t<maxT){
                       if(allocation.getAvailable(i)){//when vehicle available
                           //need to go to the start point
                           if(current[0]<worldAndRides.getFrom(r)[0]){
                               current[0]++;
                               t++;
                           }else if(current[0]>worldAndRides.getFrom(r)[0]){
                               current[0]--;
                               t++;
                           }else{//when row is equal
                               if(current[1]<worldAndRides.getFrom(r)[1]){
                                   current[1]++;
                                   t++;
                               }else if(current[1]>worldAndRides.getFrom(r)[1]){
                                   current[1]--;
                                   t++;
                               }
                           }
                           //when vehicle arrives at the start location
                           if(Arrays.equals(current,worldAndRides.getFrom(r))){
                               if(t<worldAndRides.getEarliest(r)){//when arrive early
                                   t++;
                                   continue;//wait for passanger
                               }else if(t==worldAndRides.getEarliest(r)){
                                   bonus=true;
                               }
                               allocation.setAvailable(i,false);//passenger on the vehicle
                           }
                       }else{//when vehicle not available
                           //need to go to destination point
                           if(current[0]<worldAndRides.getTo(r)[0]){
                               current[0]++;
                               t++;
                           }else if(current[0]>worldAndRides.getTo(r)[0]){
                               current[0]--;
                               t++;
                           }else{//when row is equal
                               if(current[1]<worldAndRides.getTo(r)[1]){
                                   current[1]++;
                                   t++;
                               }else if(current[1]>worldAndRides.getTo(r)[1]){
                                   current[1]--;
                                   t++;
                               }
                           }
                           //when vehicle arrives at the destination
                           if(Arrays.equals(current,worldAndRides.getTo(r))){
                               allocation.setAvailable(i,true);
                               if(t<=worldAndRides.getLatest(r)){//when arrive in time
                                   if(bonus){//check bonus
                                       score+=worldAndRides.getDistance(r)+worldAndRides.getBonus();
                                   }else{
                                       score+=worldAndRides.getDistance(r);
                                   }
                               }
                               bonus=false;
                               break;//move on to the next ride
                           }
                       }
                   }
               }
           }
           System.out.println(score);
       } catch (Exception ex) {
           System.out.println ("ERROR "+ ex.toString());
       }
   
}
}
