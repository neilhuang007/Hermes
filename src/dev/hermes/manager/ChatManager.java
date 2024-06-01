package dev.hermes.manager;

import dev.hermes.Hermes;
import dev.hermes.utils.interfaces.InstanceAccess;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;

@UtilityClass
public class ChatManager implements InstanceAccess {

    @Getter
    @AllArgsConstructor
    public enum KeyColors {
        RED(new Color(255, 50, 50)),
        ORANGE(new Color(255, 128, 50)),
        YELLOW(new Color(255, 255, 50)),
        LIME(new Color(128, 255, 50)),
        DARK_GREEN(new Color(50, 128, 50)),
        AQUA(new Color(50, 200, 255)),
        DARK_BLUE(new Color(50, 100, 200)),
        PURPLE(new Color(128, 50, 255)),
        PINK(new Color(255, 128, 255)),
        GRAY(new Color(100, 100, 110));

        private final Color color;
    }

    /**
     * Adds a message to the players chat without sending it to the server
     *
     * @param message message that is going to be added to chat
     */
    public void display(final Object message, final Object... objects) {
        if (mc.thePlayer != null) {
            final String format = String.format(message.toString(), objects);

            mc.thePlayer.addChatMessage(new ChatComponentText(getPrefix() + format));
        }
    }

    public void displayNoPrefix(final Object message, final Object... objects) {
        if (mc.thePlayer != null) {
            final String format = String.format(message.toString(), objects);

            mc.thePlayer.addChatMessage(new ChatComponentText(getPrefix() + format));
        }
    }

    /**
     * Sends a message in the chat
     *
     * @param message message that is going to be sent in chat
     */
    public void send(final Object message) {
        if (mc.thePlayer != null) {
            PacketManager.send(new C01PacketChatMessage(message.toString()));
        }
    }

    private String getPrefix() {
        final String color = KeyColors.AQUA.toString();
        return EnumChatFormatting.BOLD + color + Hermes.NAME
                + EnumChatFormatting.RESET + color + " » "
                + EnumChatFormatting.RESET;
    }
}
