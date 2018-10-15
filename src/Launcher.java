public class Launcher {

    public static void main(String[] args){


        Process p1 = new Process("P1");
        Process p2 = new Process("P2");
        Process p3 = new Process("P3");
        Process p4 = new Process("P4");
        Process p5 = new Process("P5");
        Process p6 = new Process("P6");

        try{
            Thread.sleep(5000);
        }catch(Exception e){
            e.printStackTrace();
        }

        p1.stop();
        p2.stop();
        p3.stop();
        p4.stop();
        p5.stop();
        p6.stop();
        p1.waitStopped();
        p2.waitStopped();
        p3.waitStopped();
        p4.waitStopped();
        p5.waitStopped();
        p6.waitStopped();


        System.out.println("Final clocks: " + p1.getClock() + ", " + p2.getClock() + ", " + p3.getClock());



    }

}
