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
        CubeReference = rcr;
        MovesQueue = mq;
        latch  = l;

        rotateCubeToOriginalOrientation();

        solveGreenFaceLayer();
        solveMiddlelayer();
        solveThirdLayer();

        CubeReference.aiIsDoneSolving();
        // RubiksPiece r = getPieceAtPosition(4);
        // r.SetGray();
        // CubeReference.turnBackToOriginalColorAfterASec(r);
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
        System.out.println("Rotating to Original orientation...");
        if (CubeReference.getActualZAxis() == 'z') {
            if (CubeReference.getZNeg() == -1) {
                rotateCubeOnAxis('y', 90);
                rotateCubeOnAxis('y', 90);
            }
        } else {
            while (CubeReference.getActualZAxis() != 'z' || CubeReference.getZNeg() != 1) {
                rotateCubeOnAxis('x', 90);
                if (CubeReference.getActualZAxis() == 'z' && CubeReference.getZNeg() == 1) break;
                rotateCubeOnAxis('y', 90);
            }
        }
        while (CubeReference.getActualXAxis() != 'x' || CubeReference.getXNeg() != 1)
            rotateCubeOnAxis('z', 90);
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



    private RubiksPiece getPiece(String color, HashSet<RubiksPiece> set_of_pieces) {
        for (RubiksPiece r : set_of_pieces)
            if (r.getRevealedColors().contains(color)) return r;
        return null;
    }

    private boolean isOnBlueFace(RubiksPiece r) {
        int i = getPositionIndex(r);
        return (i == 0 ||
                i == 4 ||
                i == 5 ||
                i == 8);
    }


    private void solveGreenFaceLayer() {
        HashSet<RubiksPiece> corners = getSetOfPieces("green", _TYPE.CORNER);
        HashSet<RubiksPiece> edges = getSetOfPieces("green", _TYPE.EDGE);
        RubiksPiece yg_piece = getPiece("yellow", edges);
        System.out.println("position index: " + getPositionIndex(yg_piece));
        yg_piece.DumpOrientation();
        String colors[] = {"yellow", "red", "white", "orange"};
    

        for (int i = 0; i < 4; i++) {
            System.out.println(colors[i]);
            RubiksPiece edge = getPiece(colors[i], edges);
            moveEdgePieceToBlueFace(edge);
            moveEdgePieceToSolvedSpot(edge, colors[i]);
        }

    }


    private void moveEdgePieceToBlueFace(RubiksPiece r) {
        int index = getPositionIndex(r);
        String face = "";
        int angle = 90;
        if (index == 0 || index == 4 || index == 5 || index == 8) {
            return;
        } else if (index == 1 || index == 2 || index == 9 || index == 10) {
            switch (index) {
                case 1:  face = "left";  angle  = -90; break;
                case 2:  face = "left";  angle  =  90; break;
                case 9:  face = "right"; angle  = -90; break;
                case 10: face = "right"; angle  =  90; break;
            }
            rotateFace(face, angle);
            rotateFace("back", 90);
            rotateFace(face, angle*-1);
        } else {
            switch (index)  {
                case 3: face = "left"; break;
                case 6: face = "top"; break;
                case 7: face = "bottom"; break;
                case 11: face = "right"; break;
            }
            rotateFace(face, 90);
            rotateFace(face, 90);
            rotateFace("back", 90);
            rotateFace(face, -90);
            rotateFace(face, -90);
        }
    }

    private void moveEdgePieceToSolvedSpot(RubiksPiece r, String color) {
        int position = 0;
        switch (color) {
            case "yellow": position = 8; break;
            case "red":    position = 4; break;
            case "white":  position = 0; break;
            case "orange": position = 5; break;
        }
        while (getPositionIndex(r) != position) rotateFace("back", 90);
        String face = "";
        String helper = "";
        switch (color) {
            case "yellow": face = "right";    helper = "top"; break;
            case "red":    face = "top";     helper = "left"; break;
            case "white":  face = "left";  helper = "bottom"; break;
            case "orange": face = "bottom"; helper = "right"; break;
        }
        if (r.getActualXAxis() != 'x' || r.getActualYAxis() != 'y')
            edgePieceAlg2(color);
        else 
            edgePieceAlg1(face);
        r.DumpOrientation();
    }

    private void edgePieceAlg1(String face) {
        rotateFace(face, 90);
        rotateFace(face, 90);
    } 

    private void edgePieceAlg2(String color) {
        System.out.println("Alg2");
        String face = "";
        String helper = "";
        int neg = 1, neg2 = 1;
        switch (color) {
            case "yellow": face = "right";    helper = "top";  break;
            case "red":    face = "top";     helper = "left"; neg = -1; break;
            case "white":  face = "left";  helper = "bottom"; neg = -1; neg2 = -1; break;
            case "orange": face = "bottom"; helper = "right"; neg2 = -1; break;
        }
        rotateFace("back", 90);
        rotateFace(helper, 90 * neg2);
        rotateFace(face, 90 * neg);
        rotateFace(helper, 90 * neg2 * -1);
    }


    private void solveMiddlelayer() {

    }

    private void solveThirdLayer() {

    }


    private int getPositionIndex(RubiksPiece r) {
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 3; z++) {
                    if (CubeReference.getPieceAtCoords(x, y, z) == r) {
                        // System.out.println("Index: " + PositionIndex[x][y][z]);
                        return PositionIndex[x][y][z];
                    }
                }
            }
        }
        System.out.println("hmmm couldn't find piece");
        return -1;
    }




    // private String 



    private void rotateCubeOnAxis(char axis, int angle) {
        CubeReference.setManualIsRotating(true);
        // System.out.println("x: " + CubeReference.getActualXAxis() + ", y: " + CubeReference.getActualYAxis() + ", z: " + CubeReference.getActualZAxis());
        switch (axis) {
            case 'x': CubeReference.RotateCubeOnXAnimated(angle); break;
            case 'y': CubeReference.RotateCubeOnYAnimated(angle); break;
            case 'z': CubeReference.RotateCubeOnZAnimated(angle); break;
        }
        while (CubeReference.manualIsRotating()) System.out.print("");
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
        PositionIndex[0][2][1] = 3;
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

