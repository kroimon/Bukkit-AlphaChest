package net.sradonia.bukkit.alphachest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Utility class to store inventories to file and read them back again.
 */
public class InventoryIO {

    /**
     * Loads an inventory from a plain-text file.
     *
     * @param file the text file to load
     * @return the loaded inventory
     * @throws IOException if the file could not be read
     * @deprecated use {@link #loadFromYaml} instead
     */
    @Deprecated
    public static Inventory loadFromTextFile(File file) throws IOException {
        final Inventory inventory = Bukkit.getServer().createInventory(null, 6 * 9);

        final BufferedReader in = new BufferedReader(new FileReader(file));

        String line;
        int slot = 0;

        while ((line = in.readLine()) != null) {
            if (!line.equals("")) {
                final String[] parts = line.split(":");

                try {
                    int type = Integer.parseInt(parts[0]);
                    int amount = Integer.parseInt(parts[1]);
                    short damage = Short.parseShort(parts[2]);

                    if (type != 0) {
                        inventory.setItem(slot, new ItemStack(type, amount, damage));
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }

                ++slot;
            }
        }

        in.close();

        return inventory;
    }

    /**
     * Loads an inventory from a YAML configuration file.
     *
     * @param file the YAML file to load
     * @return the loaded inventory
     * @throws IOException if the file could not be read
     * @throws InvalidConfigurationException if the file could not be parsed
     */
    public static Inventory loadFromYaml(File file) throws IOException, InvalidConfigurationException {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.load(file);

        int inventorySize = yaml.getInt("size", 6 * 9);

        Inventory inventory = Bukkit.getServer().createInventory(null, inventorySize);

        ConfigurationSection items = yaml.getConfigurationSection("items");

        for (int slot = 0; slot < inventorySize; slot++) {
            String slotString = String.valueOf(slot);

            if (items.isItemStack(slotString)) {
                ItemStack itemStack = items.getItemStack(slotString);
                inventory.setItem(slot, itemStack);
            }
        }

        return inventory;
    }

    /**
     * Saves an inventory to a YAML configuration file.
     *
     * @param inventory the inventory to save
     * @param file the YAML file to write
     * @throws IOException if the file could not be written
     */
    public static void saveToYaml(Inventory inventory, File file) throws IOException {
        YamlConfiguration yaml = new YamlConfiguration();

        int inventorySize = inventory.getSize();
        yaml.set("size", inventorySize);

        ConfigurationSection items = yaml.createSection("items");

        for (int slot = 0; slot < inventorySize; slot++) {
            ItemStack stack = inventory.getItem(slot);

            if (stack != null) {
                items.set(String.valueOf(slot), stack);
            }
        }

        yaml.save(file);
    }
}
