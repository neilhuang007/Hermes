package dev.hermes.ui.alt;


import com.mojang.realmsclient.gui.ChatFormatting;
import dev.hermes.Hermes;

import dev.hermes.ui.alt.account.Account;
import dev.hermes.ui.alt.impl.AuthThread;
import dev.hermes.ui.alt.impl.GuiAddAccount;
import dev.hermes.ui.alt.impl.GuiMicrosoftLogin;
import dev.hermes.ui.alt.impl.GuiRenameAccount;
import dev.hermes.utils.TimerUtil;
import dev.hermes.utils.renderer.IngameRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

public class GuiAccountManager extends GuiScreen {
    private GuiButton login;
    private GuiButton remove;
    private GuiButton rename;
    private GuiButton random;
    public AuthThread loginThread;
    private int offset;
    public Account selectedAlt = null;
    public String status = ChatFormatting.GRAY + "Idle...";

    TimerUtil watch = new TimerUtil();

    @Override
    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                if (loginThread == null) mc.displayGuiScreen(null);
                else if ((!loginThread.getStatus().equals(ChatFormatting.AQUA + "Logging in...")) && (!loginThread.getStatus().equals(ChatFormatting.RED + "Do not hit back!" + ChatFormatting.AQUA + " Logging in..."))) {
                    mc.displayGuiScreen(null);
                } else {
                    loginThread.setStatus(ChatFormatting.RED + "Do not hit back!" + ChatFormatting.AQUA + " Logging in...");
                }
                break;
            case 1:
                String user = selectedAlt.getUsername();
                String pass = selectedAlt.getPassword();
                String refreshtoken = selectedAlt.getRefreshToken();
                String accounttype = selectedAlt.getAccountType();
                loginThread = new AuthThread(user, pass, refreshtoken, accounttype);
                loginThread.start();
                break;
            case 2:
                if (loginThread != null) {
                    loginThread = null;
                }
                Hermes.accountManager.getAccounts().remove(selectedAlt);
                setStatus("\247aRemoved");
                Hermes.accountManager.get("alts").write();

                selectedAlt = null;
                break;
            case 3:
                mc.displayGuiScreen(new GuiAddAccount(this));
                break;
            case 4:
                mc.displayGuiScreen(new GuiMicrosoftLogin(this));
                break;
            case 5:
                Account randomAlt = Hermes.accountManager.getAccounts().get(new java.util.Random().nextInt(Hermes.accountManager.getAccounts().size()));
                String user1 = randomAlt.getUsername();
                String pass1 = randomAlt.getPassword();
                String rt = randomAlt.getRefreshToken();
                String at = randomAlt.getAccountType();
                loginThread = new AuthThread(user1, pass1,rt,at);
                loginThread.start();
                break;
            case 6:
                mc.displayGuiScreen(new GuiRenameAccount(this));
                break;
            case 7:
                if (!Hermes.accountManager.getAccounts().isEmpty()) {
                    Hermes.accountManager.getAccounts().clear();
                    Hermes.accountManager.get("alts").write();
                }
                break;
            case 8:
                Hermes.accountManager.getAccounts().clear();
                Hermes.accountManager.get("alts").read();
                setStatus("\247bReloaded!");
                break;
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {

        if (Mouse.hasWheel()) {
            int wheel = Mouse.getDWheel();
            if (wheel < 0) {
                offset += 26;
                if (offset < 0) {
                    offset = 0;
                }
            } else if (wheel > 0) {
                offset -= 26;
                if (offset < 0) {
                    offset = 0;
                }
            }
        }
        drawDefaultBackground();
        Minecraft.getMinecraft().fontRendererObj.drawString(mc.session.getUsername(), 10, 10, 0xDDDDDD);
        Minecraft.getMinecraft().fontRendererObj.drawCenteredString("Account Manager - " + Hermes.accountManager.getAccounts().size() + " alts", width / 2, 10, -1);
        Minecraft.getMinecraft().fontRendererObj.drawCenteredString(loginThread == null ? status : loginThread.getStatus(), width / 2, 20, -1);
        IngameRenderer.drawBorderedRect(50.0F, 33.0F, width - 100, height - 90, 1.0F, new Color(25, 25, 25, 255).getRGB(), new Color(15, 15, 15, 255).getRGB());
        GL11.glPushMatrix();
        prepareScissorBox(0.0F, 33.0F, width, height - 50);
        GL11.glEnable(3089);
        int y = 38;

        for (Account alt : Hermes.accountManager.getAccounts()) {
            if (isAltInArea(y)) {
                String name;
                name = alt.getUsername();
                String pass;
                if ("CRACKED".equals(alt.getAccountType())) {
                    pass = "\247cCracked";
                } else {
                    pass = alt.getPassword().replaceAll(".", "*");
                }
                if (alt == selectedAlt) {
                    if ((isMouseOverAlt(par1, par2, y - offset)) && (Mouse.isButtonDown(0))) {
                        IngameRenderer.drawBorderedRect(52.0F, y - offset - 4, width - 104,   20, 1.0F, new Color(45, 45, 45, 255).getRGB(), -2142943931);
                    } else if (isMouseOverAlt(par1, par2, y - offset)) {
                        IngameRenderer.drawBorderedRect(52.0F, y - offset - 4, width - 104,   20, 1.0F, new Color(45, 45, 45, 255).getRGB(), -2142088622);
                    } else {
                        IngameRenderer.drawBorderedRect(52.0F, y - offset - 4, width - 104,   20, 1.0F, new Color(45, 45, 45, 255).getRGB(), -2144259791);
                    }
                } else if ((isMouseOverAlt(par1, par2, y - offset)) && (Mouse.isButtonDown(0))) {
                    IngameRenderer.drawBorderedRect(52.0F, y - offset - 4, width - 104,   20, 1.0F, -new Color(45, 45, 45, 255).getRGB(), -2146101995);
                } else if (isMouseOverAlt(par1, par2, y - offset)) {
                    IngameRenderer.drawBorderedRect(52.0F, y - offset - 4, width - 104,  20, 1.0F, new Color(45, 45, 45, 255).getRGB(), -2145180893);
                }
                Minecraft.getMinecraft().fontRendererObj.drawCenteredString(name, width / 2, y - offset, -1);
                Minecraft.getMinecraft().fontRendererObj.drawCenteredString(pass, width / 2, y - offset + 10, 5592405);
                y += 26;
            }
        }

        GL11.glDisable(3089);
        GL11.glPopMatrix();
        super.drawScreen(par1, par2, par3);
        if (selectedAlt == null) {
            login.enabled = false;
            remove.enabled = false;
            rename.enabled = false;
        } else {
            login.enabled = true;
            remove.enabled = true;
            rename.enabled = true;
        }
        if (Hermes.accountManager.getAccounts().isEmpty()) {
            random.enabled = false;
        } else {
            random.enabled = true;
        }
        if (Keyboard.isKeyDown(200)) {
            offset -= 26;
            if (offset < 0) {
                offset = 0;
            }
        } else if (Keyboard.isKeyDown(208)) {
            offset += 26;
            if (offset < 0) {
                offset = 0;
            }
        }
    }

    @Override
    public void initGui() {
        buttonList.add(new GuiButton(0, width / 2 + 116, height - 24, 75, 20, "Cancel"));
        buttonList.add(login = new GuiButton(1, width / 2 - 122, height - 48, 100, 20, "Login"));
        buttonList.add(remove = new GuiButton(2, width / 2 - 40, height - 24, 70, 20, "Remove"));
        buttonList.add(new GuiButton(3, width / 2 + 4 + 86, height - 48, 100, 20, "Add"));
        buttonList.add(new GuiButton(4, width / 2 - 16, height - 48, 100, 20, "Microsoft Login"));
        buttonList.add(random = new GuiButton(5, width / 2 - 122, height - 24, 78, 20, "Random"));
        buttonList.add(rename = new GuiButton(6, width / 2 + 38, height - 24, 70, 20, "Edit"));
        buttonList.add(new GuiButton(7, width / 2 - 190, height - 24, 60, 20, "Clear"));
        buttonList.add(new GuiButton(8, width / 2 - 190, height - 48, 60, 20, "Reload"));
        login.enabled = false;
        remove.enabled = false;
        rename.enabled = false;
        Hermes.accountManager.get("alts").read();
    }

    private boolean isAltInArea(int y) {
        return y - offset <= height - 60;
    }

    private boolean isMouseOverAlt(int x, int y, int y1) {
        return (x >= 52) && (y >= y1 - 4) && (x <= width - 52) && (y <= y1 + 20) && (x >= 0) && (y >= 33) && (x <= width) && (y <= height - 60);
    }

    @Override
    public void mouseClicked(int par1, int par2, int par3) {
        if (offset < 0) {
            offset = 0;
        }
        int y = 38 - offset;
        for (Account alt : Hermes.accountManager.getAccounts()) {
            if (isMouseOverAlt(par1, par2, y)) {
                if (alt == selectedAlt) {
                    actionPerformed(buttonList.get(1));
                    return;
                }
                selectedAlt = alt;
            }
            y += 26;
        }
        try {
            super.mouseClicked(par1, par2, par3);
        } catch (IOException e) {
        }
    }

    private void prepareScissorBox(float x, float y, float x2, float y2) {
        ScaledResolution scale = new ScaledResolution(mc);
        int factor = scale.getScaleFactor();
        GL11.glScissor((int) (x * factor), (int) ((scale.getScaledHeight() - y2) * factor), (int) ((x2 - x) * factor), (int) ((y2 - y) * factor));
    }

    public void setStatus(String status) {
        watch.resetTimer();
        this.status = status;
        if(watch.finished(5000)){
            loginThread.setStatus(ChatFormatting.GRAY + "Idle...");
        }
    }

}