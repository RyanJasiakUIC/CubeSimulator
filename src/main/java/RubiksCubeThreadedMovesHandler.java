import java.util.concurrent.ConcurrentLinkedQueue;

import javafx.application.Platform;
import javafx.scene.control.TextField;

public class RubiksCubeThreadedMovesHandler extends Thread {

    RubiksCube CubeReference;
    ConcurrentLinkedQueue<Character> MovesQueue;

    boolean should_check_if_solved;
    
    RubiksCubeThreadedMovesHandler(RubiksCube _Cube, ConcurrentLinkedQueue<Character> _Moves) {
        if (_Cube == null || _Moves == null) {
            System.out.println("\n\n ********************************** NULL !!!!!!!!!!!!\n\n\n");
            Platform.exit();
        }
        CubeReference = _Cube;
        MovesQueue    = _Moves;

        should_check_if_solved = false;
    }

    public void run() {
        while (true) {
            if (!MovesQueue.isEmpty()) {
                if (!CubeReference.IsRotatingSomethingAtTheMoment()) {
                    switch (MovesQueue.peek()) {
                        case 'G': CubeReference.RotateCubeOnXAnimated(90); break;
                        case 'Y': CubeReference.RotateCubeOnXAnimated(-90); break;
                        case 'H': CubeReference.RotateCubeOnYAnimated(-90); break;
                        case 'T': CubeReference.RotateCubeOnYAnimated(90); break;
                        case 'B': CubeReference.RotateCubeOnZAnimated(90); break;
                        case 'N': CubeReference.RotateCubeOnZAnimated(-90); break;
                        case 'R': CubeReference.RotateRightFaceAnimated(-90); break;
                        case 'F': CubeReference.RotateRightFaceAnimated(90); break;
                        case 'Q': CubeReference.RotateLeftFaceAnimated(-90); break;
                        case 'A': CubeReference.RotateLeftFaceAnimated(90); break;
                        case 'Z': CubeReference.RotateFrontFaceAnimated(90); break;
                        case 'X': CubeReference.RotateFrontFaceAnimated(-90); break;
                        case 'C': CubeReference.RotateBackFaceAnimated(90); break;
                        case 'V': CubeReference.RotateBackFaceAnimated(-90); break;
                        case 'W': CubeReference.RotateTopFaceAnimated(90); break;
                        case 'E': CubeReference.RotateTopFaceAnimated(-90); break;
                        case 'S': CubeReference.RotateBottomFaceAnimated(90); break;
                        case 'D': CubeReference.RotateBottomFaceAnimated(-90); break;
                        case 'P': CubeReference.turnUnOriginalyOrientedPieceBlackForASec(); break;
                    }
                    MovesQueue.remove();
                }
            }
        }
    }
}
