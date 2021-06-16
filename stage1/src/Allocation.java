import java.io.*; 
import java.util.*;
import java.util.ArrayList;
import java.util.regex.*;

/**
 * Read allocation file and save it in the class
 *
 * @author (jl749)
 */
public class Allocation
{
    ArrayList<Vehicle> vehicleAllocation=new ArrayList<>();
    public Allocation(String allocationFileName, WorldAndRides worldAndRides) throws FileFormatException {
        try{
            Scanner s=new Scanner(new File(allocationFileName));
            Pattern p=Pattern.compile("(\\d+)(\\s+\\d+)+");
            HashSet<Integer> duplicateCK=new HashSet<>();//ride allocated must not duplicate
            int size=0;
            while(s.hasNextLine()){
                String line=s.nextLine();
                Matcher m=p.matcher(line);
                ArrayList<Integer> rideAllocated=new ArrayList<>();//for ride allocated for a vehicle
                if(m.find()){//when pattern found
                    String[] arr=line.split("\\s+");
                    if(Integer.parseInt(arr[0])!=arr.length-1){throw new FileFormatException("Wrong format(out)");}
                    for(int i=1;i<arr.length;i++){
                        rideAllocated.add(Integer.parseInt(arr[i]));
                        duplicateCK.add(Integer.parseInt(arr[i]));
                        size++;
                    }
                    vehicleAllocation.add(new Vehicle(rideAllocated));
                }else{throw new FileFormatException("Pattern not found(out)");}
            }

            if(duplicateCK.size()!=size){throw new FileFormatException("Rides should be allocated only once(out)");}
            if(worldAndRides.getTotalVehicle()!=vehicleAllocation.size()){
                throw new FileFormatException("All vehicle must be allocated(out)");
            }//when vehicle number doesn't match
            //no need to check rides (can be skipped)
        }catch(IOException ex){
            System.out.println(ex.toString());
        }
    }
    
    //accessor methods
    public ArrayList<Integer> getRideListAllocated(int index){
        return vehicleAllocation.get(index).getRideAllocated();
    }
    public boolean getAvailable(int index){
        return vehicleAllocation.get(index).getAvailable();
    }
    public void setAvailable(int index,boolean val){
        vehicleAllocation.get(index).setAvailable(val);
    }
    
    private class Vehicle{
        boolean available=true;
        ArrayList<Integer> rideAllocated=new ArrayList<>();//rideIndex allocated
        private Vehicle(ArrayList<Integer> rideAllocated){
            this.rideAllocated=rideAllocated;
        }
        
        private boolean getAvailable(){
            return available;
        }
        private void setAvailable(boolean val){
            available=val;
        }
        private ArrayList<Integer> getRideAllocated(){
            return rideAllocated;
        }
    }
}