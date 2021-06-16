import java.io.*;
import java.util.Scanner;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Read information from the worldAndRidesFile and store the information
 *
 * @author (jl749)
 */
public class WorldAndRides
{
    int row,column,vehicle,bonus;
    int rides;
    int steps;
    ArrayList<Ride> rideList=new ArrayList<>();//index is ride number
    public WorldAndRides(String worldAndRidesFileName) throws FileFormatException {
        try{
            //Scanner s=new Scanner(new BufferedReader(new FileReader(worldAndRidesFileName)));
            Scanner s=new Scanner(new File(worldAndRidesFileName));
            Scanner tmp=new Scanner(new File(worldAndRidesFileName));//to peek next line
            int line=1;
            Pattern p=Pattern.compile("(\\d+)(\\s+)(\\d+)(\\s+)(\\d+)(\\s+)(\\d+)(\\s+)(\\d+)(\\s+)(\\d+)");
            while(s.hasNextInt()&&tmp.hasNextLine()){
                Matcher m=p.matcher(tmp.nextLine());
                if(line==1&&m.find()){//for line1 and pattern is ok
                    row=s.nextInt();
                    column=s.nextInt();
                    vehicle=s.nextInt();
                    rides=s.nextInt();
                    bonus=s.nextInt();
                    steps=s.nextInt();
                    line++;
                }else if(m.find()){//from line2
                    int x1=s.nextInt();
                    int y1=s.nextInt();
                    int x2=s.nextInt();
                    int y2=s.nextInt();
                    if(x1<=row&&y1<=column&&x2<=row&&y2<=column){//coordinate size check
                        int[] start={x1,y1};
                        int[] end={x2,y2};
                        if(Arrays.equals(start,end)){
                            throw new FileFormatException("Start location and Destination must be different(in)");
                        }
                        Ride r=new Ride(start,end,s.nextInt(),s.nextInt());
                        rideList.add(r);
                    }else{throw new FileFormatException("Coordinate size out of boundary(in)");}
                }else{throw new FileFormatException("Pattern not found(in)");}//wrong pattern
            }
            if(line==1){throw new FileFormatException("No line read(in)");}//when empty
            if(rides!=rideList.size()){throw new FileFormatException("Check number of rides(in)");}//check number of rides
        }catch(IOException ex){
            System.out.println(ex.toString());
        }
    }
    
    public int getDistance(int index){
        return rideList.get(index).calculateDistance();
    }
    public int getTotalVehicle(){
        return vehicle;
    }
    public int getBonus(){
        return bonus;
    }
    public int getRides(){
        return rides;
    }
    public int getStep(){
        return steps;
    }
    public int[] getFrom(int index){
        return rideList.get(index).getFrom();
    }
    public int[] getTo(int index){
        return rideList.get(index).getTo();
    }
    public int getEarliest(int index){
        return rideList.get(index).getEarliest();
    }
    public int getLatest(int index){
        return rideList.get(index).getLatest();
    }
    
    private class Ride//nested class    static???
    {
        int[] from=new int[2];
        int[] to=new int[2];
        int earliestStart;
        int latestFinish;
        private Ride(int[] from,int[] to,int earliestStart,int latestFinish){
            this.from=from;
            this.to=to;
            this.earliestStart=earliestStart;
            this.latestFinish=latestFinish;
        }
        private int[] getFrom(){//accessor
            return from;
        }
        private int[] getTo(){
            return to;
        }
        private int getEarliest(){
            return earliestStart;
        }
        private int getLatest(){
            return latestFinish;
        }
        private int calculateDistance(){
            int x=Math.abs(from[0]-to[0]);
            int y=Math.abs(from[1]-to[1]);
            return x+y;
        }
    }
}
