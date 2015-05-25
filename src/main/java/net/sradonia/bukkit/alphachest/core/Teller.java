package net.sradonia.bukkit.alphachest.core;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.sradonia.bukkit.alphachest.AlphaChest;

public class Teller {

    private final AlphaChest plugin;

    private String prefix;

    private String infoColor;
    private String successColor;
    private String warningColor;
    private String errorColor;
    private String miscColor;

    public Teller(AlphaChest plugin) {
        this.plugin = plugin;

        init();
    }

    public enum Type {
        INFO, SUCCESS, WARNING, ERROR, MISC;
    }

    /**
     * Initializes the Teller prefix and colors.
     */
    private void init() {
        prefix = plugin.getConfig().getString("messages.prefix", "&0[&7AlphaChest&0]");

        infoColor = plugin.getConfig().getString("messages.colors.info", "&f");
        successColor = plugin.getConfig().getString("messages.colors.success", "&2");
        warningColor = plugin.getConfig().getString("messages.colors.warning", "&6");
        errorColor = plugin.getConfig().getString("messages.colors.error", "&4");
        miscColor = plugin.getConfig().getString("messages.colors.misc", "&1");
    }

    /**
     * Sends a formatted and colored message to a specified CommandSender with a given type and message.
     *
     * @param sender the CommandSender to send the message to
     * @param type the type of Teller message to send
     * @param message the message to send
     */
    public void tell(CommandSender sender, Type type, String message) {
        String color = "";

        switch (type) {
            case INFO:
                color = infoColor;
                break;
            case SUCCESS:
                color = successColor;
                break;
            case WARNING:
                color = warningColor;
                break;
            case ERROR:
                color = errorColor;
                break;
            case MISC:
                color = miscColor;
                break;
        }

        String parsedMessage = prefix + " " + ChatColor.RESET + color + message;
        parsedMessage = ChatColor.translateAlternateColorCodes('&', parsedMessage);

        sender.sendMessage(parsedMessage);
    }
}
