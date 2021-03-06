package sample;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.*;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;


public class Main extends Application {

    public static double SHIP_TEMPLATE_WIDTH = 100;
    private BooleanProperty firingArcVisibile = new SimpleBooleanProperty(true);


    public Parent createContent(Stage stage, PathTransition pathTransition) throws Exception {

        // Image
        Image texture = new Image("file:resources/starfield.jpg");
        ImagePattern imagePattern = new ImagePattern(texture);

        //Box testBox = new Box(1000, 1000, 500);
        Rectangle rectangle = new Rectangle(1000, 1000);
        rectangle.heightProperty().bind(stage.heightProperty());
        rectangle.widthProperty().bind(stage.widthProperty());
        rectangle.setStroke(Color.GRAY);

        PhongMaterial textureMaterial = new PhongMaterial();
        textureMaterial.setDiffuseMap(texture);
        //testBox.setMaterial(textureMaterial);
        //testBox.setDrawMode(DrawMode.FILL);

        rectangle.setFill(imagePattern);


        ParallelCamera parallelCamera = new ParallelCamera();
        //parallelCamera.getTransforms().add(transform3D);
                //new Rotate(-20, Rotate.Y_AXIS),
                //new Rotate(-20, Rotate.X_AXIS),
                //new Translate(-500, -500, 0));
          //      new Translate(0, 0, 0));*/


        //PhongMaterial falconMaterial = new PhongMaterial();

        //falconMaterial.setDiffuseMap(falcon);
        //testBox.setMaterial(textureMaterial);
        //testBox.setDrawMode(DrawMode.FILL);


        final Rectangle shipToken = getShipToken("resources/ywing.jpg");
        MovementTemplate movementTemplate = getHardTurnTemplate(200, 200, 20, 100);
        Path firingArc = getFiringArc(400);

        pathTransition.setDuration(Duration.millis(2000));
        pathTransition.setPath(movementTemplate.movementPath);
        pathTransition.setNode(shipToken);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);
        pathTransition.play();



        /*
        final KeyValue kvx = new KeyValue(ship.translateXProperty(), 500);
        final KeyValue kvy = new KeyValue(ship.translateYProperty(), 500);
        final KeyFrame kfx = new KeyFrame(Duration.millis(1000), kvx);
        final KeyFrame kfy = new KeyFrame(Duration.millis(1000), kvy);
        timeline.getKeyFrames().add(kfx);
        timeline.getKeyFrames().add(kfy);
        timeline.play();              */

        // Build the Scene Graph
        Group root = new Group();
        root.getChildren().add(parallelCamera);
        root.getChildren().add(rectangle);
        root.getChildren().add(movementTemplate.movementTemplate);
        root.getChildren().add(shipToken);
        root.getChildren().add(firingArc);

        root.setOnMousePressed(event -> {
            if (event.getTarget() instanceof Rectangle) {
                ((Rectangle) event.getTarget()).setStroke(Color.YELLOW);
            }
        });

        root.setOnMouseReleased(event -> {
            if (event.getTarget() instanceof Rectangle) {
                ((Rectangle) event.getTarget()).setStroke(Color.GRAY);
            }
        });

        firingArc.setLayoutX(SHIP_TEMPLATE_WIDTH / 2.0);
        firingArc.setLayoutY(SHIP_TEMPLATE_WIDTH / 2.0);

        Rotate rotate = new Rotate();
        rotate.angleProperty().bind(shipToken.rotateProperty());
        firingArc.getTransforms().add(rotate);
        firingArc.translateXProperty().bind(shipToken.translateXProperty());
        firingArc.translateYProperty().bind(shipToken.translateYProperty());
        firingArc.visibleProperty().bind(firingArcVisibile);

        // Use a SubScene
        SubScene subScene = new SubScene(root, 1200, 1200, true, null);
        subScene.setFill(Color.WHITE);
        subScene.setCamera(parallelCamera);
        subScene.heightProperty().bind(stage.heightProperty());
        subScene.widthProperty().bind(stage.widthProperty());

        Group group = new Group();
        group.getChildren().add(subScene);
        return group;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setResizable(true);
        PathTransition pathTransition = new PathTransition();
        Scene scene = new Scene(createContent(primaryStage, pathTransition));

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case SPACE:
                    pathTransition.play();
                    break;
                case F:
                    firingArcVisibile.set(!firingArcVisibile.get());
                    break;
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public Rectangle getShipToken(String fileName) {
        final Rectangle shipToken = new Rectangle (0, 0, 100, 100);
        shipToken.setArcHeight(5);
        shipToken.setArcWidth(5);
        shipToken.getTransforms().addAll(
                new Translate(50, 50, 0),
                new Rotate(90, 0, 0, 1),
                new Translate(-50, -50, 0)
        );
        shipToken.setStroke(Color.GRAY);
        shipToken.setStrokeWidth(3.0);

        Image shipTokenImage = new Image("file:"+fileName);
        ImagePattern imagePattern = new ImagePattern(shipTokenImage);
        shipToken.setFill(imagePattern);

        return shipToken;
    }

    public static class MovementTemplate {
        Path movementPath;
        Path movementTemplate;

        public MovementTemplate(Path movementPath, Path movementTemplate) {
            this.movementPath = movementPath;
            this.movementTemplate = movementTemplate;
        }
    }

    public Path getFiringArc(double firingRadius) {
        Path firingArc = new Path();

        double x1 = firingRadius * Math.cos(45.0/180.0*Math.PI);
        double y1 = -firingRadius * Math.sin(45.0/180.0*Math.PI);

        double x2 = firingRadius * Math.cos(45.0/180.0*Math.PI);
        double y2 = firingRadius * Math.sin(45.0/180.0*Math.PI);

        firingArc.getElements().add(new MoveTo(0, 0));
        firingArc.getElements().add(new LineTo(x1, y1));
        firingArc.getElements().add(new ArcTo(firingRadius, firingRadius, 90, x2, y2, false, true));
        firingArc.getElements().add(new LineTo(0, 0));
        firingArc.setFill(Color.rgb(255, 0, 0, 0.5));
        firingArc.setStroke(Color.rgb(255, 0, 0, 1.0));
        firingArc.setFillRule(FillRule.NON_ZERO);

        return firingArc;
    }

    public MovementTemplate getHardTurnTemplate(double x, double y, double width, double radius) {
        Path movementPath = new Path();
        Path movementTemplate = new Path();

        double outerRadius = radius + width;
        double innerRadius = radius - width;

        double x0 = x;
        double y0 = y-width;

        double x1 = x0 + outerRadius;
        double y1 = y0 + outerRadius;

        double x2 = x1 - width*2;
        double y2 = y1;

        double x3 = x2 - innerRadius;
        double y3 = y2 - innerRadius;

        movementTemplate.getElements().add(new MoveTo(x0, y0));
        movementTemplate.getElements().add(new ArcTo(outerRadius, outerRadius, 90, x1, y1, false, true));
        movementTemplate.getElements().add(new LineTo(x2, y2));
        movementTemplate.getElements().add(new ArcTo(innerRadius, innerRadius, 90, x3, y3, false, false));
        movementTemplate.getElements().add(new LineTo(x0, y0));
        movementTemplate.setFill(Color.DARKGREEN);
        movementTemplate.setStroke(Color.WHITE);
        movementTemplate.setFillRule(FillRule.NON_ZERO);

        movementPath.getElements().add(new MoveTo(x - SHIP_TEMPLATE_WIDTH/2.0, y));
        movementPath.getElements().add(new LineTo(x, y));
        movementPath.getElements().add(new ArcTo(radius, radius, 90, x+radius, y+radius, false, true));
        movementPath.getElements().add(new LineTo(x+radius, y+radius + SHIP_TEMPLATE_WIDTH/2.0));
        movementPath.setStroke(Color.WHITE);
        movementPath.getStrokeDashArray().setAll(5d, 5d);

        return new MovementTemplate(movementPath, movementTemplate);
    }

    /**
     * Java main for when running without JavaFX launcher
     Perspective Camera
     Camera 3-5
     */
    public static void main(String[] args) {
        launch(args);
    }
}
