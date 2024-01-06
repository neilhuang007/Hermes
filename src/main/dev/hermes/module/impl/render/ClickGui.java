package dev.hermes.module.impl.render;

import dev.hermes.module.Module;
import dev.hermes.module.Catagory;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.lwjgl.input.Keyboard;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class ClickGui extends Module {


    Thread Guithread;

    JFXPanel fxPanel;

    public ClickGui() {
        super("ClickGui", "GUI for the Client",Catagory.Render, Keyboard.KEY_RSHIFT);
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

    private static void injectConsoleListener(WebEngine webEngine) {
        // Get the console object from the WebView
        JSObject window = (JSObject) webEngine.executeScript("window");
        JSObject console = (JSObject) window.getMember("console");

        // Set up a listener for the console.log method
        console.setMember("log", new ConsoleListener());
    }

    // Listener class to capture console.log messages
    public static class ConsoleListener {
        public void log(String message) {
            // Output the log message to your Java console or logs
            System.out.println("JavaScript Console Log: " + message);
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
                WebEngine webEngine = webView.getEngine();

                // Attach a listener to the console in the WebView
                webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue == Worker.State.SUCCEEDED) {
                        injectConsoleListener(webEngine);
                    }
                    if (newValue == Worker.State.FAILED) {
                        System.out.println("WebView loading failed with error: " + webEngine.getLoadWorker().getMessage());
                    }
                });

                loadHTMLContent(webView);
                fxPanel.setScene(new Scene(webView));
            });
        });

    }

    private static void loadHTMLContent(WebView webView) {
        URL htmlUrl = ClickGui.class.getClassLoader().getResource("assets/hermes/ui/index.html");
        if (htmlUrl != null) {
            webView.getEngine().load(htmlUrl.toExternalForm());
        } else {
            System.err.println("HTML file not found");
        }
    }



}
