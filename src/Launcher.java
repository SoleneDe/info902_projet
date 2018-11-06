public class Launcher {

    public static void main(String[] args){

        // Processes
        Process p1 = new Process("P1");
        Process p2 = new Process("P2");
        Process p3 = new Process("P3");

        // Time running
        try{
            Thread.sleep(4000);
        }catch(Exception e){
            e.printStackTrace();
        }

        // Stop the process
        p1.stop();
        p2.stop();
        p3.stop();

        // Waiting for them to actually stop, to end the execution
        p1.waitStopped();
        p2.waitStopped();
        p3.waitStopped();

        System.out.println("Final clocks: " + p1.getClock() + ", " + p2.getClock() + ", " + p3.getClock());

    }

}
