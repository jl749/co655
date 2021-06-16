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
    
    public void printAllocation(){
        vehicleAllocation.clear();//reset allocation
        rideList=worldAndRides.getRideList();
        for(int i=0;i<worldAndRides.getTotalVehicle();i++){//for every vehicle
            vehicleAllocation.add(new Vehicle());
        }
        // for(int i=0;i<worldAndRides.getRides();i+=worldAndRides.getTotalVehicle()){//NOT HAPPY :(
            // for(int j=0;j<worldAndRides.getTotalVehicle();j++){
                // int no=getBestRide(vehicleAllocation.get(j),j,rideList);
                // if(no==-1){
                    // continue;
                // }
                // vehicleAllocation.get(j).addRide(no+"p");
                // carshare(vehicleAllocation.get(j),j,no);
                // vehicleAllocation.get(j).addRide(no+"d");
            // }
        // }
        for(int j=0;j<worldAndRides.getTotalVehicle();j++){
            int no=getBestRide(vehicleAllocation.get(j),j,rideList);
            if(no==-1){
                System.out.println("All vehicles must have at least 1 ride allocated");
                return;
            }
            vehicleAllocation.get(j).addRide(no+"p");
            carshare(vehicleAllocation.get(j),j,no);
            vehicleAllocation.get(j).addRide(no+"d");
        }
        int no=-1;
        for(int i=0;i<worldAndRides.getTotalVehicle();i++){
            no=getBestRide(vehicleAllocation.get(i),i,rideList);
            while(no!=-1){
                vehicleAllocation.get(i).addRide(no+"p");
                carshare(vehicleAllocation.get(i),i,no);
                vehicleAllocation.get(i).addRide(no+"d");
                no=getBestRide(vehicleAllocation.get(i),i,rideList);
            }
        }
        
        for(Vehicle v:vehicleAllocation){//PRINT
            System.out.print(v.getRideAllocated().size()/2+" ");
            for(String n:v.getRideAllocated()){
                System.out.print(n+" ");
            }
            System.out.print("\n");
        }
    }
    private void carshare(Vehicle v,int index,int rideN){
        WorldAndRides.Ride firstRide=worldAndRides.getRideList().get(rideN);
        ArrayList<WorldAndRides.Ride> candidates=new ArrayList<>();
        for(WorldAndRides.Ride r:rideList){
            if(!r.getAssigned() && firstRide.onTheWay(r)){
                candidates.add(r);
            }
        }
        
        WorldAndRides.Ride lastRide=firstRide;
        if(candidates.isEmpty()){
            v.setT(v.getT()+lastRide.calculateDistance());
            return;
        }
        while(v.getPassenger()<4){
            int nextRideN=getBestRide(v,index,candidates);
            if(nextRideN==-1){
                break;
            }
            WorldAndRides.Ride nextBestRide=rideList.get(nextRideN);
            nextBestRide.assigned();
            vehicleAllocation.get(index).addRide(nextRideN+"p");
            v.pickPassenger();
            WorldAndRides.Ride tmp=worldAndRides.new Ride(nextBestRide.getTo(),firstRide.getTo());
            lastRide=worldAndRides.new Ride(nextBestRide.getFrom(),firstRide.getTo());
            candidates.clear();
            for(WorldAndRides.Ride r:rideList){
                if(!r.getAssigned() && tmp.onTheWay(r)){
                    candidates.add(r);
                }
            }
            carshare(v,index,nextRideN);
            vehicleAllocation.get(index).addRide(nextRideN+"d");
            v.dropPassenger();
        }
        v.setT(v.getT()+lastRide.calculateDistance());
    }
    private int getBestRide(Vehicle v,int index,ArrayList<WorldAndRides.Ride> candidates){//index==vehicleNo
        int bestRide=-1;//ride number(0~N)
        int closest=0x7FFFFFFF;
        int fuel=0;
        WorldAndRides.Ride tmp=worldAndRides.new Ride();
        for(WorldAndRides.Ride r:candidates){
            if(!r.getAssigned()){
                if(r.calculateDistance()+r.calculateStart(v.getCurrent())<=worldAndRides.getStep()-v.getT()){//TotalD<=stepsLeft
                    if(r.ckBonus(v.getCurrent(),v.getT())){//when bonus available
                        if(v.getT()+r.calculateStart(v.getCurrent())<r.getEarliest()){//when arrive early vehicle waits
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
        rideList.get(tmp.getRideN()).assigned();//tick off the ride from ridelist
        vehicleAllocation.get(index).setCurrent(tmp.getFrom());
        vehicleAllocation.get(index).setT(v.getT()+closest);
        return bestRide;
    }
    
    private class Vehicle{
        ArrayList<String> rideAllocated=new ArrayList<>();//rideIndex allocated
        int[] current={0,0};//default
        int t=0;
        int passenger=1;
        private Vehicle(ArrayList<String> rideAllocated){
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
        private ArrayList<String> getRideAllocated(){
            return rideAllocated;
        }
        private void addRide(String num){
            rideAllocated.add(num);
        }
        private void pickPassenger(){passenger++;}
        private void dropPassenger(){passenger--;}
        private int getPassenger(){return passenger;}
    }
}