import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.RejectedExecutionException;

public class TransparentBlurredWindow extends Application {
    private static final int BLUR_RADIUS = 10;

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();

        Scene scene = new Scene(root, 400, 300, Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.setFill(new Color(0,0,0,0.5));
        primaryStage.setAlwaysOnTop(true);
        ColorAdjust adj = new ColorAdjust(0, -0.9, -0.5, 0);
        GaussianBlur blur = new GaussianBlur(55); // 55 is just to show edge effect more clearly.
        adj.setInput(blur);
        root.setEffect(adj);
    }

    private BufferedImage captureScreenBehindWindow(Stage stage) throws AWTException {
        Rectangle screenRect = new Rectangle((int) stage.getX(), (int) stage.getY(), (int) stage.getWidth(), (int) stage.getHeight());
        Robot robot = new Robot();
        return robot.createScreenCapture(screenRect);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
