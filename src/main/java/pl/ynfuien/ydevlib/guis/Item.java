package pl.ynfuien.ydevlib.guis;

import com.destroystokyo.paper.profile.PlayerProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import pl.ynfuien.ydevlib.messages.Messenger;
import pl.ynfuien.ydevlib.messages.YLogger;
import pl.ynfuien.ydevlib.messages.colors.ColorFormatter;

import java.util.*;

public class Item {
    protected Material material;
    protected Short amount = 1;

    protected String displayName = null;
    protected List<String> lore = new ArrayList<>();

    protected boolean unbreakable = false;
    protected Set<ItemFlag> itemFlags = new HashSet<>();
    protected Color potionColor = null;
    protected HashMap<Enchantment, Integer> enchantments = new HashMap<>();
    protected String skullOwner = null;

    private ItemStack finalItem = null;
    private ItemStack templateItem = null;
    private boolean needsComponentParsing = false;

    /**
     * Loads an item from the config section, that is expected to at least have a 'material' field.
     * @return True if item was successfully loaded, false otherwise
     */
    public boolean load(ConfigurationSection config) {
        templateItem = null;

        // Material
        if (!config.contains("material")) {
            log("Key 'material' is missing!");
            return false;
        }

        material = Material.matchMaterial(config.getString("material"));
        if (material == null || !material.isItem()) {
            log(String.format("Material '%s' is incorrect!", config.getString("material")));
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

        // Display name
        if (config.contains("display-name")) displayName = config.getString("display-name");

        // Lore
        if (config.contains("lore")) {
            if (!config.isList("lore")) {
                log("Lore must be a list!");
                return false;
            }

            lore = config.getStringList("lore");
        }

        // Unbreakable
        if (config.contains("unbreakable")) {
            unbreakable = config.getBoolean("unbreakable");
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

        // Skull owner
        if (config.contains("skull-owner")) {
            skullOwner = config.getString("skull-owner");
            if (!material.equals(Material.PLAYER_HEAD)) log("Skull owner field is set but item material isn't a player head!");
        }

        setupItemStacks();
        return true;
    }

    /**
     * Loads an item that's under provided key in provided config section.
     * If a value under a key is a string, then it will be used for the item material.
     * Otherwise, a section under that key will be passed to the {@link #load(ConfigurationSection) load(ConfigurationSection)}.
     * @return Loaded item or null if there was an error
     */
    public static Item load(ConfigurationSection config, String key) {
        Item item = new Item();

        ConfigurationSection section = config.getConfigurationSection(key);
        if (section == null) {
            String material = config.getString(key);
            Material mat = Material.matchMaterial(material);

            if (mat == null || !mat.isItem()) {
                item.log(String.format("Material '%s' is incorrect!", material));
                return null;
            }

            item.material = mat;
            item.setupItemStacks();
            return item;
        }

        return item.load(section) ? item : null;
    }

    protected void log(String message) {
        YLogger.warn(String.format("[Item] %s", message));
    }

    private void setupItemStacks() {
        if (material.isAir()) {
            templateItem = ItemStack.empty();
            finalItem = templateItem;
            return;
        }

        MiniMessage serializer = ColorFormatter.SERIALIZER;

        // Template item, for per player usage
        templateItem = new ItemStack(material, amount);
        ItemMeta meta = templateItem.getItemMeta();

        meta.setUnbreakable(unbreakable);
        for (ItemFlag flag : itemFlags) meta.addItemFlags(flag);

        for (Enchantment enchant : enchantments.keySet()) {
            meta.addEnchant(enchant, enchantments.get(enchant), true);
        }

        if (potionColor != null) {
            PotionMeta potionMeta = (PotionMeta) meta;
            potionMeta.setColor(potionColor);
        }

        templateItem.setItemMeta(meta);

        // Final item, for general usage
        finalItem = templateItem.clone();
        meta = finalItem.getItemMeta();

        if (this.displayName != null) {
            needsComponentParsing = true;
            Component displayName = serializer.deserialize(this.displayName);
            meta.displayName(ColorFormatter.negateUnsetDecoration(displayName, TextDecoration.ITALIC));
        }

        if (!this.lore.isEmpty()) {
            needsComponentParsing = true;
            List<Component> lore = new ArrayList<>();
            for (String line : this.lore) {
                Component parsed = serializer.deserialize(line);
                lore.add(ColorFormatter.negateUnsetDecoration(parsed, TextDecoration.ITALIC));
            }
            meta.lore(lore);
        }

        if (material.equals(Material.PLAYER_HEAD) && skullOwner != null) needsComponentParsing = true;

        finalItem.setItemMeta(meta);
    }

    public ItemStack getItemStack() {
        return finalItem;
    }

    public ItemStack getItemStack(Player player) {
        return getItemStack(player, new HashMap<>());
    }

    public ItemStack getItemStack(Player player, HashMap<String, Object> placeholders) {
        if (!needsComponentParsing) return templateItem;

        if (placeholders != null) {
            placeholders.put("player-name", player.getName());
            placeholders.put("player-uuid", player.getUniqueId().toString());
        }

        ItemStack item = templateItem.clone();
        ItemMeta meta = item.getItemMeta();

        if (this.displayName != null) {
            Component displayName = Messenger.parseMessage(player, this.displayName, placeholders);
            meta.displayName(ColorFormatter.negateUnsetDecoration(displayName, TextDecoration.ITALIC));
        }

        if (!this.lore.isEmpty()) {
            List<Component> lore = new ArrayList<>();
            for (String line : this.lore) {
                Component parsed = Messenger.parseMessage(player, line, placeholders);
                lore.add(ColorFormatter.negateUnsetDecoration(parsed, TextDecoration.ITALIC));
            }
            meta.lore(lore);
        }

        if (material.equals(Material.PLAYER_HEAD) && skullOwner != null) {
            String owner = Messenger.parsePluginPlaceholders(skullOwner, placeholders);
            owner = ColorFormatter.parsePAPI(player, owner);

            try {
                PlayerProfile profile = Bukkit.createProfile(owner);
                SkullMeta skullMeta = (SkullMeta) meta;
                skullMeta.setPlayerProfile(profile);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                log(String.format("Provided skull-owner is incorrect! ('%s')", owner));
            }
        }


        item.setItemMeta(meta);
        return item;
    }
}