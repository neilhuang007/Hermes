import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;



public class TransparentWindow extends Application {

    @Override
    public void start(Stage primaryStage) {
        setTransparent("Hermes Renderer");



    }

    private static void setTransparent(String windowTitle) {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, windowTitle);
        System.out.println(hwnd);
        int extendedStyle = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
        int newExtendedStyle = extendedStyle | WinUser.WS_EX_LAYERED;
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, newExtendedStyle);
        User32.INSTANCE.SetLayeredWindowAttributes(hwnd, 0x00000000, (byte) 0, WinUser.LWA_COLORKEY);
        newExtendedStyle = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
        newExtendedStyle |= WinUser.WS_EX_TRANSPARENT;
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, newExtendedStyle);
    }




    public static void main(String[] args) {
        launch(args);
    }
}