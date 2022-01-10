import java.util.concurrent.ConcurrentLinkedQueue;

public class AIThread extends Thread {
    RubiksAISolver ai;
    RubiksCube CubeReference;
    ConcurrentLinkedQueue<Character> MovesQueue;
    String starting_color;
    Object latch;


    AIThread(RubiksCube rcr, ConcurrentLinkedQueue<Character> mq, Object l) {
        ai = new RubiksAISolver();
        CubeReference = rcr;
        MovesQueue = mq;
        latch  = l;
    }

    public void run() {
        ai.solve(CubeReference, MovesQueue, latch);
    }
    
}
