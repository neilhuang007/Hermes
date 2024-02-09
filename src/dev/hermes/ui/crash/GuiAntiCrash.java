package dev.hermes.ui.crash;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;

public class GuiAntiCrash extends GuiScreen {
    private final File crashReportDirectory;

    public GuiAntiCrash() {
        // Assume the crash reports directory is always located in the "crash-reports" folder within the Minecraft data directory.
        crashReportDirectory = new File(Minecraft.getMinecraft().mcDataDir, "crash-reports");
    }

    @Override
    public void initGui() {
        int buttonWidth = 200;
        int buttonHeight = 20;
        int buttonYPosition = this.height / 2 + 50;

        // Button ID 0 for returning to the main menu
        this.buttonList.add(new GuiButton(0, this.width / 2 - buttonWidth / 2, buttonYPosition, buttonWidth, buttonHeight, "Return to Main Menu"));

        // Button ID 1 for opening the crash reports directory
        this.buttonList.add(new GuiButton(1, this.width / 2 - buttonWidth / 2, buttonYPosition + buttonHeight + 5, buttonWidth, buttonHeight, "Open Crash Reports Directory"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            // Return to main menu
            this.mc.displayGuiScreen(new GuiMainMenu());
        } else if (button.id == 1) {
            // Open the crash reports directory
            try {
                Desktop.getDesktop().open(crashReportDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        drawCenteredString(this.fontRendererObj, "Your game has crashed!", this.width / 2, 100, 0xFFFFFF);
        drawCenteredString(this.fontRendererObj, "Press the button below to return to the main menu or open the crash logs directory.", this.width / 2, 120, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
