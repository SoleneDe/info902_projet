public class Launcher {

    public static void main(String[] args){


        Process p1 = new Process("P1");
        Process p2 = new Process("P2");
        Process p3 = new Process("P3");


        try{
            Thread.sleep(1000);
        }catch(Exception e){
            e.printStackTrace();
        }

        p1.stop();
        p2.stop();
        p3.stop();


        p1.waitStopped();
        p2.waitStopped();
        p3.waitStopped();



        System.out.println("Final clocks: " + p1.getClock() + ", " + p2.getClock() + ", " + p3.getClock());



    }

}
