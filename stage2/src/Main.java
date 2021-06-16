public class Main {
  public static void main(String[] args) throws FileFormatException{
    String worldAndRidesFileName = args[0];
    //modify the following to launch the allocation which would read the rides file and print
    
    //the allocation to the standard output, e.g
    Allocation a =new Allocation(worldAndRidesFileName);
    a.printAllocation();
  }
}
