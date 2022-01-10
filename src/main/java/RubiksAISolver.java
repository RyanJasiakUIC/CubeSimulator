import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;

import javax.swing.text.Position;

import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class RubiksAISolver {



    
    RubiksCube CubeReference;
    ConcurrentLinkedQueue<Character> MovesQueue;
    String starting_color;
    Object latch;
    HashMap<String,Character> ColorAxisDictionary; 
    HashMap<String,Integer>   ColorNegDictionary;
    int[][][] PositionIndex;

    RubiksAISolver() {
        starting_color = "green";

        PositionIndex = new int[3][3][3];
        resetPositionIndex();

        ColorAxisDictionary = new HashMap<>();
        ColorNegDictionary = new HashMap<>();

        ColorAxisDictionary.put("green", 'y');
        ColorNegDictionary.put("green", 1);

        ColorAxisDictionary.put("blue", 'y');
        ColorNegDictionary.put("blue", -1);

        ColorAxisDictionary.put("orange", 'z');
        ColorNegDictionary.put("orange", -1);

        ColorAxisDictionary.put("red", 'z');
        ColorNegDictionary.put("red", 1);

        ColorAxisDictionary.put("yellow", 'x');
        ColorNegDictionary.put("yellow", 1);

        ColorAxisDictionary.put("white", 'x');
        ColorNegDictionary.put("white", -1);

    }

    public void solve(RubiksCube rcr, ConcurrentLinkedQueue<Character> mq, Object l) {
        System.out.println("noooooooo");
        CubeReference = rcr;
        MovesQueue = mq;
        latch  = l;
        updateNegsDictionary();
        rotateCubeToOriginalOrientation();
        solveFirstLayer();
        solveSecondLayer();
        solveThirdLayer();
        CubeReference.aiIsDoneSolving();
        RubiksPiece r = getPieceAtPosition(4);
        r.SetGray();
        CubeReference.turnBackToOriginalColorAfterASec(r);
    }

    private RubiksPiece getPieceAtPosition(int pos) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    if (PositionIndex[x][y][z] == pos)
                        return CubeReference.getPieceAtCoords(x, y, z);
                }
            }
        }
        return null;
    }

    private void rotateCubeToOriginalOrientation() {
        moveColorToTop("red");
        // moveColorToFront("red");
    }
    

    

    public void setStartingColor(String color) {

    }

    /* 
    *  **** IMPORTANT METHODS REQUIRED FOR AI UNDERSTANDING
    *  **** PIECE POSITION AND ORIENTATION
    *
    *
    *   VOID methods
    *   - moveColorToFront(String color)
    *   - moveColorToTop(String color)
    *   - moveColorToBottom(String color)
    *   - moveColorToRight(String color)
    *
    *   String methods
    *   - getOtherColorOfPiece(String color, RubiksPiece p)
    *   
    *   CHAR methods
    *   - getTargetAxisOfColor(String color)
    *
    *
    *   Int methods
    *   - getPerspectivePositionOfEdge(RubiksPiece p)
    *   - getActualPositionOfEdge(RubiksPiece p)
    *   - getPerspectivePositionOfCorner(RubiksPiece p)
    *   - getActualPositionOfCorner(RubiksPiece p)
    */





    HashSet<RubiksPiece> getSetOfPieces(String color, _TYPE _t) {
        HashSet<RubiksPiece> PieceSet = new HashSet<>();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    RubiksPiece p = CubeReference.getPieceAtCoords(x, y, z);
                    if (p.getRevealedColors().contains(color) &&
                        p.get_TYPE() == _t)
                        PieceSet.add(p);
                }
            }
        }
        return PieceSet;
    }






    private void solveFirstLayer() {
        // moveColorToTop(starting_color);
        HashSet<RubiksPiece> corners = getSetOfPieces(starting_color, _TYPE.CORNER);
        HashSet<RubiksPiece> edges = getSetOfPieces(starting_color, _TYPE.EDGE);
        // moveColorToFront("orange");
        
    }

    private void solveSecondLayer() {

    }

    private void solveThirdLayer() {

    }




    // private String 


    

    private int getNegOfAxis(char axis) {
        switch (axis) {
            case 'x': return CubeReference.getXNeg();
            case 'y': return CubeReference.getYNeg();
            default:  return CubeReference.getZNeg();
        }
    }



    private void rotateCubeOnAxis(char axis, int angle) {
        CubeReference.setManualIsRotating(true);
        switch (axis) {
            case 'x': CubeReference.RotateCubeOnXAnimated(angle); rotatePositionIndexX(angle); break;
            case 'y': CubeReference.RotateCubeOnYAnimated(angle); rotatePositionIndexY(angle); break;
            case 'z': CubeReference.RotateCubeOnZAnimated(angle); rotatePositionIndexZ(angle); break;
        }
        while (CubeReference.manualIsRotating()) System.out.print("");
        updateNegsDictionary();
    }

    private void rotateFace(String face, int angle) {
        CubeReference.setManualIsRotating(true);
        switch (face) {
            case "right":  CubeReference.RotateRightFaceAnimated(angle); break;
            case "left":   CubeReference.RotateLeftFaceAnimated(angle); break;
            case "top":    CubeReference.RotateTopFaceAnimated(angle); break;
            case "bottom": CubeReference.RotateBottomFaceAnimated(angle); break;
            case "front":  CubeReference.RotateFrontFaceAnimated(angle); break;
            case "back":   CubeReference.RotateBackFaceAnimated(angle); break;
        }
        while (CubeReference.manualIsRotating()) System.out.print("");
        // switch (face) {
        //     case "right":  rotatePositionIndexX(angle); break;
        //     case "left":   rotatePositionIndexX(angle); break;
        //     case "top":    rotatePositionIndexZ(angle); break;
        //     case "bottom": rotatePositionIndexZ(angle); break;
        //     case "front":  rotatePositionIndexY(angle); break;
        //     case "back":   rotatePositionIndexY(angle); break;
        // }
    }

    private void rotatePositionIndexX(int direction) {
        int temp[][][] = new int[3][3][3];
        if (direction < 0) {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        temp[x][2 - z][y] = PositionIndex[x][y][z];
                    }
                }
            }
        } else {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = 0; z < 3; z++) {
                        temp[x][z][2 - y] = PositionIndex[x][y][z];
                    }
                }
            }
        }
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    PositionIndex[x][y][z] = temp[x][y][z];
                }
            }
        }
    }
    private void rotatePositionIndexY(int direction) {
        int temp[][][] = new int[3][3][3];
        if (direction < 0) {
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    for (int z = 0; z < 3; z++) {
                        temp[2 - z][y][x] = PositionIndex[x][y][z];
                    }
                }
            }
        } else {
            for (int y = 0; y < 3; y++) {
                for (int x = 0; x < 3; x++) {
                    for (int z = 0; z < 3; z++) {
                        temp[z][y][2 - x] = PositionIndex[x][y][z];
                    }
                }
            }
        }
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    PositionIndex[x][y][z] = temp[x][y][z];
                }
            }
        }
    }
    private void rotatePositionIndexZ(int direction) {
        int temp[][][] = new int[3][3][3];
        if (direction < 0) {
            for (int z = 0; z < 3; z++) {
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        temp[2 - y][x][z] = PositionIndex[x][y][z];
                    }
                }
            }
        } else {
            for (int z = 0; z < 3; z++) {
                for (int x = 0; x < 3; x++) {
                    for (int y = 0; y < 3; y++) {
                        temp[y][2 - x][z] = PositionIndex[x][y][z];
                    }
                }
            }
        }
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    PositionIndex[x][y][z] = temp[x][y][z];
                }
            }
        }
    }


    private char getOwnerOfAxis(char c) {
        switch (c) {
            case 'x': return CubeReference.getOwnerOfXAxis();
            case 'y': return CubeReference.getOwnerOfYAxis();
            default:  return CubeReference.getOwnerOfZAxis();
        }
    }
    
    private void updateNegsDictionary() {
        System.out.println("Updating negs");
        char owner = getOwnerOfAxis('y');
        int neg = getNegOfAxis(owner);
        ColorNegDictionary.put("green", neg);
        ColorNegDictionary.put("blue", neg * -1);

        owner = getOwnerOfAxis('z');
        neg = getNegOfAxis(owner);
        ColorNegDictionary.put("red", neg);
        ColorNegDictionary.put("orange", neg * -1);

        owner = getOwnerOfAxis('x');
        neg = getNegOfAxis(owner);
        ColorNegDictionary.put("yellow", neg);
        ColorNegDictionary.put("white", neg * -1);
    }

    //
    // Essentially, rotate to make the color's axis
    // owned by z.
    //
    private void moveColorToTop(String color) {
        char targ_z_axis = ColorAxisDictionary.get(color);
        while (CubeReference.getActualZAxis() != targ_z_axis || getNegOfAxis('z') != ColorNegDictionary.get(color)) {
            System.out.println("Actual z: " + CubeReference.getActualZAxis() + ", targ: " + targ_z_axis);
            System.out.println("getNeg: " + getNegOfAxis('z') + ", ColorNeg: " + ColorNegDictionary.get(color));
            switch (getOwnerOfAxis(targ_z_axis)) {
                case 'x': rotateCubeOnAxis('y', 90 * CubeReference.getXNeg()); break;
                case 'y': rotateCubeOnAxis('x', 90 * CubeReference.getXNeg()); break;
                case 'z': rotateCubeOnAxis('x', 90 * CubeReference.getXNeg()); break;
            }
        }
        System.out.println("Actual z: " + CubeReference.getActualZAxis() + ", targ: " + targ_z_axis);
        System.out.println("getNeg: " + getNegOfAxis('z') + ", ColorNeg: " + ColorNegDictionary.get(color));
    }

    private void moveColorToFront(String color) {
        char targ_y_axis = ColorAxisDictionary.get(color);
        int neg = ColorNegDictionary.get(color);
        while (CubeReference.getActualYAxis() != targ_y_axis || getNegOfAxis('y') != ColorNegDictionary.get(color)) {
            System.out.println("actual y: " + CubeReference.getActualYAxis() + ", neg: " + neg);
            rotateCubeOnAxis('z', 90);
        }
    }

    private void resetPositionIndex() {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    PositionIndex[x][y][z] = -1;
                }
            }
        }
        //
        // Edges
        //
        PositionIndex[0][0][1] = 0;
        PositionIndex[0][1][0] = 1;
        PositionIndex[0][1][2] = 2;
        PositionIndex[0][2][0] = 3;
        PositionIndex[1][0][0] = 4;
        PositionIndex[1][0][2] = 5;
        PositionIndex[1][2][0] = 6;
        PositionIndex[1][2][2] = 7;
        PositionIndex[2][0][1] = 8;
        PositionIndex[2][1][0] = 9;
        PositionIndex[2][1][2] = 10;
        PositionIndex[2][2][1] = 11;


    }

    private int getActualEdgePosition(RubiksPiece p) {
        RubiksCube c = CubeReference;
        if (c.getPieceAtCoords(0, 0, 1) == p) return 0;
        else if (c.getPieceAtCoords(0, 1, 0) == p) return 1;
        else if (c.getPieceAtCoords(0, 1, 2) == p) return 2;
        else if (c.getPieceAtCoords(0, 2, 0) == p) return 3;
        else if (c.getPieceAtCoords(1, 0, 0) == p) return 4;
        else if (c.getPieceAtCoords(1, 0, 2) == p) return 5;
        else if (c.getPieceAtCoords(1, 2, 0) == p) return 6;
        else if (c.getPieceAtCoords(1, 2, 2) == p) return 7;
        else if (c.getPieceAtCoords(2, 0, 1) == p) return 8;
        else if (c.getPieceAtCoords(2, 1, 0) == p) return 9;
        else if (c.getPieceAtCoords(2, 1, 2) == p) return 10;
        return 11;
    }

    private int getPerspectiveEdgePosition(RubiksPiece p) {
        int pos = getActualEdgePosition(p);
        char x = CubeReference.getActualXAxis();
        char y = CubeReference.getActualYAxis();
        char z = CubeReference.getActualZAxis();
        
        return 0;
    }

}

