package net.sradonia.bukkit.alphachest.utils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public final class BukkitUtil {

    private BukkitUtil() {}

    /**
     * Returns a player by their name.
     *
     * @param name the name to search for
     * @return the found player or null
     */
    public static Player getPlayer(String name) {
        Validate.notNull(name, "Name cannot be null");

        Player found = null;
        String lowerName = name.toLowerCase();
        int delta = Integer.MAX_VALUE;
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        for (Player player : players) {
            if (player.getName().toLowerCase().startsWith(lowerName)) {
                int curDelta = player.getName().length() - lowerName.length();

                if (curDelta < delta) {
                    found = player;
                    delta = curDelta;
                }

                if (curDelta == 0) break;
            }
        }

        return found;
    }

    /**
     * Returns an OfflinePlayer by their name.
     *
     * @param name the name to search for
     * @return the found player or null
     */
    public static OfflinePlayer getOfflinePlayer(String name) {
        OfflinePlayer player = getPlayer(name);

        if (player != null) {
            return player;
        } else {
            UUID uuid = null;

            UUIDFetcher fetcher = new UUIDFetcher(Collections.singletonList(name));
            Map<String, UUID> response;

            try {
                response = fetcher.call();
                uuid = response.get(name);
            } catch (Exception e) {
                Bukkit.getServer().getLogger().warning("Exception while running UUIDFetcher");
                e.printStackTrace();
            }

            return uuid == null ? null : Bukkit.getOfflinePlayer(uuid);
        }
    }
}
