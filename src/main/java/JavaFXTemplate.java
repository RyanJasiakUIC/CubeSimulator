import java.security.Key;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.ThreadLocalRandom;


import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

import javafx.application.Application;
import javafx.event.Event;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PointLight;
import javafx.scene.shape.*;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Rotate;


public class JavaFXTemplate extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    Button aiButton;
    Button scrambleButton;
    RubiksCube Cube;
    SmartGroup CubeGroup;
    double speed;
    ConcurrentLinkedQueue<Character> MovesQueue;
    ArrayList<Character> MovesArray;

    boolean is_scrambling;

    SubScene CubeScene;

    Object latch;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

        speed = 2;
        MovesQueue = new ConcurrentLinkedQueue<>();
        latch = new Object();

        createMovesArrayForScrambling();

		primaryStage.setTitle("Rubiks Cube Simulator");

        Cube = new RubiksCube(60, latch);
        CubeGroup = new SmartGroup();
        CubeGroup.getChildren().add(Cube.GetGroup());
        CubeGroup.translateXProperty().set(WIDTH/2);
        CubeGroup.translateYProperty().set(HEIGHT/2);
        CubeGroup.rotateByX(-60);
        CubeGroup.rotateByZ(40);

        RubiksCubeThreadedMovesHandler RoobThread = new RubiksCubeThreadedMovesHandler(Cube, MovesQueue);
        RoobThread.start();

        primaryStage.addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (!Cube.isAISolving() && !is_scrambling) {
                switch (e.getCode()) {
                    case T: MovesQueue.add('T'); break;
                    case Y: MovesQueue.add('Y'); break;
                    case G: MovesQueue.add('G'); break;
                    case H: MovesQueue.add('H'); break;
                    case B: MovesQueue.add('B'); break;
                    case N: MovesQueue.add('N'); break;
                    case R: MovesQueue.add('R'); break;
                    case F: MovesQueue.add('F'); break;
                    case Q: MovesQueue.add('Q'); break;
                    case A: MovesQueue.add('A'); break;
                    case Z: MovesQueue.add('Z'); break;
                    case X: MovesQueue.add('X'); break;
                    case C: MovesQueue.add('C'); break;
                    case V: MovesQueue.add('V'); break;
                    case W: MovesQueue.add('W'); break;
                    case E: MovesQueue.add('E'); break;
                    case S: MovesQueue.add('S'); break;
                    case D: MovesQueue.add('D'); break;
                    case P: MovesQueue.add('P'); break;
                }
            }
        });


        Pane UI_Pane = new Pane();

        aiButton = new Button("AI Solve (Not yet functioning)");
        scrambleButton = new Button("Scramble it up!");
        
        aiButton.setOnAction(e -> {
            // if (!Cube.isAISolving()) {
            //     Cube.setNumFrames(10);
            //     Cube.aiIsSolving();
            //     new Thread(() -> {
            //         RubiksAISolver ai = new RubiksAISolver();
            //         ai.solve(Cube, MovesQueue, latch);
            //     }).start();
            // }
        });

        is_scrambling = false;
        scrambleButton.setOnAction(e -> {
            if (!is_scrambling) {
                is_scrambling = true;
                new Thread(() -> {
                    Cube.setNumFrames(3);
                    for (int i = 0; i < 70; i++) {
                        MovesQueue.add(MovesArray.get(ThreadLocalRandom.current().nextInt(0, MovesArray.size()-1)));
                    }
                    while (!MovesQueue.isEmpty()) System.out.print("");
                    Cube.setNumFrames(6);
                    is_scrambling = false;
                }).start();
            }
        });

        BorderPane bp = new BorderPane();
        VBox v = new VBox(scrambleButton, aiButton);
        bp.setLeft(v);

        PointLight light = new PointLight();
        light.setTranslateX(1000);
        light.setTranslateY(1500);
        light.setTranslateZ(-19000);

        Group CubeSceneGroup = new Group(light, CubeGroup);

        CubeScene = new SubScene(CubeSceneGroup, WIDTH, HEIGHT, true, SceneAntialiasing.DISABLED);
        bp.setCenter(CubeScene);

		Scene scene = new Scene(bp, WIDTH*7/6, HEIGHT*7/6, true);
        scene.setFill(Color.BLACK);
		scene.getRoot().setStyle("-fx-font-family: 'Verdana';" + "-fx-focus-color: transparent;  -fx-background-color: transparent;");
		primaryStage.setScene(scene);
		primaryStage.show();
    }

    void o(String s) {
        System.out.println(s);
    }













    public void createMovesArrayForScrambling() {
        MovesArray = new ArrayList<>();
        MovesArray.add('T');
        MovesArray.add('Y');
        MovesArray.add('G');
        MovesArray.add('H');
        MovesArray.add('B');
        MovesArray.add('N');
        MovesArray.add('R');
        MovesArray.add('F');
        MovesArray.add('Q');
        MovesArray.add('A');
        MovesArray.add('Z');
        MovesArray.add('X');
        MovesArray.add('C');
        MovesArray.add('V');
        MovesArray.add('W');
        MovesArray.add('E');
        MovesArray.add('S');
        MovesArray.add('D');
        MovesArray.add('P');
    }

}
