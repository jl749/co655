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
    WorldAndRides worldAndRides;//getTotalVehicle(),getBonus(),getStep()
    ArrayList<Vehicle> vehicleAllocation=new ArrayList<>();
    ArrayList<WorldAndRides.Ride> rideList=new ArrayList<>();
    public Allocation(String worldAndRidesFileName) throws FileFormatException {
        worldAndRides=new WorldAndRides(worldAndRidesFileName);
    }
    
    public void printAllocation(){//make allocation file
        vehicleAllocation.clear();//reset allocation
        rideList=worldAndRides.getRideList();
        for(int i=0;i<worldAndRides.getTotalVehicle();i++){//for every vehicle
            vehicleAllocation.add(new Vehicle());
        }
        for(int j=0;j<worldAndRides.getTotalVehicle();j++){
            int no=getBestRide(vehicleAllocation.get(j),j);
            if(no==-1){
                System.out.println("All vehicles must have at least 1 ride allocated");
                return;
            }
            vehicleAllocation.get(j).addRide(no);
        }
        for(int i=0;i<worldAndRides.getRides();i+=worldAndRides.getTotalVehicle()){//NOT HAPPY :(
            for(int j=0;j<worldAndRides.getTotalVehicle();j++){
                int no=getBestRide(vehicleAllocation.get(j),j);
                if(no==-1){
                    continue;
                }
                vehicleAllocation.get(j).addRide(no);
            }
        }
        // try{         NO NEED TO CREATE A FILE
            // File out=new File("test.out");
            // FileWriter fw=new FileWriter(out);
            // for(Vehicle v:vehicleAllocation){
                // fw.write(v.getRideAllocated().size()+" ");
                // for(int n:v.getRideAllocated()){
                    // fw.write(n+" ");
                // }
                // fw.write("\n");
            // }
            // fw.close();
        // }catch(IOException ex){
            // System.out.println(ex.toString());
        // }
        for(Vehicle v:vehicleAllocation){
            System.out.print(v.getRideAllocated().size()+" ");
            for(int n:v.getRideAllocated()){
                System.out.print(n+" ");
            }
            System.out.print("\n");
        }
    }
    private int getBestRide(Vehicle v,int index){//index==vehicleNo
        int bestRide=-1;//ride number(0~N)
        int closest=0x7FFFFFFF;
        int fuel=0;
        WorldAndRides.Ride tmp=worldAndRides.new Ride();
        for(WorldAndRides.Ride r:rideList){
            if(!r.getAssigned()){
                if(r.calculateDistance()+r.calculateStart(v.getCurrent())<=worldAndRides.getStep()-v.getT()){//TotalD<=stepsLeft
                    if(r.ckBonus(v.getCurrent(),v.getT())){//when bonus available
                        if(v.getT()+r.calculateStart(v.getCurrent())<r.getEarliest()){//when arrive early
                            fuel=r.getEarliest()-v.getT();
                            if(fuel+r.calculateDistance()>worldAndRides.getStep()-v.getT()){continue;}//check again if TotalD<stepsLeft
                        }else{fuel=r.calculateStart(v.getCurrent())-worldAndRides.getBonus();}//when currentT+distanceS==earliestStart
                        fuel-=worldAndRides.getBonus();
                    }else{
                        fuel=r.calculateStart(v.getCurrent());
                    }
                    if(v.getT()+fuel+r.calculateDistance()>r.getLatest()){continue;}
                    if(fuel<closest){
                        closest=fuel;
                        tmp=r;
                        bestRide=r.getRideN();
                    }
                }
            }
        }
        if(tmp.getRideN()==-1){
            return bestRide;
        }
        rideList.get(tmp.getRideN()).assigned();
        vehicleAllocation.get(index).setCurrent(tmp.getTo());
        vehicleAllocation.get(index).setT(v.getT()+closest+tmp.calculateDistance());
        return bestRide;
    }
    
    private class Vehicle{
        ArrayList<Integer> rideAllocated=new ArrayList<>();//rideIndex allocated
        int[] current={0,0};//default
        int t=0;
        private Vehicle(ArrayList<Integer> rideAllocated){
            this.rideAllocated=rideAllocated;
        }
        private Vehicle(){}
        
        private int[] getCurrent(){
            return current;
        }
        private int getT(){
            return t;//current time at the location
        }
        private void setT(int t){
            this.t=t;
        }
        private void setCurrent(int[] newCurrent){
            current=newCurrent;
        }
        private ArrayList<Integer> getRideAllocated(){
            return rideAllocated;
        }
        private void addRide(int num){
            rideAllocated.add(num);
        }
    }
}