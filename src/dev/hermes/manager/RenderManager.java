package dev.hermes.manager;

import dev.hermes.event.EventTarget;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.Display;


public class RenderManager {


    public static Thread Renderthread = new Thread();


    private static final Frustum FRUSTUM = new Frustum();

    private static final Pane root = new Pane(); // Static reference to the root pane
    private static Stage primaryStage; // Static reference to the primary stage

    private static Minecraft mc = Minecraft.getMinecraft();
    private static ScaledResolution scaledResolution = new ScaledResolution(mc);

    private static boolean isopen = false;


    @EventTarget
    public void Tick() {
        if(isopen = true){
//            System.out.println("tick");
            updateWindowLocation(Display.getX(), Display.getY());
            refreshcanvas();
        }
    }

    public static void init() {
        System.out.println("Render manager init");
        // Ensure JavaFX initialization
        new JFXPanel();
        Platform.runLater(() -> createOverlayWindow(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight()));
        isopen = true; // Consider setting this within the Platform.runLater() if it depends on successful UI initialization
    }

    private static void createOverlayWindow(double width, double height) {
        Platform.runLater(() -> {
            primaryStage = new Stage(); // Initialize the stage
            primaryStage.initStyle(StageStyle.TRANSPARENT); // Make the window transparent
            primaryStage.setAlwaysOnTop(true); // Set to always on top

            Scene scene = new Scene(root, width, height);
            scene.setFill(Color.TRANSPARENT);

            primaryStage.setScene(scene);
            primaryStage.show();
            System.out.println("Created overlay window");
        });
    }


    // Method to update the window location
    public static void updateWindowLocation(final double x, final double y) {
        // only draw if the guis are what we want
        Platform.runLater(() -> {
            if (primaryStage != null) {
                primaryStage.setX(x);
                primaryStage.setY(y);
                // Explicitly set the size of the stage or root pane to match scaled resolution, preventing shrinkage.
                primaryStage.setWidth(scaledResolution.getScaledWidth());
                primaryStage.setHeight(scaledResolution.getScaledHeight());
                primaryStage.show();
            }else{
                if(mc.currentScreen instanceof GuiMainMenu){
                    createOverlayWindow(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
                } else if (mc.thePlayer != null) {
                    createOverlayWindow(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
                }
                isopen = true;
            }
        });

    }

    public static void refreshcanvas(){
        Platform.runLater(() -> {
            root.getChildren().clear();
            // Ensure the root pane matches the scaled resolution, potentially after clearing its children
            root.setPrefSize(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        });
    }

    public static void roundedRectangle(final double x, final double y, final double width, final double height, final double arcWidth, final double arcHeight, final Color color) {
        if(isopen){
            Platform.runLater(() -> {
                Rectangle roundedRectangle = new Rectangle(x, y, width, height);
                roundedRectangle.setArcWidth(arcWidth);
                roundedRectangle.setArcHeight(arcHeight);
                roundedRectangle.setFill(color); // Use the provided color
                root.getChildren().add(roundedRectangle);
            });
        }
    }

    public static void horizontalGradient(final double x, final double y, final double width, final double height, final java.awt.Color leftColor, final java.awt.Color rightColor) {
        if(isopen){
            Platform.runLater(() -> {

                javafx.scene.paint.Color fxleftcolor = convertColor(leftColor);

                javafx.scene.paint.Color fxrightcolor = convertColor(rightColor);

                // Create the gradient
                LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, null, new Stop[]{new Stop(0, fxleftcolor), new Stop(1, fxrightcolor)});

                // Create and position the rectangle
                Rectangle rect = new Rectangle(x, y, width, height);
                rect.setFill(gradient);

                // Assuming 'root' is the parent node of your scene
                root.getChildren().add(rect);
            });
        }
    }

    public static void rectangle(final double x, final double y, final double width, final double height, final java.awt.Color color) {
        if (isopen) { // Check if the drawing operation should proceed
            Platform.runLater(() -> {
                // Convert java.awt.Color to javafx.scene.paint.Color
                javafx.scene.paint.Color fxColor = convertColor(color);

                // Create the rectangle with specified position, size, and converted color
                Rectangle rect = new Rectangle(x, y, width, height);
                rect.setFill(fxColor);

                // Add the rectangle to the scene's root node
                root.getChildren().add(rect);
            });
        }
    }

    // Drawing a vertical gradient rectangle
    public static void verticalGradient(final double x, final double y, final double width, final double height, final java.awt.Color topAwtColor, final java.awt.Color bottomAwtColor) {
        if (isopen) { // Proceed only if isOpen is true
            Platform.runLater(() -> {
                // Convert colors from AWT to JavaFX
                javafx.scene.paint.Color topColor = convertColor(topAwtColor);
                javafx.scene.paint.Color bottomColor = convertColor(bottomAwtColor);

                // Create the gradient
                LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, null,
                        new Stop[]{
                                new Stop(0, topColor),
                                new Stop(1, bottomColor)
                        });

                // Create and add the rectangle
                Rectangle rect = new Rectangle(x, y, width, height);
                rect.setFill(gradient);
                root.getChildren().add(rect); // Add the rectangle to the specified root pane
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
    public static void drawBorderedRect(double x, double y, double width, double height, double lineSize, int borderColor, int color) {
        if (isopen) {
            Platform.runLater(() -> {
                // Main rectangle
                Rectangle rect = new Rectangle(x, y, width, height);
                rect.setFill(convertARGBtoColor(color));
                root.getChildren().add(rect);

                // Border rectangles
                Rectangle topBorder = new Rectangle(x, y, width, lineSize);
                topBorder.setFill(convertARGBtoColor(borderColor));
                Rectangle leftBorder = new Rectangle(x, y, lineSize, height);
                leftBorder.setFill(convertARGBtoColor(borderColor));
                Rectangle rightBorder = new Rectangle(x + width - lineSize, y, lineSize, height);
                rightBorder.setFill(convertARGBtoColor(borderColor));
                Rectangle bottomBorder = new Rectangle(x, y + height - lineSize, width, lineSize);
                bottomBorder.setFill(convertARGBtoColor(borderColor));

                root.getChildren().addAll(topBorder, leftBorder, rightBorder, bottomBorder);
            });
        }
    }

}
