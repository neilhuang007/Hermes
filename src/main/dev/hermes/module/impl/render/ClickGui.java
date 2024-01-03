package dev.hermes.module.impl.render;

import dev.hermes.module.Module;
import dev.hermes.module.ModuleType;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class ClickGui extends Module {


    Thread Guithread;


    JFXPanel fxPanel;



    public ClickGui() {
        super("ClickGui", ModuleType.Render);
    }


    @Override
    public void onDisable(){
        if(Guithread != null && Guithread.isAlive()){
            Platform.runLater(() -> {
                if (fxPanel != null) {
                    fxPanel.setScene(null);
                }
                // Other cleanup code if needed
            });
            Platform.exit();
            Guithread.stop();
            Guithread=null;
        }
    }

    @Override
    public void onEnable(){
        //gui thread
        Guithread = new Thread(() -> {
            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Swing and JavaFX");
                final JFXPanel fxPanel = new JFXPanel();
                initFX(fxPanel);
                // javafx thread
                Platform.runLater(() -> {
                    // This method is invoked on the EDT thread
                    frame.add(fxPanel);
                    frame.setSize(800, 500);
                    frame.setVisible(true);
                    frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

                    // Add a WindowAdapter to detect window closing event
                    frame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosing(WindowEvent e) {
                            System.out.println("=================================================");
                            System.out.println("window closed");
                            System.out.println("=================================================");

                            ClickGui.this.toggle();
                        }
                    });
                });
            });
        });
        Guithread.start();
    }

    private static void initFX(JFXPanel fxPanel) {
        SwingUtilities.invokeLater(() -> {
            Platform.runLater(() -> {
                // This method is invoked on the JavaFX thread
                WebView webView = new WebView();
                loadHTMLContent(webView);
                fxPanel.setScene(new Scene(webView));
            });
        });

    }

    private static void loadHTMLContent(WebView webView) {
        URL htmlUrl = ClickGui.class.getClassLoader().getResource("dev/hermes/ui/index.html");
        if (htmlUrl != null) {
            webView.getEngine().load(htmlUrl.toExternalForm());
        } else {
            System.err.println("HTML file not found");
        }
    }

}
