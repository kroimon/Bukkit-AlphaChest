package net.sradonia.bukkit.alphachest.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class BukkitUtil {

    /**
     * Searches for and retrieves an offline player by their name.
     *
     * @param name the name to search for
     * @return the found player or null
     */
    public static OfflinePlayer getOfflinePlayerByName(String name) {
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();

        for (OfflinePlayer player : offlinePlayers) {
            if (player.getName().equalsIgnoreCase(name)) {
                return player;
            }
        }

        return null;
    }
}
