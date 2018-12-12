package colorpixelreader.FX;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sun.awt.image.ToolkitImage;

public class ColorPixelReader extends Application implements Runnable {

    BufferedImage scrn;
    Color color;
    Robot robot;

    Stage stage;
    Scene scene;
    StackPane root;
    ImageView labelView;
    ImageView imgV;
    final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);
    Thread t;

    Point p = MouseInfo.getPointerInfo().getLocation();

    public ColorPixelReader() throws AWTException {

        imgV = new ImageView();
        imgV.setFitHeight(zoomProperty.get() * 4);
        imgV.setFitWidth(zoomProperty.get() * 4);
        robot = new Robot();
        t = new Thread(this);
        t.start();

    }

    private void loop() {

    }

    @Override
    public void run() {

        while (true) {
            p = MouseInfo.getPointerInfo().getLocation();
            color = robot.getPixelColor(p.x, p.y);

            System.out.println(color);

            imgV.setImage(getScreenshot(p, 250));

        }

    }

    private javafx.scene.image.Image getScreenshot(Point p, int size) {
        Point newP = p;

        newP.x = newP.x - size / 2;
        newP.y = newP.y - size / 2;
        javafx.scene.image.Image cap
                = SwingFXUtils.toFXImage(
                        robot.createScreenCapture(new Rectangle(newP, new Dimension(size, size))), null);
//cap.
//        Graphics g = cap.getGraphics();
//        g.drawRect(size / 2 - 5, size / 2 - 5, 10, 10);
//        g.dispose();
//        BufferedImage temp = (BufferedImage) cap.getScaledInstance(size * 2, size * 2, Image.SCALE_FAST);
        return cap;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        ColorPixelReader cp = new ColorPixelReader();

        stage = primaryStage;

        root = new StackPane();

        root.getChildren().add(imgV);

        labelView = new ImageView();
        root.getChildren().add(labelView);

        scene = new Scene(root, 250, 250);

        stage.setScene(scene);
        stage.show();

        cp.loop();

    }

    public static void main(String[] args) throws AWTException {

        launch(args);

    }

}
