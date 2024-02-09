package dev.hermes.manager;

import dev.hermes.event.EventTarget;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.Sys;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import java.util.*;
import java.util.stream.Collectors;


public class RenderManager {

    public static Map<String, Shape> shapesMap = new HashMap<>();


    public static List<String> modifiedidbuffer = new ArrayList<>(); // Corrected initialization


    private static final Frustum FRUSTUM = new Frustum();
    private static final Pane root = new Pane(); // Static reference to the root pane
    private static Stage primaryStage; // Static reference to the primary stage

    private static AnimationTimer animationTimer;
    private static Scene scene; // Static reference to the scene
    private static Minecraft mc = Minecraft.getMinecraft();
    private static ScaledResolution scaledResolution = new ScaledResolution(mc);
    private static boolean isopen = true;
    private static double lastX = -1;
    private static double lastY = -1;

    static {
        new JFXPanel(); // Initialize JavaFX toolkit
    }


    // Check the window position every second
    public static void CheckWindowPosition() {
        Timer timer = new Timer();
        TimerTask checkPositionTask = new TimerTask() {
            @Override
            public void run() {
                if(isopen){
                    if(Display.getX() != lastX || Display.getY() != lastY) {
                        lastX = Display.getX();
                        lastY = Display.getY();
                        updateWindowLocation();
                    }
                }
            }
        };

        // Schedule the task to run once every second (1000 milliseconds)
        timer.scheduleAtFixedRate(checkPositionTask, 0, 1000);
    }


    public static void setupAnimationTimer() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Platform.runLater(() -> {
                    CheckCanvas(); // Ensure this is called to check and render shapes
                    // Possibly other periodic tasks
                });
            }
        };
        animationTimer.start();
    }

    public static void CheckCanvas() {
        // Iterates over the map using an iterator to avoid ConcurrentModificationException
        Iterator<Map.Entry<String, Shape>> iterator = shapesMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Shape> entry = iterator.next();
            String id = entry.getKey();
            if (!modifiedidbuffer.contains(id)) {
                // Directly remove from the iterator and root to avoid concurrent modification issues
                Platform.runLater(() -> root.getChildren().remove(entry.getValue()));
                iterator.remove(); // Removes from shapesMap safely
            }
        }
        modifiedidbuffer.clear(); // Clear the buffer for the next check

        if(mc.theWorld != null && mc.thePlayer != null && mc.currentScreen == null){
            mc.setIngameFocus();

        }

    }




    // Determine if the overlay should be displayed based on game state
    private static boolean shouldDisplayOverlay() {
        GuiScreen currentScreen = mc.currentScreen;
        return mc.theWorld != null && mc.thePlayer != null;
//              && !(currentScreen instanceof GuiIngameMenu || currentScreen instanceof GuiWinGame || currentScreen instanceof GuiGameOver);
    }

    // Initialize the overlay window
    public static void init() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        double width = scaledResolution.getScaledWidth_double();
        double height = scaledResolution.getScaledHeight_double();
        createOverlayWindow(width, height);
        lastX = Display.getX();
        lastY = Display.getY();
        CheckWindowPosition();
        updateWindowLocation();
        setupAnimationTimer();
    }

    // Create the overlay window
    private static void createOverlayWindow(double width, double height) {

        Platform.runLater(() -> {
            System.out.println("started rendering");
            primaryStage = new Stage();
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            primaryStage.setAlwaysOnTop(true);
            System.out.println("Set always on top");

            scene = new Scene(root, width * scaledResolution.getScaleFactor(), height * scaledResolution.getScaleFactor());
            scene.setFill(Color.TRANSPARENT);
            System.out.println("Set scene");
            root.setMouseTransparent(true); // Make the root pane transparent to mouse events
            primaryStage.setScene(scene);
            System.out.println("Set stage");

            // Handle the close request by consuming the event, which prevents the window from being closed
            primaryStage.setOnCloseRequest(event -> event.consume());
            System.out.println("Set on close request");

            root.addEventFilter(MouseEvent.ANY, MouseEvent::consume);
            primaryStage.show();
        });
    }

    public static void updateWindowLocation() {
        Platform.runLater(() -> {
            if(isopen){
                if (primaryStage != null) {
                    ScaledResolution scaledResolution = new ScaledResolution(mc);
                    final int factor = scaledResolution.getScaleFactor();

                    final int titleBarHeightEstimate = 30; // Adjust this value as necessary

                    // Adjust window size and position
                    primaryStage.setX(Display.getX());
                    primaryStage.setY(Display.getY() + titleBarHeightEstimate);
                    // Here, subtract the estimated title bar height from the height calculation
                    primaryStage.setWidth(scaledResolution.getScaledWidth_double() * factor);
                    primaryStage.setHeight((scaledResolution.getScaledHeight_double() * factor));

                    // Reinforce root size after clearing its children
                    root.setPrefSize(scaledResolution.getScaledWidth() * factor, scaledResolution.getScaledHeight() * factor);
                }
            }
        });
    }




    public static void roundedRectangle(String id, final double x, final double y, final double width, final double height, final double arcWidth, final double arcHeight, final Color color) {
        Platform.runLater(() -> {
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            final double factor = scaledResolution.getScaleFactor();
            Rectangle roundedRectangle = (Rectangle) shapesMap.get(id);

            if (roundedRectangle == null) {
                roundedRectangle = new Rectangle(x * factor, y * factor, width * factor, height * factor);
                roundedRectangle.setArcWidth(arcWidth * factor);
                roundedRectangle.setArcHeight(arcHeight * factor);
                roundedRectangle.setFill(color);
                shapesMap.put(id, roundedRectangle);
                roundedRectangle.setMouseTransparent(true);
                root.getChildren().add(roundedRectangle);
            } else {
                // Update properties for existing rectangle
                roundedRectangle.setX(x * factor);
                roundedRectangle.setY(y * factor);
                roundedRectangle.setWidth(width * factor);
                roundedRectangle.setHeight(height * factor);
                roundedRectangle.setArcWidth(arcWidth * factor);
                roundedRectangle.setArcHeight(arcHeight * factor);
                roundedRectangle.setFill(color);
            }
            modifiedidbuffer.add(id);
        });
    }



    public static void horizontalGradient(String id, final double x, final double y, final double width, final double height, final java.awt.Color leftColor, final java.awt.Color rightColor) {
        Platform.runLater(() -> {
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            final double factor = scaledResolution.getScaleFactor();
            Rectangle rect = (Rectangle) shapesMap.get(id);

            if (rect == null) {
                rect = new Rectangle(x * factor, y * factor, width * factor, height * factor);
                shapesMap.put(id, rect);
                rect.setMouseTransparent(true);
                root.getChildren().add(rect);
            }

            javafx.scene.paint.Color fxLeftColor = convertColor(leftColor);
            javafx.scene.paint.Color fxRightColor = convertColor(rightColor);
            LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, null, new Stop(0, fxLeftColor), new Stop(1, fxRightColor));
            rect.setFill(gradient);

            rect.setX(x * factor);
            rect.setY(y * factor);
            rect.setWidth(width * factor);
            rect.setHeight(height * factor);

            modifiedidbuffer.add(id);
        });
    }


    public static void rectangle(String id, final double x, final double y, final double width, final double height, final java.awt.Color color) {
        Platform.runLater(() -> {
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            final double factor = scaledResolution.getScaleFactor();
            Rectangle rect = (Rectangle) shapesMap.get(id);

            if (rect == null) {
                rect = new Rectangle(x * factor, y * factor, width * factor, height * factor);
                shapesMap.put(id, rect);
                rect.setMouseTransparent(true);
                root.getChildren().add(rect);
            } else {
                rect.setX(x * factor);
                rect.setY(y * factor);
                rect.setWidth(width * factor);
                rect.setHeight(height * factor);
            }
            rect.setFill(convertColor(color));
            modifiedidbuffer.add(id);
        });
    }

    public static void verticalGradient(String id, final double x, final double y, final double width, final double height, final java.awt.Color topColor, final java.awt.Color bottomColor) {
        Platform.runLater(() -> {
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            final double factor = scaledResolution.getScaleFactor();
            Rectangle rect = (Rectangle) shapesMap.get(id);

            if (rect == null) {
                rect = new Rectangle(x * factor, y * factor, width * factor, height * factor);
                shapesMap.put(id, rect);
                rect.setMouseTransparent(true);
                root.getChildren().add(rect);

            } else {
                rect.setX(x * factor);
                rect.setY(y * factor);
                rect.setWidth(width * factor);
                rect.setHeight(height * factor);
            }

            javafx.scene.paint.Color fxTopColor = convertColor(topColor);
            javafx.scene.paint.Color fxBottomColor = convertColor(bottomColor);
            LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, fxTopColor), new Stop(1, fxBottomColor));
            rect.setFill(gradient);

            modifiedidbuffer.add(id);
        });
    }


    public static boolean isInViewFrustrum(final Entity entity) {
        return (isInViewFrustrum(entity.getEntityBoundingBox()) || entity.ignoreFrustumCheck);
    }

    private static boolean isInViewFrustrum(final AxisAlignedBB bb) {
        final Entity current = mc.getRenderViewEntity();
        FRUSTUM.setPosition(current.posX, current.posY, current.posZ);
        return FRUSTUM.isBoundingBoxInFrustum(bb);
    }

    private static javafx.scene.paint.Color convertColor(java.awt.Color color) {
        return new javafx.scene.paint.Color(
                color.getRed() / 255.0,
                color.getGreen() / 255.0,
                color.getBlue() / 255.0,
                color.getAlpha() / 255.0
        );
    }

    // Convert ARGB integer color to JavaFX Color
    private static Color convertARGBtoColor(int argb) {
        double alpha = (argb >> 24 & 0xFF) / 255.0;
        double red = (argb >> 16 & 0xFF) / 255.0;
        double green = (argb >> 8 & 0xFF) / 255.0;
        double blue = (argb & 0xFF) / 255.0;
        return new Color(red, green, blue, alpha);
    }

    // Adapted drawBorderedRect for JavaFX
    public static void drawBorderedRect(String id, double x, double y, double width, double height, double lineSize, int borderColor, int fillColor) {
        Platform.runLater(() -> {
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            final double factor = scaledResolution.getScaleFactor();

            // Main rectangle
            Rectangle mainRect = (Rectangle) shapesMap.computeIfAbsent(id, k -> new Rectangle());
            mainRect.setX(x * factor);
            mainRect.setY(y * factor);
            mainRect.setWidth(width * factor);
            mainRect.setHeight(height * factor);
            mainRect.setFill(convertARGBtoColor(fillColor));
            if (!root.getChildren().contains(mainRect)) {
                mainRect.setMouseTransparent(true);
                root.getChildren().add(mainRect);
            }

            // Border rectangles
            String[] borderIds = {id + "_top", id + "_left", id + "_right", id + "_bottom"};
            double[][] borderDimensions = {
                    {x, y, width, lineSize}, // Top
                    {x, y, lineSize, height}, // Left
                    {(x + width - lineSize), y, lineSize, height}, // Right
                    {x, (y + height - lineSize), width, lineSize} // Bottom
            };

            for (int i = 0; i < borderIds.length; i++) {
                Rectangle borderRect = (Rectangle) shapesMap.computeIfAbsent(borderIds[i], k -> new Rectangle());
                borderRect.setX(borderDimensions[i][0] * factor);
                borderRect.setY(borderDimensions[i][1] * factor);
                borderRect.setWidth(borderDimensions[i][2] * factor);
                borderRect.setHeight(borderDimensions[i][3] * factor);
                borderRect.setFill(convertARGBtoColor(borderColor));
                if (!root.getChildren().contains(borderRect)) {
                    borderRect.setMouseTransparent(true);
                    root.getChildren().add(borderRect);
                }
            }

            modifiedidbuffer.add(id);
            for (String borderId : borderIds) {
                modifiedidbuffer.add(borderId);
            }
        });
    }


}
