package dev.hermes.utils.mouse;

import javafx.scene.input.MouseEvent;

public class MouseUtil {

    public static boolean isMouseOver(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

}
