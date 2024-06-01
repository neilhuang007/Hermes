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
        setTransparent(true,"Transparent Blurred Window");
    }

    public static void setTransparent(boolean transparent, String windowTitle) {
        WinDef.HWND hwnd = User32.INSTANCE.FindWindow(null, windowTitle);
        int wl = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE);
        if (transparent) {
            // 设置窗口为透明
            wl |= WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT;
        } else {
            // 清除透明样式，允许窗口接收绘制消息
            wl &= ~(WinUser.WS_EX_LAYERED | WinUser.WS_EX_TRANSPARENT);
        }
        User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, wl);
    }




    public static void main(String[] args) {
        launch(args);
    }
}