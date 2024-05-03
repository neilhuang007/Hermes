package dev.hermes.manager;

import dev.hermes.api.Hermes;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.*;


@Hermes
public class RenderManager extends Manager{

    public static Map<String, Shape> shapesMap = new HashMap<>();

    public static Map<String, Shape> buttonsMap = new HashMap<>();

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

    private static double lastwidth = -1;
    private static double lastheight = -1;

    public static double mouseX = 0;
    public static double mouseY = 0;

    public static double deltaMouseX = 0;
    public static double deltaMouseY = 0;

    public static boolean takeover = false;



    // A reference to the current GUI
    public static GuiScreen currentGui;


    private static JFrame frame; // Add this line


    public static boolean isdrawinggui = false;

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
                    if(Display.getX() != lastX || Display.getY() != lastY || Display.getHeight() != lastheight || Display.getWidth() != lastwidth) {
                        lastX = Display.getX();
                        lastY = Display.getY();
                        lastwidth = Display.getWidth();
                        lastheight = Display.getHeight();
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
            Shape shape = entry.getValue();
            shape.setMouseTransparent(true); // Ensure the shape is not interactive
            if (!modifiedidbuffer.contains(id)) {
                // Directly remove from the iterator and root to avoid concurrent modification issues
                Platform.runLater(() -> root.getChildren().remove(entry.getValue()));
                iterator.remove(); // Removes from shapesMap safely
            }
        }
        modifiedidbuffer.clear(); // Clear the buffer for the next check

        if(isdrawinggui){
            if(!isopen){
                isopen = true;
            }
        }else if(mc.theWorld != null && mc.thePlayer != null && mc.currentScreen == null){
            // means ingame
            if(!isopen){
                isopen = true;
            }
        }else{
            isopen = false;
        }
//        rectangle("capture",0,0, root.getWidth(), root.getHeight(), new java.awt.Color(0,0,0));
    }


    // Initialize the overlay window
    public void initwindow() {
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        double width = scaledResolution.getScaledWidth_double();
        double height = scaledResolution.getScaledHeight_double();
        createOverlayWindow(width, height);
        lastX = Display.getX();
        lastY = Display.getY();
        CheckWindowPosition();
        updateWindowLocation();
        setupAnimationTimer();

        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        Mouse.setGrabbed(false);

    }


    // Create the overlay window
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


            scene.setOnMouseMoved(event -> {
                if(takeover){
                    if(isopen){
                        // means ingame

                        mouseX = event.getSceneX() / scaledResolution.getScaleFactor();
                        mouseY = event.getSceneY() / scaledResolution.getScaleFactor();

                        deltaMouseX = (event.getSceneX() - ((double) Display.getWidth() / 2)) * scaledResolution.getScaleFactor();
                        deltaMouseY = (-(event.getSceneY() - Display.getHeight() / 2) + 1) * scaledResolution.getScaleFactor();
                        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
                        Mouse.setGrabbed(true);
                        System.out.println("deltaX: " + deltaMouseX + " deltaY: " + deltaMouseY);
                    }
                }
            });

            scene.setOnMouseEntered(event -> {
                takeover = true;
                System.out.println("Taking Over mouse control");
            });

            scene.setOnMouseExited(event -> {
                takeover = false;
                System.out.println("released mouse control");
                if(mc.theWorld != null && mc.thePlayer != null && mc.currentScreen == null){
                    Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
                    Mouse.setGrabbed(true);
                }else{
                    Mouse.setGrabbed(false);
                }
            });

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
                    primaryStage.setX(Display.getX() + 8);
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

    public static void setupGuiRefresher(GuiScreen guiScreen) {

        TimerTask refresher = new TimerTask() {

            @Override
            public void run() {
                if(isdrawinggui){
                    guiScreen.drawScreen(Mouse.getX(),Mouse.getY(),mc.timer.renderPartialTicks);
                }else {
                    animationTimer.stop();
                }
            }
        };
        refresher.run();
    }

    public static void CloseOverlayGuiScreen(){
        isdrawinggui = false;
        buttonsMap.clear();
    }

    public static void createUIButton(String id, double x, double y, double width, double height, Runnable action) {
        Platform.runLater(() -> {
            Rectangle button = new Rectangle(x, y, width, height);
            button.setFill(Color.TRANSPARENT); // Or any visually appropriate color

            // Set an action to be performed when the rectangle is clicked
            button.setOnMouseClicked(event -> {
                System.out.println(id);
                action.run(); // Execute the associated action
            });

            buttonsMap.put(id, button); // Optionally store the rectangle if needed for future reference
            root.getChildren().add(button); // Add the rectangle to the scene
        });
    }





    public static void roundedRectangle(String id, final double x, final double y, final double width, final double height, final double arcWidth, final double arcHeight, final Color color) {
        if(isopen){
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
    }

    public static void roundedRectangle(String id, final double x, final double y, final double width, final double height, final double arcWidth, final double arcHeight, final java.awt.Color color) {
        if(isopen){
            Platform.runLater(() -> {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                final double factor = scaledResolution.getScaleFactor();
                Rectangle roundedRectangle = (Rectangle) shapesMap.get(id);

                Color fillcolor = convertColor(color);
                if (roundedRectangle == null) {
                    roundedRectangle = new Rectangle(x * factor, y * factor, width * factor, height * factor);
                    roundedRectangle.setArcWidth(arcWidth * factor);
                    roundedRectangle.setArcHeight(arcHeight * factor);
                    roundedRectangle.setFill(fillcolor);
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
                    roundedRectangle.setFill(fillcolor);
                }
                modifiedidbuffer.add(id);
            });
        }
    }




    public static void horizontalGradient(String id, final double x, final double y, final double width, final double height, final java.awt.Color leftColor, final java.awt.Color rightColor) {
        if(isopen){
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
    }


    public static void rectangle(String id, final double x, final double y, final double width, final double height, final java.awt.Color color) {
        if(isopen){
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

    }

    public static void verticalGradient(String id, final double x, final double y, final double width, final double height, final java.awt.Color topColor, final java.awt.Color bottomColor) {
        if(isopen){
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
        if(isopen){
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
    public static void drawString(String id, String str, double x, double y, java.awt.Color color) {
        if(isopen){
            Platform.runLater(() -> {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                final double factor = scaledResolution.getScaleFactor();
                Text text = (Text) shapesMap.get(id);
                if (text == null) {
                    text = new Text(x*factor, y*factor, str);
                    shapesMap.put(id, text);
                    root.getChildren().add(text);
                } else {
                    text.setX(x*factor);
                    text.setY(y*factor);
                    text.setText(str);
                }
                text.setFill(convertColor(color)); // Set color
                // Set default font
                text.setFont(javafx.scene.text.Font.font("Arial", 14));
            });
        }
    }

    public static void drawString(String id, String str, double x, double y, java.awt.Color color, Font font) {
        if(isopen){
            Platform.runLater(() -> {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                final double factor = scaledResolution.getScaleFactor();
                Text text = (Text) shapesMap.get(id);
                if (text == null) {
                    text = new Text(x*factor, (y + (font.getSize()/2))*factor, str);
                    shapesMap.put(id, text);
                    root.getChildren().add(text);
                } else {
                    text.setX(x*factor);
                    text.setY((y + (font.getSize()/2))*factor);
                    text.setText(str);
                }
                text.setFont(javafx.scene.text.Font.font(font.getFontName(), font.getSize())); // Set font
                text.setFill(convertColor(color)); // Set color
            });
        }
    }


}