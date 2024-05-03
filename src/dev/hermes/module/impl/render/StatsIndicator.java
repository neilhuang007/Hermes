package dev.hermes.module.impl.render;

import dev.hermes.api.Hermes;
import dev.hermes.event.EventTarget;
import dev.hermes.event.events.impl.render.EventRender3D;
import dev.hermes.manager.RenderManager;
import dev.hermes.module.Module;
import dev.hermes.module.api.Category;
import dev.hermes.module.api.ModuleInfo;
import dev.hermes.module.value.impl.DragValue;
import dev.hermes.utils.vector.Vector2d;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Hermes
@ModuleInfo(name = "Stats Indicator", description = "Shows stats on the screen", category = Category.RENDER)
public class StatsIndicator extends Module {

    DragValue position = new DragValue("Position", this, new Vector2d(0, 0), true, true);

    HashMap<String, Integer> playerStars = new HashMap<>();
    HashMap<String, Double> playerFkdr = new HashMap<>();
    HashMap<String, Double> playerWinLossRate = new HashMap<>();
    HashMap<String, Integer> playerFinalKills = new HashMap<>();
    HashMap<String, Integer> playerWins = new HashMap<>();

    // new List for player names
    List<String> playerNames = new ArrayList<>();

    @Override
    public void onEnable() {
        addTestData();
    }

    @EventTarget
    public void onRender(EventRender3D event) {
        RenderManager.roundedRectangle("statsoverlay_bg", position.getValue().getX(), position.getValue().getY(), 350, 200, 10,10,new Color(0, 0, 0, 0.2));

        // Define the column headers
        String[] headers = {"Player Name", "Stars", "FKDR", "WLR", "Final Kills", "Wins"};

        // Define the starting position for the spreadsheet
        double startX = position.getValue().getX() + 10;
        double startY = position.getValue().getY() + 10;

//        // Draw the column headers
//        for (int i = 0; i < headers.length; i++) {
//            RenderManager.drawString("header_" + i, headers[i], startX + i * 100, startY + 20); // Increased gap between categories
//        }

        // Draw each row of the spreadsheet
        for (int i = 0; i < playerNames.size(); i++) {
            String playerName = playerNames.get(i);
            String[] stats = {
                    playerName,
                    String.valueOf(playerStars.get(playerName)),
                    String.valueOf(playerFkdr.get(playerName)),
                    String.valueOf(playerWinLossRate.get(playerName)),
                    String.valueOf(playerFinalKills.get(playerName)),
                    String.valueOf(playerWins.get(playerName))
            };

//            for (int j = 0; j < stats.length; j++) {
//                RenderManager.drawString(playerName + "_stat_" + j, stats[j], startX + j * 70, startY + 40 + (i + 1) * 20); // Increased gap between categories
//            }
        }
    }

    public void addTestData() {
        // Add some test data
        playerNames.add("TestPlayer1");
        playerStars.put("TestPlayer1", 5);
        playerFkdr.put("TestPlayer1", 1.5);
        playerWinLossRate.put("TestPlayer1", 2.0);
        playerFinalKills.put("TestPlayer1", 10);
        playerWins.put("TestPlayer1", 20);

        playerNames.add("TestPlayer2");
        playerStars.put("TestPlayer2", 4);
        playerFkdr.put("TestPlayer2", 1.2);
        playerWinLossRate.put("TestPlayer2", 1.8);
        playerFinalKills.put("TestPlayer2", 8);
        playerWins.put("TestPlayer2", 15);
    }
}
