package dev.hermes.ui.alt.impl;


import com.mojang.realmsclient.gui.ChatFormatting;
import dev.hermes.Hermes;
import dev.hermes.ui.alt.GuiAccountManager;
import dev.hermes.ui.alt.account.Account;
import dev.hermes.ui.alt.account.MicrosoftLogin;
import dev.hermes.utils.SkinUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiMicrosoftLogin extends GuiScreen {
    private final GuiAccountManager manager;

    private AuthThread loginThread;

    private String status = EnumChatFormatting.GRAY + "Idle...";

    public GuiMicrosoftLogin(GuiAccountManager manager) {
        this.manager = manager;
    }


    private MicrosoftLogin.LoginData loginWithRefreshToken(String refreshToken) {
        final MicrosoftLogin.LoginData loginData = MicrosoftLogin.login(refreshToken);
        mc.session = new Session(loginData.username, loginData.uuid, loginData.mcToken, "microsoft");
        return loginData;
    }
    String username;
    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                // sets the status
                status.equals(ChatFormatting.RED + "Do not hit back!" + ChatFormatting.AQUA + " Logging in...");
                MicrosoftLogin.getRefreshToken(refreshToken -> {
                    if (refreshToken != null) {
                        new Thread(() -> {
                            // logging in
                            MicrosoftLogin.LoginData loginData = loginWithRefreshToken(refreshToken);
                            Account account = new Account(loginData.username, SkinUtil.uuidOf(loginData.username),loginData.newRefreshToken);
                            //account.setUsername(loginData.username);
                            //account.setRefreshToken(loginData.newRefreshToken); // TODO: THIS IS IMPORTANT
                            Hermes.accountManager.getAccounts().add(account);
                            // writes the file
                            Hermes.accountManager.get("alts").write();
                            String username = loginData.username;
                            //System.out.println(loginData.username + " " + SkinUtil.uuidOf(loginData.username) + " " + loginData.newRefreshToken)
                            // updates the status on the main gui
                            loginThread.setStatus(ChatFormatting.GREEN + "Succesfully logged in as" + loginData.username);
                        }).start();
                    }
                });
                // updates the status
                status.equals(ChatFormatting.GREEN + "succesfully logged in as " + username);

                break;
            case 1:
                mc.displayGuiScreen(manager);
        }
    }

    @Override
    public void drawScreen(int i, int j, float f) {
        int var3 = height / 4 + 24;
        drawDefaultBackground();
        Minecraft.getMinecraft().fontRendererObj.drawCenteredString( "Alt Login", width / 2, 20, -1);
        Minecraft.getMinecraft().fontRendererObj.drawCenteredString(status, width / 2, 29, -1);
        super.drawScreen(i, j, f);
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        int var3 = height / 4 + 24;
        buttonList.add(new GuiButton(0, width / 2 - 96, var3 + 72 + 12, "Login"));
        buttonList.add(new GuiButton(1, width / 2 - 96, var3 + 72 + 12 + 24, "Back"));
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        if (par1 == '\r') {
            actionPerformed(buttonList.get(0));
        }
    }

    @Override
    public void mouseClicked(int par1, int par2, int par3) {
        try {
            super.mouseClicked(par1, par2, par3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
