package pl.ynfuien.ydevlib.guis;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;

import java.util.*;

public class Item {
    protected Material material;
    protected Short amount = 1;
    protected Component displayName = null;
    protected boolean unbreakable = false;
    protected List<Component> lore = new ArrayList<>();
    protected Set<ItemFlag> itemFlags = new HashSet<>();
    protected Color potionColor = null;
    protected HashMap<Enchantment, Integer> enchantments = new HashMap<>();

    public boolean load(ConfigurationSection config) {
        finalItem = null;

        Map<String, Object> values = config.getValues(true);
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            YLogger.info(String.format("Entry: '%s' - '%s'", entry.getKey(), entry.getValue()));
        }

        // Material
        if (!config.contains("material")) {
            log("Key 'material' is missing!");
            return false;
        }

        material = Material.matchMaterial(config.getString("material"));
        if (material == null) {
            log("Value for the 'material' key is incorrect!");
            return false;
        }

        // Amount
        if (config.contains("amount")) {
            amount = (short) config.getInt("amount");

            if (amount <= 0) {
                log("Amount can't be lower than 0! It will be set to 1.");
                amount = 1;
            }
        }


        MiniMessage serializer = ColorFormatter.SERIALIZER;

        // Display name
        if (config.contains("display-name")) {
            String text = config.getString("display-name");
            displayName = serializer.deserialize(text);
        }

        // Unbreakable
        if (config.contains("unbreakable")) {
            unbreakable = config.getBoolean("unbreakable");
        }

        // Lore
        if (config.contains("lore")) {
            if (!config.isList("lore")) {
                log("Lore must be a list!");
                return false;
            }

            List<String> lines = config.getStringList("lore");
            for (String line : lines) {
                lore.add(serializer.deserialize(line));
            }
        }

        // Item flags
        if (config.contains("item-flags")) {
            if (!config.isList("item-flags")) {
                log("Item-flags must be a list!");
                return false;
            }

            List<String> flags = config.getStringList("item-flags");
            for (String flag : flags) {
                try {
                    ItemFlag itemFlag = ItemFlag.valueOf(flag.toUpperCase());
                    itemFlags.add(itemFlag);
                } catch (IllegalArgumentException e) {
                    log(String.format("Item flag '%s' is incorrect!", flag));
                }
            }
        }

        // Potion color
        if (config.contains("potion-color")) {
            String hexColor = config.getString("potion-color");


            try {
                java.awt.Color c = java.awt.Color.decode(hexColor);
                potionColor = Color.fromRGB(c.getRed(), c.getGreen(), c.getBlue());
            } catch (NumberFormatException e) {
                log(String.format("Potion color '%s' is incorrect! It's supposed to be in the HEX format - #RRGGBB.", hexColor));
            }
        }

        // Enchants
        if (config.contains("enchants")) {
            if (!config.isConfigurationSection("enchants")) {
                log("Enchants must be a section!");
                return false;
            }

            ConfigurationSection section = config.getConfigurationSection("enchants");
            Set<String> enchants = section.getKeys(false);
            for (String enchant : enchants) {
                Enchantment enchantment = Bukkit.getRegistry(Enchantment.class).get(NamespacedKey.minecraft(enchant.toLowerCase()));
                if (enchantment == null) {
                    log(String.format("Enchant '%s' is incorrect!", enchant));
                    continue;
                }

                int value = section.getInt(enchant);
                enchantments.put(enchantment, value);
            }
        }

        return true;
    }

    public static Item load(ConfigurationSection config, String key) {
        Item item = new Item();

        ConfigurationSection section = config.getConfigurationSection(key);
        if (section == null) {
            String material = config.getString(key);
            Material mat = Material.matchMaterial(material);

            if (mat == null) {
                item.log(String.format("Material '%s' is incorrect!", material));
                return null;
            }

            item.material = mat;
            return item;
        }

        return item.load(section) ? item : null;
    }

    protected void log(String message) {
        YLogger.warn(String.format("[Item] %s", message));
    }

    private ItemStack finalItem = null;
    public ItemStack getItemStack() {
        if (finalItem != null) return finalItem;

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(displayName);
        meta.lore(lore);
        meta.setUnbreakable(unbreakable);
        for (ItemFlag flag : itemFlags) meta.addItemFlags(flag);

        for (Enchantment enchant : enchantments.keySet()) {
            meta.addEnchant(enchant, enchantments.get(enchant), true);
        }

        if (potionColor != null) {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setColor(potionColor);
        }

        item.setItemMeta(meta);
        finalItem = item;
        return item;
    }
}