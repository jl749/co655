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
                            throw new FileFormatException("Start location and Destination must be different");
                        }
                        Ride r=new Ride(start,end,s.nextInt(),s.nextInt(),line-2);
                        rideList.add(r);
                        line++;
                    }else{throw new FileFormatException("Coordinate size out of boundary");}
                }else{throw new FileFormatException("Pattern not found");}//wrong pattern
            }
            
            if(rides!=rideList.size()){throw new FileFormatException("Check number of rides");}//check number of rides
        }catch(IOException ex){
            System.out.println(ex.toString());
        }
    }
    
    //accessor methods
    public ArrayList<Ride> getRideList(){
        return rideList;
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
    
    public class Ride//nested class    static???
    {
        int rideN=-1;
        int[] from=new int[2];
        int[] to=new int[2];
        int earliestStart;
        int latestFinish;
        boolean assigned=false;
        private Ride(int[] from,int[] to,int earliestStart,int latestFinish,int rideN){
            this.from=from;
            this.to=to;
            this.earliestStart=earliestStart;
            this.latestFinish=latestFinish;
            this.rideN=rideN;
        }
        public Ride(){}
        public Ride(int[] from,int[] to){
            this.from=from;
            this.to=to;
        }
        
        public int[] getFrom(){//accessor
            return from;
        }
        public int[] getTo(){
            return to;
        }
        public int getEarliest(){
            return earliestStart;
        }
        public int getLatest(){
            return latestFinish;
        }
        public int calculateDistance(){
            int x=Math.abs(from[0]-to[0]);
            int y=Math.abs(from[1]-to[1]);
            return x+y;
        }
        public int calculateStart(int[] current){
            int x=Math.abs(current[0]-from[0]);
            int y=Math.abs(current[1]-from[1]);
            return x+y;
        }
        public boolean ckBonus(int[] current,int t){
            if(t+calculateStart(current)<=earliestStart&&getEarliest()+calculateDistance()<=latestFinish){
                return true;
            }
            return false;
        }
        public void assigned(){
            assigned=true;
        }
        public boolean getAssigned(){
            return assigned;
        }
        public int getRideN(){
            return rideN;
        }
        
        public boolean onTheWay(WorldAndRides.Ride r){
            int[] from=getFrom();
            int[] to=getTo();
            int[] start=r.getFrom();
            int[] end=r.getTo();
            if(from[0]<=to[0] && from[1]<=to[1]){//upright
                if(start[0]<=end[0] && start[1]<=end[1]){
                if(start[0]>=from[0] && start[1]>=from[1] && start[0]<=to[0] && start[1]<=to[1] && end[0]>=from[0] && end[1]>=from[1] && end[0]<=to[0] && end[1]<=to[1]){
                    return true;
                }
                }
            }else if(from[0]<=to[0] && from[1]>=to[1]){//downright
                if(start[0]<=end[0] && start[1]>=end[1]){
                if(start[0]>=from[0] && start[1]<=from[1] && start[0]<=to[0] && start[1]>=to[1] && end[0]>=from[0] && end[1]<=from[1] && end[0]<=to[0] && end[1]>=to[1]){
                    return true;
                }
                }
            }else if(from[0]>=to[0] && from[1]>=to[1]){//downleft
                if(start[0]>=end[0] && start[1]>=end[1]){
                if(start[0]<=from[0] && start[1]<=from[1] && start[0]>=to[0] && start[1]>=to[1] && end[0]<=from[0] && end[1]<=from[1] && end[0]>=to[0] && end[1]>=to[1]){
                    return true;
                }
                }
            }else if(from[0]>=to[0] && from[1]<=to[1]){//upleft
                if(start[0]>=end[0] && start[1]<=end[1]){
                if(start[0]<=from[0] && start[1]>=from[1] && start[0]>=to[0] && start[1]<=to[1] && end[0]<=from[0] && end[1]>=from[1] && end[0]>=to[0] && end[1]<=to[1]){
                    return true;
                }
                }
            }
            return false;
        }
    }
}
