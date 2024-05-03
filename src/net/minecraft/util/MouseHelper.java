package net.minecraft.util;

import dev.hermes.manager.RenderManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class MouseHelper
{
    public int deltaX;
    public int deltaY;

    public void grabMouseCursor()
    {
        Mouse.setGrabbed(true);
        this.deltaX = 0;
        this.deltaY = 0;
    }

    public void ungrabMouseCursor()
    {
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        Mouse.setGrabbed(false);
    }

    public void mouseXYChange()
    {
        if(RenderManager.takeover){
            this.deltaX = (int) RenderManager.deltaMouseX;
            this.deltaY = (int) RenderManager.deltaMouseY;
        }else{
            this.deltaX = Mouse.getDX();
            this.deltaY = Mouse.getDY();
        }

    }
}
