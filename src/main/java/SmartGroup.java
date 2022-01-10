import javafx.collections.ObservableList;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.transform.*;

class SmartGroup extends Group {
    Rotate r;
    Transform t = new Rotate();

    public static void addRotate(Node node, Point3D rotationAxis, double angle) {
        ObservableList<Transform> transforms = node.getTransforms();
        try {
            for (Transform _t : transforms) {
                rotationAxis = _t.inverseDeltaTransform(rotationAxis);
            }
        } catch (NonInvertibleTransformException ex) {
            throw new IllegalStateException(ex);
        }
        transforms.add(new Rotate(angle, rotationAxis));
    }

    // void rotateByX(int ang) {
    //     this.getTransforms().add(new Rotate(ang,Rotate.X_AXIS));
    //     addRotate(this, Rotate.X_AXIS, ang/2);
    // }
    // void rotateByY(int ang) {
    //     this.getTransforms().add(new Rotate(ang,Rotate.Y_AXIS));
    //     addRotate(this, Rotate.Y_AXIS, ang/2);
    // }
    // void rotateByZ(int ang) {
    //     this.getTransforms().add(new Rotate(ang,Rotate.Z_AXIS));
    //     addRotate(this, Rotate.Z_AXIS, ang/2);
    // }
    void rotateByX(int ang) {
        r = new Rotate(ang, Rotate.X_AXIS);
        t = t.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().add(t);
    }
    void rotateByY(int ang) {
        r = new Rotate(ang, Rotate.Y_AXIS);
        t = t.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().add(t);
    }
    void rotateByZ(int ang) {
        r = new Rotate(ang, Rotate.Z_AXIS);
        t = t.createConcatenation(r);
        this.getTransforms().clear();
        this.getTransforms().add(t);
    }
}