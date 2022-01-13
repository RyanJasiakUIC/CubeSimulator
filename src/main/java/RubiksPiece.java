import java.util.*;

import javafx.geometry.Orientation;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.transform.*;
import javafx.scene.chart.Axis;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import java.lang.ProcessBuilder.Redirect.Type;
import java.sql.PreparedStatement;

import javafx.animation.PauseTransition;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

enum _TYPE {
    CORNER,
    EDGE,
    MIDDLE,
    CENTER
}

public class RubiksPiece {

    private HashMap<String, Box> Boxes;
    private HashMap<Character, Character> axes;
    private HashMap<Character, Integer>   negs;
    private HashSet<String> RevealedColors;
    private _TYPE t;
    private SmartGroup group;

    PhongMaterial redMaterial;
    PhongMaterial blueMaterial;
    PhongMaterial greenMaterial;
    PhongMaterial grayMaterial;
    PhongMaterial whiteMaterial;
    PhongMaterial yellowMaterial;
    PhongMaterial orangeMaterial;
    PhongMaterial blackMaterial;
    PhongMaterial PinkMaterial;

    RubiksPiece(int size) {

        Boxes = new HashMap<>();

        axes = new HashMap<>();
        axes.put('x', 'x');
        axes.put('y', 'y');
        axes.put('z', 'z');

        negs = new HashMap<>();
        negs.put('x', 1);
        negs.put('y', 1);
        negs.put('z', 1);
        
        group = new SmartGroup();
        RevealedColors = new HashSet<>();

        redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.RED);
        redMaterial.setSpecularColor(Color.RED);
        greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.GREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
        blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.BLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
        grayMaterial = new PhongMaterial();
        grayMaterial.setDiffuseColor(Color.SILVER);
        grayMaterial.setSpecularColor(Color.SILVER);
        whiteMaterial = new PhongMaterial();
        whiteMaterial.setDiffuseColor(Color.WHITESMOKE);
        whiteMaterial.setSpecularColor(Color.WHITESMOKE);
        yellowMaterial = new PhongMaterial();
        yellowMaterial.setDiffuseColor(Color.YELLOW);
        yellowMaterial.setSpecularColor(Color.YELLOW);
        orangeMaterial = new PhongMaterial();
        orangeMaterial.setDiffuseColor(Color.ORANGE);
        orangeMaterial.setSpecularColor(Color.ORANGE);
        blackMaterial = new PhongMaterial();
        blackMaterial.setDiffuseColor(Color.BLACK);
        blackMaterial.setSpecularColor(Color.BLACK);

        Boxes.put("green", new Box(size, size/50, size));
        Boxes.get("green").translateYProperty().set(size/2);

        Boxes.put("blue", new Box(size, size/50, size));
        Boxes.get("blue").translateYProperty().set(-size/2);

        Boxes.put("yellow", new Box(size/50, size, size));
        Boxes.get("yellow").translateXProperty().set(size/2);

        Boxes.put("white", new Box(size/50, size, size));
        Boxes.get("white").translateXProperty().set(-size/2);

        Boxes.put("orange", new Box(size, size, size/50));
        Boxes.get("orange").translateZProperty().set(size/2);

        Boxes.put("red", new Box(size, size, size/50));
        Boxes.get("red").translateZProperty().set(-size/2);

        SetColors();

        group.getChildren().addAll(Boxes.get("green"),
                                   Boxes.get("blue"),
                                   Boxes.get("yellow"),
                                   Boxes.get("white"),
                                   Boxes.get("orange"),
                                   Boxes.get("red"));
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

    public void SetGray() {
        Boxes.forEach((color, box)-> {
            box.setMaterial(grayMaterial);
        });
    }

    public void SetType(_TYPE _t) {
        t = _t;
    }

    public void DumpOrientation() {
        String x_neg = "", y_neg = "", z_neg = "";
        if (negs.get('x') < 1) x_neg = "-";
        if (negs.get('y') < 1) y_neg = "-";
        if (negs.get('z') < 1) z_neg = "-";
        o("x: " + x_neg + axes.get('x'));
        o("y: " + y_neg + axes.get('y'));
        o("z: " + z_neg + axes.get('z') + "\n");
    }

    public void SetColors() {
        Boxes.get("green").setMaterial(greenMaterial);
        Boxes.get("blue").setMaterial(blueMaterial);
        Boxes.get("yellow").setMaterial(yellowMaterial);
        Boxes.get("white").setMaterial(whiteMaterial);
        Boxes.get("orange").setMaterial(orangeMaterial);
        Boxes.get("red").setMaterial(redMaterial);
    }

    public void MakeObvious() {
        Boxes.forEach((color, box)-> {
            box.setMaterial(blackMaterial);
        });
    }

    public SmartGroup GetGroup() {
        return group;
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

    public _TYPE get_TYPE() {
        return t;
    }

    public void SetRevealedColors(String ... colors) {
        for (String color : colors)
            RevealedColors.add(color);
        
        if (!RevealedColors.contains("green")) Boxes.get("green").setMaterial(blackMaterial);
        if (!RevealedColors.contains("blue")) Boxes.get("blue").setMaterial(blackMaterial);
        if (!RevealedColors.contains("yellow")) Boxes.get("yellow").setMaterial(blackMaterial);
        if (!RevealedColors.contains("white")) Boxes.get("white").setMaterial(blackMaterial);
        if (!RevealedColors.contains("orange")) Boxes.get("orange").setMaterial(blackMaterial);
        if (!RevealedColors.contains("red")) Boxes.get("red").setMaterial(blackMaterial);
    }

    public HashSet<String> getRevealedColors() {
        return RevealedColors;
    }

    public void RotateOnAxesWithPivot(int ang, char perspective_axis, int x_piv, int y_piv, int z_piv) {
        Point3D ax;
        switch (axes.get(perspective_axis)) {
            case 'x': ax = Rotate.X_AXIS; break;
            case 'y': ax = Rotate.Y_AXIS; break;
            default:  ax = Rotate.Z_AXIS; break;
        }
        Rotate r = new Rotate(ang * negs.get(perspective_axis), ax);
        x_piv *= negs.get('x');
        y_piv *= negs.get('y');
        z_piv *= negs.get('z');
        if (x_piv != -999) {
            switch (axes.get('x')) {
                case 'x': r.setPivotX(x_piv); break;
                case 'y': r.setPivotY(x_piv); break;
                case 'z': r.setPivotZ(x_piv); break;
            }
        }
        if (y_piv != -999) {
            switch (axes.get('y')) {
                case 'x': r.setPivotX(y_piv); break;
                case 'y': r.setPivotY(y_piv); break;
                case 'z': r.setPivotZ(y_piv); break;
            }
        }
        if (z_piv != -999) {
            switch (axes.get('z')) {
                case 'x': r.setPivotX(z_piv); break;
                case 'y': r.setPivotY(z_piv); break;
                case 'z': r.setPivotZ(z_piv); break;
            }
        }
        group.getTransforms().add(r);
    }

    public void UpdateAxesFromRotation(int ang, char perspective_axis) {
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
        // if (RevealedColors.contains("red") &&
        //     RevealedColors.contains("blue") &&
        //     RevealedColors.contains("white"))
        //     DumpOrientation();
    }

    private void UpdateNegs(char c) {
        int n = negs.get(c);
        negs.put(c, n * -1);
    }

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

    void o(String s) {
        System.out.println(s);
    }
}
