import java.lang.ProcessBuilder.Redirect.Type;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import javafx.animation.PauseTransition;
import javafx.scene.control.ScrollBar;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class RubiksCube {

    //
    // Data Members
    //
    private int cube_size;
    private int gap_size;
    private double speed;
    private int num_frames;
    private SmartGroup piece_groups[][][];
    private RubiksPiece pieces[][][];
    private RubiksPiece SolvedCubeOrientation[][][];
    private SmartGroup CubeGroup;
    private boolean is_rotating;
    private boolean manual_is_rotating;
    private boolean is_AI_solving;

    //
    // Orientations
    //
    private HashMap<Character,Character>  axes;
    private HashMap<Character,Integer>    negs;

    Object latch;


    //
    // Construction Conjunction
    //
    RubiksCube(int CUBE_SIZE, Object l) {

        speed = 2;
        num_frames = 6;
        gap_size = CUBE_SIZE / 30;
        is_rotating = false;
        manual_is_rotating = false;
        is_AI_solving = false;
        cube_size = CUBE_SIZE;
        CubeGroup = new SmartGroup();
        latch = l;

        piece_groups = new SmartGroup[3][3][3];
        SolvedCubeOrientation = new RubiksPiece[3][3][3];
        pieces = new RubiksPiece[3][3][3];

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    RubiksPiece rpr = new RubiksPiece(CUBE_SIZE);
                    SetPieceTypeAndRevealedColors("" + x + y + z, rpr);
                    pieces[x][y][z] = rpr;
                    SolvedCubeOrientation[x][y][z] = rpr;
                    rpr.GetGroup().translateXProperty().set((CUBE_SIZE + gap_size) * (x-1));
                    rpr.GetGroup().translateYProperty().set((CUBE_SIZE + gap_size) * (y-1));
                    rpr.GetGroup().translateZProperty().set((CUBE_SIZE + gap_size) * (z-1));
                    piece_groups[x][y][z] = rpr.GetGroup();
                    CubeGroup.getChildren().add(rpr.GetGroup());
                }
            }
        }

        axes = new HashMap<>(); 
        axes.put('x', 'x');
        axes.put('y', 'y');
        axes.put('z', 'z');

        negs = new HashMap<>();
        negs.put('x', 1);
        negs.put('y', 1);
        negs.put('z', 1);
    }




    /********************************************************
     *  
        PUBLIC METHODS
     *
    ********************************************************/

    //
    // GetGroup()
    //
    // Returns the group associated with the Cube
    //
    public SmartGroup GetGroup() {
        return CubeGroup;
    }

    public boolean IsRotatingSomethingAtTheMoment() {
        return is_rotating;
    }

    public void setManualIsRotating(boolean b) {
        manual_is_rotating = b;
    }

    public boolean manualIsRotating() {
        return manual_is_rotating;
    }

    public void SetSpeed(double d) {
        speed = d;
    }

    public void setNumFrames(int n) {
        num_frames = n;
    }

    public boolean isAISolving() {
        return is_AI_solving;
    }

    public void aiIsSolving() {
        is_AI_solving = true;
    }

    public void aiIsDoneSolving() {
        is_AI_solving = false;
    }

    public RubiksPiece getPieceAtCoords(int x, int y, int z) {
        return pieces[x][y][z];
    }

    public char getActualXAxis() {
        return axes.get('x');
    }
    public char getActualYAxis() {
        return axes.get('y');
    }
    public char getActualZAxis() {
        return axes.get('z');
    }

    public int getXNeg() {
        return negs.get('x');
    }
    public int getYNeg() {
        return negs.get('y');
    }
    public int getZNeg() {
        return negs.get('z');
    }

    public boolean pieceIsInOriginalSpot(RubiksPiece p) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    if (SolvedCubeOrientation[x][y][z] == p) {
                        return (pieces[x][y][z] == p);
                    }
                }
            }
        }
        return false;
    }

    public void turnUnOriginalyOrientedPieceBlackForASec() {
        is_rotating = true;
        pieces[0][0][1].SetGray();
        turnBackToOriginalColorAfterASec(pieces[0][0][1]);
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    if (!pieces[x][y][z].isInOriginalOrientation()) {
                        pieces[x][y][x].SetGray();
                        turnBackToOriginalColorAfterASec(pieces[x][y][z]);
                        return;
                    }
                }
            }
        }
    }

    public void turnPieceAtCoordsBlackForASec(int x, int y, int z) {
        is_rotating = true;
        pieces[x][y][z].SetGray();
        turnBackToOriginalColorAfterASec(pieces[x][y][z]);
    }

    public void turnBackToOriginalColorAfterASec(RubiksPiece rpr) {
        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(e -> {
            rpr.SetColors();
            is_rotating = false;
        });
        pause.play();
    }

    public boolean isInOriginalOrientation() {
        if (axes.get('x') == 'x' &&
            axes.get('y') == 'y' &&
            axes.get('z') == 'z' &&
            negs.get('x') == 1   &&
            negs.get('y') == 1   &&
            negs.get('z') == 1)
                return true;
        return false;
    }

    public boolean isSolved() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    if (!pieces[x][y][z].isInOriginalOrientation()) {
                        return false;
                    }
                    if (!(pieces[x][y][z] == SolvedCubeOrientation[x][y][z]))
                        return false;
                }
            }
        }
        return true;
    }

    private void DumpOrientation() {
        String x_neg = "", y_neg = "", z_neg = "";
        if (negs.get('x') < 1) x_neg = "-";
        if (negs.get('y') < 1) y_neg = "-";
        if (negs.get('z') < 1) z_neg = "-";
        o("x: " + x_neg + axes.get('x'));
        o("y: " + y_neg + axes.get('y'));
        o("z: " + z_neg + axes.get('z') + "\n");
    }

    //
    // RotateCubeOn[AXIS]Animated()
    //
    // Rotates the Cube on the respective Axis in
    // an animated fashion
    //
    public void RotateCubeOnXAnimated(int angle) {
        if (is_rotating) return;
        is_rotating = true;
        RotateCubeRecursive(angle/num_frames, num_frames, 'x');
    }
    public void RotateCubeOnYAnimated(int angle) {
        if (is_rotating) return;
        is_rotating = true;
        RotateCubeRecursive(angle/num_frames, num_frames, 'y');
    }
    public void RotateCubeOnZAnimated(int angle) {
        if (is_rotating) return;
        is_rotating = true;
        RotateCubeRecursive(angle/num_frames, num_frames, 'z');
    }


    public char getOwnerOfXAxis() {
        if (axes.get('x') == 'x') return 'x';
        if (axes.get('y') == 'x') return 'y';
        return 'z';
    }

    public char getOwnerOfYAxis() {
        if (axes.get('x') == 'y') return 'x';
        if (axes.get('y') == 'y') return 'y';
        return 'z';
    }

    public char getOwnerOfZAxis() {
        if (axes.get('x') == 'z') return 'x';
        if (axes.get('y') == 'z') return 'y';
        return 'z';
    }



    public boolean zAxisIsNeg() {
        return (negs.get('z') == -1);
    }

    

    
    //
    // Rotate[SIDE]FaceAnimated()
    //
    // Rotates a face of the Cube in the angle/direction
    // specified, and in an animated fashion
    //
    public void RotateRightFaceAnimated(int angle) {
        if (is_rotating) return;
        is_rotating = true;
        int side = (negs.get('x') == 1) ? 2 : 0;
        switch (axes.get('x')) {
            case 'x': RotateFaceRecursive1(angle/num_frames * negs.get('x'), num_frames, side); break;
            case 'y': RotateFaceRecursive2(angle/num_frames * negs.get('x'), num_frames, side); break;
            case 'z': RotateFaceRecursive3(angle/num_frames * negs.get('x'), num_frames, side); break;
        }
    }
    public void RotateLeftFaceAnimated(int angle) {
        if (is_rotating) return;
        is_rotating = true;
        int side = (negs.get('x') == 1) ? 0 : 2;
        switch (axes.get('x')) {
            case 'x': RotateFaceRecursive1(angle/num_frames * negs.get('x'), num_frames, side); break;
            case 'y': RotateFaceRecursive2(angle/num_frames * negs.get('x'), num_frames, side); break;
            case 'z': RotateFaceRecursive3(angle/num_frames * negs.get('x'), num_frames, side); break;
        }
    }
    public void RotateFrontFaceAnimated(int angle) {
        if (is_rotating) return;
        is_rotating = true;
        int side = (negs.get('y') == 1) ? 2 : 0;
        switch (axes.get('y')) {
            case 'x': RotateFaceRecursive1(angle/num_frames * negs.get('y'), num_frames, side); break;
            case 'y': RotateFaceRecursive2(angle/num_frames * negs.get('y'), num_frames, side); break;
            case 'z': RotateFaceRecursive3(angle/num_frames * negs.get('y'), num_frames, side); break;
        }
    }
    public void RotateBackFaceAnimated(int angle) {
        if (is_rotating) return;
        is_rotating = true;
        int side = (negs.get('y') == 1) ? 0 : 2;
        switch (axes.get('y')) {
            case 'x': RotateFaceRecursive1(angle/num_frames * negs.get('y'), num_frames, side); break;
            case 'y': RotateFaceRecursive2(angle/num_frames * negs.get('y'), num_frames, side); break;
            case 'z': RotateFaceRecursive3(angle/num_frames * negs.get('y'), num_frames, side); break;
        }
    }
    public void RotateTopFaceAnimated(int angle) {
        if (is_rotating) return;
        is_rotating = true;
        int side = (negs.get('z') == 1) ? 0 : 2;
        switch (axes.get('z')) {
            case 'x': RotateFaceRecursive1(angle/num_frames * negs.get('z'), num_frames, side); break;
            case 'y': RotateFaceRecursive2(angle/num_frames * negs.get('z'), num_frames, side); break;
            case 'z': RotateFaceRecursive3(angle/num_frames * negs.get('z'), num_frames, side); break;
        }
    }
    public void RotateBottomFaceAnimated(int angle) {
        if (is_rotating) return;
        is_rotating = true;
        int side = (negs.get('z') == 1) ? 2 : 0;
        switch (axes.get('z')) {
            case 'x': RotateFaceRecursive1(angle/num_frames * negs.get('z'), num_frames, side); break;
            case 'y': RotateFaceRecursive2(angle/num_frames * negs.get('z'), num_frames, side); break;
            case 'z': RotateFaceRecursive3(angle/num_frames * negs.get('z'), num_frames, side); break;
        }
    }









    /**********************************************************************
     *  
        PRIVATE FUNCTIONS
     *
    **********************************************************************/




    /******************************
    ***    ROTATING THE CUBE
    ******************************/

    //
    // RotateCubeRecursive()
    //
    // Recrusively rotates the cube 1 frame at a time. After
    // all frames are complete, the change in axes are updated
    // in the backend.
    //
    private void RotateCubeRecursive(int ang, int n, char perspective_axis) {
        PauseTransition s = new PauseTransition(Duration.millis(speed));
        s.setOnFinished(e -> {
            if (n > 0) {
                switch (axes.get(perspective_axis)) {
                    case 'x': CubeGroup.rotateByX(ang * negs.get(perspective_axis)); break;
                    case 'y': CubeGroup.rotateByY(ang * negs.get(perspective_axis)); break;
                    case 'z': CubeGroup.rotateByZ(ang * negs.get(perspective_axis)); break;
                }
                RotateCubeRecursive(ang, n-1, perspective_axis);
            } else {
                // if (ang < 0) o("NEGATIVE");
                // o("Actual: " + axes.get(perspective_axis) + ", perspective: " + perspective_axis);
                char to_change1, to_change2;
                switch (perspective_axis) {
                    case 'x':
                        to_change1 = 'y';
                        to_change2 = 'z';
                        break;
                    case 'y':
                        to_change1 = 'z';
                        to_change2 = 'x';
                        break;
                    default:
                        to_change1 = 'x';
                        to_change2 = 'y';
                        break;
                }
                RotateAxes(to_change1, to_change2, ang);
                is_rotating = false;
                manual_is_rotating = false;
                // DumpOrientation();
            }
        });
        s.play();
    }

    //
    // UpdateNegs()
    //
    // Simply changes the positive/negative status
    // of the axis by multiplying by -1
    //
    private void UpdateNegs(char c) {
        int n = negs.get(c);
        negs.put(c, n * -1);
    }

    //
    // FlipPerspectives()
    //
    // Swaps the axis owned by each HashMap-Axis, updating the negature
    // accordingly as well.
    //
    private void FlipPerspectives(char c1, char c2, int ang) {
        char temp1 = axes.get(c1),
             temp2 = axes.get(c2);
        if (ang > 0) {
            if (negs.get(c1) == negs.get(c2))
                 UpdateNegs(c1);
            else UpdateNegs(c2);
        } else {
            if (negs.get(c1) == negs.get(c2))
                 UpdateNegs(c2);
            else UpdateNegs(c1);
        }
        axes.put(c1, temp2);
        axes.put(c2, temp1);
    }

    //
    // RotateAxes()
    //
    // This is a function that does some things that I'm not
    // entirely sure how to explain but I mean who knows LOL
    //
    private void RotateAxes(char c1, char c2, int ang) {
        switch (c1) {
            case 'x':
                switch (c2) {
                    case 'y': FlipPerspectives('x', 'y', ang); break;
                    case 'z': FlipPerspectives('x', 'z', ang); break;
                }
                break;
            case 'y':
                switch (c2) {
                    case 'x': FlipPerspectives('y', 'x', ang); break;
                    case 'z': FlipPerspectives('y', 'z', ang); break;
                }
                break;
            case 'z':
                switch (c2) {
                    case 'x': FlipPerspectives('z', 'x', ang); break;
                    case 'y': FlipPerspectives('z', 'y', ang); break;
                }
                break;
        }
    }





    /******************************
    ***    ROTATING FACES
    ******************************/

    private void RotateFaceRecursive1(int ang, int n, int side) {
        PauseTransition s = new PauseTransition(Duration.millis(2));
        s.setOnFinished(e -> {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    RubiksPiece p = pieces[side][y][z];
                    int _y = (cube_size + gap_size) * (y-1) * -1;
                    int _z = (cube_size + gap_size) * (z-1) * -1;
                    p.RotateOnAxesWithPivot(ang, 'x', -999, _y, _z);
                }
            }
            if (n > 1) {
                RotateFaceRecursive1(ang, n-1, side);
            } else {
                RotateAxesOfPieces1(ang, side);
                RotateFaceInArray1(ang, side);
                is_rotating = false;
                manual_is_rotating = false;
            }
        });
        s.play();
    }
    private void RotateFaceRecursive2(int ang, int n, int side) {
        PauseTransition s = new PauseTransition(Duration.millis(2));
        s.setOnFinished(e -> {
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    RubiksPiece p = pieces[x][side][z];
                    int _x = (cube_size + gap_size) * (x-1) * -1;
                    int _z = (cube_size + gap_size) * (z-1) * -1;
                    p.RotateOnAxesWithPivot(ang, 'y', _x, -999, _z);
                }
            }
            if (n > 1) {
                RotateFaceRecursive2(ang, n-1, side);
            } else {
                RotateAxesOfPieces2(ang, side);
                RotateFaceInArray2(ang, side);
                is_rotating = false;
                manual_is_rotating = false;
            }
        });
        s.play();
    }
    private void RotateFaceRecursive3(int ang, int n, int side) {
        PauseTransition s = new PauseTransition(Duration.millis(2));
        s.setOnFinished(e -> {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    RubiksPiece p = pieces[x][y][side];
                    int _x = (cube_size + gap_size) * (x-1) * -1;
                    int _y = (cube_size + gap_size) * (y-1) * -1;
                    p.RotateOnAxesWithPivot(ang, 'z', _x, _y, -999);
                }
            }
            if (n > 1) {
                RotateFaceRecursive3(ang, n-1, side);
            } else {
                RotateAxesOfPieces3(ang, side);
                RotateFaceInArray3(ang, side);
                is_rotating = false;
                manual_is_rotating = false;
            }
        });
        s.play();
    }


    private void RotateAxesOfPieces1(int ang, int side) {
        // o("*********************************");
        for (int y = 0; y < 3; y++) {
            for (int z = 0; z < 3; z++) {
                pieces[side][y][z].UpdateAxesFromRotation(ang, 'x');
            }
        }
    }
    private void RotateAxesOfPieces2(int ang, int side) {
        // o("*********************************");
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                pieces[x][side][z].UpdateAxesFromRotation(ang, 'y');
            }
        }
    }
    private void RotateAxesOfPieces3(int ang, int side) {
        // o("*********************************");
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                pieces[x][y][side].UpdateAxesFromRotation(ang, 'z');
            }
        }
    }


    private void RotateFaceInArray1(int ang, int side) {
        SmartGroup temp[][] = new SmartGroup[3][3];
        RubiksPiece temp2[][] = new RubiksPiece[3][3];
        if (ang > 0) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    temp[2 - z][y] = piece_groups[side][y][z];
                    temp2[2 - z][y] = pieces[side][y][z];
                }
            }
        } else {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    temp[z][2 - y] = piece_groups[side][y][z];
                    temp2[z][2 - y] = pieces[side][y][z];
                }
            }
        }

        for (int y = 0; y < 3; y++) {
            for (int z = 0; z < 3; z++) {
                piece_groups[side][y][z] = temp[y][z];
                pieces[side][y][z] = temp2[y][z];
            }
        }
    }
    private void RotateFaceInArray2(int ang, int side) {
        SmartGroup temp[][] = new SmartGroup[3][3];
        RubiksPiece temp2[][] = new RubiksPiece[3][3];
        if (ang < 0) {
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    temp[2 - z][x] = piece_groups[x][side][z];
                    temp2[2 - z][x] = pieces[x][side][z];
                }
            }
        } else {
            for (int x = 0; x < 3; x++) {
                for (int z = 0; z < 3; z++) {
                    temp[z][2 - x] = piece_groups[x][side][z];
                    temp2[z][2 - x] = pieces[x][side][z];
                }
            }
        }

        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                piece_groups[x][side][z] = temp[x][z];
                pieces[x][side][z] = temp2[x][z];
            }
        }
    }
    private void RotateFaceInArray3(int ang, int side) {
        SmartGroup temp[][] = new SmartGroup[3][3];
        RubiksPiece temp2[][] = new RubiksPiece[3][3];
        if (ang > 0) {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    temp[2 - y][x] = piece_groups[x][y][side];
                    temp2[2 - y][x] = pieces[x][y][side];
                }
            }
        } else {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    temp[y][2 - x] = piece_groups[x][y][side];
                    temp2[y][2 - x] = pieces[x][y][side];
                }
            }
        }

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                piece_groups[x][y][side] = temp[x][y];
                pieces[x][y][side] = temp2[x][y];
            }
        }
    }





    /**
    ***    DEBUGGING
    **/

    private void d() {
        o("current x: " + axes.get('x') + ", current y: " + axes.get('y') + " , current z: " + axes.get('z'));
    }
    private void o(String s) {
        System.out.println(s);
    }

    



    /**
    ***    PIECES SETUP
    **/


    //
    // SetPieceTypeAndRevealedColors()
    //
    // Manually Sets up the pieces of the cube
    // piece by piece to ensure accuracy.
    //
    private void SetPieceTypeAndRevealedColors(String s, RubiksPiece piece) {
        switch (s) {
            case "000":
                piece.SetType(_TYPE.CORNER);
                piece.SetRevealedColors("red", "blue", "white");
                break;
            case "001":
                piece.SetType(_TYPE.EDGE);
                piece.SetRevealedColors("blue", "white");
                break;
            case "002":
                piece.SetType(_TYPE.CORNER);
                piece.SetRevealedColors("blue", "white", "orange");
                break;
            case "010":
                piece.SetType(_TYPE.EDGE);
                piece.SetRevealedColors("red", "white");
                break;
            case "011":
                piece.SetType(_TYPE.MIDDLE);
                piece.SetRevealedColors("white");
                break;
            case "012":
                piece.SetType(_TYPE.EDGE);
                piece.SetRevealedColors("white", "orange");
                break;
            case "020":
                piece.SetType(_TYPE.CORNER);
                piece.SetRevealedColors("red", "white", "green");
                break;
            case "021":
                piece.SetType(_TYPE.EDGE);
                piece.SetRevealedColors("white", "green");
                break;
            case "022":
                piece.SetType(_TYPE.CORNER);
                piece.SetRevealedColors("white", "green", "orange");
                break;
            case "100":
                piece.SetType(_TYPE.EDGE);
                piece.SetRevealedColors("red", "blue");
                break;
            case "101":
                piece.SetType(_TYPE.MIDDLE);
                piece.SetRevealedColors("blue");
                break;
            case "102":
                piece.SetType(_TYPE.EDGE);
                piece.SetRevealedColors("blue", "orange");
                break;
            case "110":
                piece.SetType(_TYPE.MIDDLE);
                piece.SetRevealedColors("red");
                break;
            case "111":
                piece.SetType(_TYPE.CENTER);
                break;
            case "112":
                piece.SetType(_TYPE.MIDDLE);
                piece.SetRevealedColors("orange");
                break;
            case "120":
                piece.SetType(_TYPE.EDGE);
                piece.SetRevealedColors("red", "green");
                break;
            case "121":
                piece.SetType(_TYPE.MIDDLE);
                piece.SetRevealedColors("green");
                break;
            case "122":
                piece.SetType(_TYPE.EDGE);
                piece.SetRevealedColors("green", "orange");
                break;
            case "200":
                piece.SetType(_TYPE.CORNER);
                piece.SetRevealedColors("red", "yellow", "blue");
                break;
            case "201": 
                piece.SetType(_TYPE.EDGE);
                piece.SetRevealedColors("yellow", "blue");
                break;
            case "202":
                piece.SetType(_TYPE.CORNER);
                piece.SetRevealedColors("yellow", "blue", "orange");
                break;
            case "210":
                piece.SetType(_TYPE.EDGE);
                piece.SetRevealedColors("red", "yellow");
                break;
            case "211":
                piece.SetType(_TYPE.MIDDLE);
                piece.SetRevealedColors("yellow");
                break;
            case "212":
                piece.SetType(_TYPE.EDGE);
                piece.SetRevealedColors("yellow", "orange");
                break;
            case "220":
                piece.SetType(_TYPE.CORNER);
                piece.SetRevealedColors("red", "green", "yellow");
                break;
            case "221":
                piece.SetType(_TYPE.EDGE);
                piece.SetRevealedColors("green", "yellow");
                break;
            case "222":
                piece.SetType(_TYPE.CORNER);
                piece.SetRevealedColors("green", "yellow", "orange");
        }
    }

}
