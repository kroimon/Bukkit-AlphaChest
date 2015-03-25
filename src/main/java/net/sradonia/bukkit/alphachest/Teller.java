package net.sradonia.bukkit.alphachest;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Teller {

    private final AlphaChestPlugin plugin;

    private static String prefix;

    private static String infoColor;
    private static String successColor;
    private static String warningColor;
    private static String errorColor;
    private static String miscColor;

    public Teller(AlphaChestPlugin plugin) {
        this.plugin = plugin;

        init();
    }

    public enum Type {
        INFO, SUCCESS, WARNING, ERROR, MISC;
    }

    /**
     * Initializes the Teller prefix and colors.
     */
    public void init() {
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
     * @param sender  the CommandSender to message
     * @param type    the type of tell message to send
     * @param message the message to send
     */
    public static void tell(CommandSender sender, Type type, String message) {
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
        }

        String parsedMessage = prefix + " " + ChatColor.RESET + color + message;
        parsedMessage = ChatColor.translateAlternateColorCodes('&', parsedMessage);

        sender.sendMessage(parsedMessage);
    }
}
