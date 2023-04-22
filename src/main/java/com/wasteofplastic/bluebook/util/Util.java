/*
 * This file is part of BlueBook.

    BlueBook is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Bluebook is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Bluebook.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wasteofplastic.bluebook.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.FurnaceRecipe;
//import org.bukkit.configuration.InvalidConfigurationException;
//import org.bukkit.configuration.file.YamlConfiguration;
//import org.bukkit.enchantments.Enchantment;
//import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Colorable;
//import org.bukkit.inventory.meta.EnchantmentStorageMeta;
//import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import com.google.common.base.Enums;
import com.wasteofplastic.bluebook.BlueBook;
import com.wasteofplastic.bluebook.IngredientCost;

public class Util {
    private Map<Material, Double> blockPrices = new EnumMap<>(Material.class);
    private Map<DyeColor, Double> dyePrices = new EnumMap<>(DyeColor.class);
    private record Potions(PotionType pt, Integer strength, Boolean extended, Boolean splash) {}
    private Map<Potions, Double> potionPrices = new HashMap<>();
    private Map<Material, Double> fishPrices = new EnumMap<>(Material.class);

    private static BlueBook plugin;
    // Currency symbol or word
    private static String currency = "$";
    public String thisWorld = "";

    {
        plugin = BlueBook.instance;
        // Load the base prices from the config.yml file
        //loadPrices(thisWorld);

        // Calculate all the other prices based on these core prices
        //calculatePrices();
    }

    public void loadPrices(World world) {
        plugin.getLogger().info("Loading prices");
        // Initialize all block prices so that if any are missing nothing bad
        // happens
        // The value is set to a large negative number so that even crafted
        // items will probably become priceless
        //plugin.Bukkit.getLogger().info("World is " + world);
        // Check if the path exists, otherwise use the default
        Map<String, Object> defaultMap = plugin.getConfig().getConfigurationSection("block-prices").getValues(false);
        Map<String, Object> worldMap = null;
        if (world != null) {
            if (plugin.getConfig().isSet(world.getName())) {
                //plugin.Bukkit.getLogger().info("World found in config");
                worldMap = plugin.getConfig().getConfigurationSection(world.getName()).getValues(false);
            }
        }
        // Go through every material
        for (Entry<String, Object> mat : defaultMap.entrySet()) {
            Material m = Enums.getIfPresent(Material.class, mat.getKey()).orNull();
            if (m == null) {
                plugin.getLogger().warning("Material " + mat + " is unknown");
            } else {
                final Double price = Double.valueOf(mat.getValue().toString());
                blockPrices.put(m, price);
                plugin.getLogger().info("Material " + mat + " is price is " + price);
            }
        }

    }

    /*************************************
     * Calculates the prices of materials based on the core block prices that
     * have been loaded from the config file
     * @param
     */
    public void calculatePrices() {
        // Work through each material to calculate
        // Common materials for crafting
        final double fuel = getBlockPrice(Material.COAL) / 8.0;
        final double wood = getBlockPrice(Material.OAK_LOG) / 4.0;
        final double stick = wood / 2.0;
        final double goldBar = getBlockPrice(Material.GOLD_ORE) + fuel;
        final double ironBar = getBlockPrice(Material.IRON_ORE) + fuel;
        final double redStone = getBlockPrice(Material.REDSTONE);
        final double cobble = getBlockPrice(Material.COBBLESTONE);
        final double stone = cobble + fuel;
        final double diamond = getBlockPrice(Material.DIAMOND);
        // Basics
        blockPrices.put(Material.IRON_INGOT, ironBar);
        blockPrices.put(Material.GOLD_INGOT, goldBar);
        blockPrices.put(Material.OAK_WOOD, wood);
        blockPrices.put(Material.STICK, stick);
        blockPrices.put(Material.STONE, stone);

        // WHITE - bone meal
        dyePrices.put(DyeColor.WHITE, getBlockPrice(Material.BONE));
        // ORANGE - made the traditional way, just like mom used to make it
        dyePrices.put(
                DyeColor.ORANGE,
                getBlockPrice(Material.POPPY)
                + getBlockPrice(Material.DANDELION));
        // MAGENTA
        dyePrices.put(
                DyeColor.MAGENTA,
                getBlockPrice(Material.POPPY) * 2.0
                + getBlockPrice(Material.BONE)
                + getBlockPrice(Material.LAPIS_ORE));
        // LIGHT BLUE
        dyePrices.put(
                DyeColor.LIGHT_BLUE,
                getBlockPrice(Material.BONE)
                + getBlockPrice(Material.LAPIS_ORE));
        // YELLOW
        dyePrices.put(DyeColor.YELLOW, getBlockPrice(Material.DANDELION));
        // LIME
        dyePrices.put(
                DyeColor.LIME,
                getBlockPrice(Material.CACTUS) + fuel
                + getBlockPrice(Material.BONE));
        // PINK
        dyePrices.put(
                DyeColor.PINK,
                getBlockPrice(Material.POPPY)
                + getBlockPrice(Material.BONE));
        // GRAY
        dyePrices.put(
                DyeColor.GRAY,
                getBlockPrice(Material.INK_SAC)
                + getBlockPrice(Material.BONE));
        // LIGHT GRAY
        dyePrices.put(
                DyeColor.LIGHT_GRAY,
                getBlockPrice(Material.INK_SAC)
                + getBlockPrice(Material.BONE) * 2.0);
        // CYAN
        dyePrices.put(
                DyeColor.CYAN,
                getBlockPrice(Material.CACTUS) + fuel
                + getBlockPrice(Material.LAPIS_ORE));
        // PURPLE
        dyePrices.put(
                DyeColor.PURPLE,
                getBlockPrice(Material.POPPY)
                + getBlockPrice(Material.LAPIS_ORE));
        // BLUE
        dyePrices.put(DyeColor.BLUE, getBlockPrice(Material.LAPIS_ORE));
        // BROWN
        dyePrices.put(DyeColor.BROWN, getBlockPrice(Material.COCOA) / 3.0);
        // GREEN
        dyePrices.put(DyeColor.GREEN, getBlockPrice(Material.CACTUS) + fuel);
        // RED
        dyePrices.put(DyeColor.RED, getBlockPrice(Material.POPPY));
        // BLACK
        dyePrices.put(DyeColor.BLACK, getBlockPrice(Material.INK_SAC));

        // Other crafted items
        blockPrices.put(Material.GLASS, getBlockPrice(Material.SAND) + fuel);
        blockPrices.put(Material.LAPIS_BLOCK,
                getBlockPrice(Material.LAPIS_ORE) / 9.0);
        blockPrices.put(Material.CHEST, 8.0 * wood);
        blockPrices.put(Material.HOPPER,
                5.0 * ironBar + getBlockPrice(Material.CHEST));
        blockPrices.put(Material.DISPENSER,
                getBlockPrice(Material.COBBLESTONE) * 7.0
                + getBlockPrice(Material.HOPPER) + redStone);
        blockPrices.put(Material.SANDSTONE,
                getBlockPrice(Material.SAND) * 4.0);
        blockPrices.put(Material.NOTE_BLOCK, 8.0 * wood + redStone);
        blockPrices.put(Material.POWERED_RAIL,
                (6.0 * goldBar + redStone + stick) / 6.0);
        blockPrices.put(Material.STONE_PRESSURE_PLATE, 2.0 * stone);
        blockPrices.put(Material.DETECTOR_RAIL,
                (6.0 * ironBar + redStone + blockPrices
                        .get(Material.STONE_PRESSURE_PLATE)) / 6.0);
        blockPrices.put(Material.PISTON, 3.0 * wood + ironBar + 4.0
                * cobble + redStone);
        blockPrices.put(Material.STICKY_PISTON, 3.0 * wood + ironBar + 4.0
                * cobble + redStone + getBlockPrice(Material.SLIME_BALL));
        blockPrices.put(Material.GOLD_BLOCK, 9.0 * goldBar);
        blockPrices.put(Material.IRON_BLOCK, 9.0 * ironBar);
        blockPrices.put(Material.OAK_SLAB, 3.0 * wood / 6.0);
        blockPrices.put(Material.BRICK,
                getBlockPrice(Material.CLAY_BALL) + fuel);
        blockPrices.put(Material.TNT, getBlockPrice(Material.SAND) * 4.0
                + getBlockPrice(Material.GUNPOWDER) * 5.0);
        blockPrices.put(Material.PAPER, getBlockPrice(Material.SUGAR_CANE));
        blockPrices.put(Material.BOOK, getBlockPrice(Material.PAPER) * 3.0
                + getBlockPrice(Material.LEATHER));
        blockPrices.put(Material.BOOKSHELF, getBlockPrice(Material.BOOK)
                * 3.0 + getBlockPrice(Material.OAK_WOOD) * 6.0);
        blockPrices.put(Material.TORCH, stick + getBlockPrice(Material.COAL)
        / 4.0);
        blockPrices.put(Material.OAK_STAIRS, 6.0 * wood / 4.0);
        blockPrices.put(Material.DIAMOND_BLOCK, 9.0 * diamond);
        blockPrices.put(Material.CRAFTING_TABLE, 4.0 * wood);
        blockPrices.put(Material.FURNACE, 8.0 * cobble);
        blockPrices.put(Material.LADDER, 7.0 * stick / 3.0);
        blockPrices.put(Material.RAIL, (6.0 * ironBar + stick) / 16.0);
        blockPrices.put(Material.COBBLESTONE_STAIRS, 6.0 * cobble / 4.0);
        blockPrices.put(Material.LEVER, cobble + stick);
        blockPrices.put(Material.STONE_PRESSURE_PLATE, 2.0 * stone);
        blockPrices.put(Material.OAK_PRESSURE_PLATE, 2.0 * wood);
        blockPrices.put(Material.REDSTONE_TORCH, stick + redStone);
        blockPrices.put(Material.STONE_BUTTON, stone);
        blockPrices.put(Material.SNOW_BLOCK,
                4.0 * getBlockPrice(Material.SNOWBALL));
        blockPrices.put(Material.BRICKS,
                4.0 * getBlockPrice(Material.BRICK));
        blockPrices.put(Material.JUKEBOX, 9.0 * wood + diamond);
        blockPrices.put(Material.OAK_FENCE, 6.0 * stick / 3.0);
        blockPrices.put(Material.GLOWSTONE,
                4.0 * getBlockPrice(Material.GLOWSTONE_DUST));
        blockPrices.put(
                Material.JACK_O_LANTERN,
                getBlockPrice(Material.TORCH)
                + getBlockPrice(Material.PUMPKIN));
        blockPrices.put(Material.OAK_TRAPDOOR, 6.0 * wood / 2.0);
        blockPrices.put(Material.SMOOTH_STONE, stone);
        blockPrices.put(Material.IRON_BARS, 6.0 * ironBar / 16.0);
        blockPrices.put(Material.GLASS_PANE,
                6.0 * getBlockPrice(Material.GLASS) / 16.0);
        blockPrices.put(Material.MELON,
                9.0 * getBlockPrice(Material.MELON));
        blockPrices.put(Material.OAK_FENCE_GATE, 4.0 * stick + 2.0 * wood);
        blockPrices.put(Material.BRICK_STAIRS,
                6.0 * getBlockPrice(Material.BRICK) / 4.0);
        blockPrices.put(Material.STONE_BRICK_STAIRS,
                6.0 * getBlockPrice(Material.BRICKS) / 4.0);
        blockPrices.put(Material.NETHER_BRICK,
                getBlockPrice(Material.NETHERRACK) + fuel);
        blockPrices.put(Material.NETHER_BRICK,
                4.0 * getBlockPrice(Material.NETHER_BRICKS));
        blockPrices.put(Material.NETHER_BRICK_FENCE,
                getBlockPrice(Material.NETHER_BRICK));
        blockPrices.put(Material.NETHER_BRICK_STAIRS,
                6.0 * getBlockPrice(Material.NETHER_BRICK) / 4.0);
        blockPrices.put(Material.ENCHANTING_TABLE,
                4.0 * getBlockPrice(Material.OBSIDIAN) + diamond
                + getBlockPrice(Material.BOOK));
        blockPrices.put(Material.REDSTONE_LAMP,
                4.0 * redStone + getBlockPrice(Material.GLOWSTONE));
        blockPrices.put(Material.OAK_SLAB, 3.0 * wood / 6.0);
        blockPrices.put(Material.SANDSTONE_STAIRS,
                6.0 * getBlockPrice(Material.SANDSTONE) / 4.0);
        blockPrices.put(Material.BLAZE_POWDER,
                getBlockPrice(Material.BLAZE_ROD) / 2.0);
        blockPrices.put(
                Material.ENDER_EYE,
                getBlockPrice(Material.BLAZE_POWDER)
                + getBlockPrice(Material.ENDER_PEARL));
        blockPrices.put(
                Material.ENDER_CHEST,
                8.0 * getBlockPrice(Material.OBSIDIAN)
                + getBlockPrice(Material.ENDER_EYE));
        blockPrices.put(Material.TRIPWIRE_HOOK, ironBar + stick + wood);
        blockPrices.put(Material.EMERALD_BLOCK,
                9.0 * getBlockPrice(Material.EMERALD));
        blockPrices.put(Material.SPRUCE_STAIRS, 6.0 * wood / 4.0);
        blockPrices.put(Material.BIRCH_STAIRS, 6.0 * wood / 4.0);
        blockPrices.put(Material.JUNGLE_STAIRS, 6.0 * wood / 4.0);
        blockPrices.put(
                Material.BEACON,
                5.0 * getBlockPrice(Material.GLASS)
                + getBlockPrice(Material.OBSIDIAN)
                + getBlockPrice(Material.NETHER_STAR));
        blockPrices.put(Material.COBBLESTONE_WALL, cobble);
        blockPrices.put(Material.OAK_BUTTON, wood);
        blockPrices.put(Material.ANVIL,
                3.0 * getBlockPrice(Material.IRON_BLOCK) + 4.0 * ironBar);
        blockPrices.put(Material.TRAPPED_CHEST, getBlockPrice(Material.CHEST)
                + getBlockPrice(Material.TRIPWIRE_HOOK));
        blockPrices.put(Material.HEAVY_WEIGHTED_PRESSURE_PLATE, 2.0 * goldBar);
        blockPrices.put(Material.LIGHT_WEIGHTED_PRESSURE_PLATE, 2.0 * ironBar);
        blockPrices.put(
                Material.DAYLIGHT_DETECTOR,
                3.0 * getBlockPrice(Material.GLASS) + 3.0
                * getBlockPrice(Material.QUARTZ) + 3.0
                * getBlockPrice(Material.OAK_STAIRS));
        blockPrices.put(Material.REDSTONE_BLOCK, 9.0 * redStone);
        blockPrices.put(Material.QUARTZ_BLOCK,
                9.0 * getBlockPrice(Material.QUARTZ));
        blockPrices.put(Material.QUARTZ_STAIRS,
                6.0 * getBlockPrice(Material.QUARTZ) / 4.0);
        blockPrices.put(Material.ACTIVATOR_RAIL, 6.0 * ironBar + 2.0 * stick
                + getBlockPrice(Material.REDSTONE_TORCH));
        blockPrices.put(Material.DROPPER, 7.0 * cobble + redStone);
        blockPrices.put(Material.CLAY,
                4.0 * getBlockPrice(Material.CLAY_BALL));
        blockPrices.put(Material.TERRACOTTA, getBlockPrice(Material.CLAY)
                + fuel);
        blockPrices.put(Material.BLACK_TERRACOTTA,
                (8.0 * getBlockPrice(Material.TERRACOTTA) + blockPrices
                        .get(Material.INK_SAC)) / 8.0);
        blockPrices.put(Material.HAY_BLOCK,
                9.0 * getBlockPrice(Material.WHEAT));
        blockPrices.put(Material.WHITE_CARPET,
                2.0 * getBlockPrice(Material.WHITE_WOOL) / 3.0);
        blockPrices.put(Material.COAL_BLOCK,
                9.0 * getBlockPrice(Material.COAL));
        blockPrices.put(Material.IRON_SHOVEL, ironBar + 2.0 * stick);
        blockPrices.put(Material.IRON_PICKAXE, 3.0 * ironBar + 2.0 * stick);
        blockPrices.put(Material.IRON_AXE, 3.0 * ironBar + 2.0 * stick);
        blockPrices.put(Material.FLINT_AND_STEEL,
                ironBar + getBlockPrice(Material.FLINT));
        blockPrices.put(Material.BOW,
                3.0 * stick + getBlockPrice(Material.STRING));
        blockPrices.put(Material.ARROW, (getBlockPrice(Material.FLINT)
                + stick + getBlockPrice(Material.FEATHER)) / 4.0);
        blockPrices.put(Material.IRON_SWORD, 2.0 * ironBar + stick);
        blockPrices.put(Material.WOODEN_SWORD, 2.0 * wood + stick);
        blockPrices.put(Material.WOODEN_SHOVEL, wood + 2.0 * stick);
        blockPrices.put(Material.WOODEN_PICKAXE, 3.0 * wood + 2.0 * stick);
        blockPrices.put(Material.WOODEN_AXE, 3.0 * wood + 2.0 * stick);
        blockPrices.put(Material.STONE_SWORD, 2.0 * stone + stick);
        blockPrices.put(Material.STONE_SHOVEL, stone + 2.0 * stick);
        blockPrices.put(Material.STONE_PICKAXE, 3.0 * stone + 2.0 * stick);
        blockPrices.put(Material.STONE_AXE, 3.0 * stone + 2.0 * stick);
        blockPrices.put(Material.DIAMOND_SWORD, 2.0 * diamond + stick);
        blockPrices.put(Material.DIAMOND_SHOVEL, diamond + 2.0 * stick);
        blockPrices.put(Material.DIAMOND_PICKAXE, 3.0 * diamond + 2.0 * stick);
        blockPrices.put(Material.DIAMOND_AXE, 3.0 * diamond + 2.0 * stick);
        blockPrices.put(Material.BOWL, 3.0 * wood / 4.0);
        blockPrices.put(
                Material.MUSHROOM_STEW,
                getBlockPrice(Material.RED_MUSHROOM)
                + getBlockPrice(Material.BROWN_MUSHROOM)
                + (3.0 * wood / 4.0));
        blockPrices.put(Material.GOLDEN_SWORD, 2.0 * goldBar + stick);
        blockPrices.put(Material.GOLDEN_SHOVEL, goldBar + 2.0 * stick);
        blockPrices.put(Material.GOLDEN_PICKAXE, 3.0 * goldBar + 2.0 * stick);
        blockPrices.put(Material.GOLDEN_AXE, 3.0 * goldBar + 2.0 * stick);
        blockPrices.put(Material.WOODEN_HOE, 2.0 * wood + 2.0 * stick);
        blockPrices.put(Material.STONE_HOE, 2.0 * stone + 2.0 * stick);
        blockPrices.put(Material.IRON_HOE, 2.0 * ironBar + 2.0 * stick);
        blockPrices.put(Material.DIAMOND_HOE, 2.0 * diamond + 2.0 * stick);
        blockPrices.put(Material.GOLDEN_HOE, 2.0 * goldBar + 2.0 * stick);
        blockPrices.put(Material.BREAD, 3.0 * getBlockPrice(Material.WHEAT));
        blockPrices.put(Material.LEATHER_HELMET,
                5.0 * getBlockPrice(Material.LEATHER));
        blockPrices.put(Material.LEATHER_CHESTPLATE,
                8.0 * getBlockPrice(Material.LEATHER));
        blockPrices.put(Material.LEATHER_LEGGINGS,
                7.0 * getBlockPrice(Material.LEATHER));
        blockPrices.put(Material.LEATHER_BOOTS,
                4.0 * getBlockPrice(Material.LEATHER));
        blockPrices.put(Material.CHAINMAIL_HELMET,
                5.0 * getBlockPrice(Material.FIRE));
        blockPrices.put(Material.CHAINMAIL_CHESTPLATE,
                8.0 * getBlockPrice(Material.FIRE));
        blockPrices.put(Material.CHAINMAIL_LEGGINGS,
                7.0 * getBlockPrice(Material.FIRE));
        blockPrices.put(Material.CHAINMAIL_BOOTS,
                4.0 * getBlockPrice(Material.FIRE));
        blockPrices.put(Material.IRON_HELMET, 5.0 * ironBar);
        blockPrices.put(Material.IRON_CHESTPLATE, 8.0 * ironBar);
        blockPrices.put(Material.IRON_LEGGINGS, 7.0 * ironBar);
        blockPrices.put(Material.IRON_BOOTS, 4.0 * ironBar);
        blockPrices.put(Material.DIAMOND_HELMET, 5.0 * diamond);
        blockPrices.put(Material.DIAMOND_CHESTPLATE, 8.0 * diamond);
        blockPrices.put(Material.DIAMOND_LEGGINGS, 7.0 * diamond);
        blockPrices.put(Material.DIAMOND_BOOTS, 4.0 * diamond);
        blockPrices.put(Material.GOLDEN_HELMET, 5.0 * goldBar);
        blockPrices.put(Material.GOLDEN_CHESTPLATE, 8.0 * goldBar);
        blockPrices.put(Material.GOLDEN_LEGGINGS, 7.0 * goldBar);
        blockPrices.put(Material.GOLDEN_BOOTS, 4.0 * goldBar);
        blockPrices.put(Material.COOKED_PORKCHOP, getBlockPrice(Material.PORKCHOP)
                + fuel);
        blockPrices.put(Material.PAINTING,
                8.0 * stick + getBlockPrice(Material.WHITE_WOOL));
        blockPrices.put(Material.GOLDEN_APPLE,
                8.0 * goldBar + getBlockPrice(Material.APPLE));
        blockPrices.put(Material.OAK_SIGN, (6.0 * wood + stick) / 3.0);
        blockPrices.put(Material.OAK_DOOR, 6.0 * wood);
        blockPrices.put(Material.BUCKET, 3.0 * ironBar);
        blockPrices.put(Material.MINECART, 5.0 * ironBar);
        blockPrices.put(Material.IRON_DOOR, 6.0 * ironBar);
        blockPrices.put(Material.OAK_BOAT, 6.0 * wood);
        blockPrices.put(
                Material.CHEST_MINECART,
                getBlockPrice(Material.CHEST)
                + getBlockPrice(Material.MINECART));
        blockPrices.put(
                Material.FURNACE_MINECART,
                getBlockPrice(Material.FURNACE)
                + getBlockPrice(Material.MINECART));
        blockPrices.put(Material.COMPASS, 4.0 * ironBar + redStone);
        blockPrices.put(Material.FISHING_ROD,
                3.0 * stick + 2.0 * getBlockPrice(Material.STRING));
        blockPrices.put(Material.CLOCK, 4.0 * goldBar + redStone);
        blockPrices.put(Material.COOKED_COD,
                getBlockPrice(Material.COD) + fuel);
        blockPrices.put(Material.COOKED_SALMON,
                getBlockPrice(Material.SALMON) + fuel);
        blockPrices.put(Material.SUGAR, getBlockPrice(Material.SUGAR_CANE));
        blockPrices.put(
                Material.CAKE,
                3.0 * getBlockPrice(Material.WHEAT)
                + getBlockPrice(Material.EGG) + 2.0
                * getBlockPrice(Material.SUGAR) + 3.0
                * getBlockPrice(Material.MILK_BUCKET));
        blockPrices.put(Material.WHITE_BED,
                3.0 * wood + 3.0 * getBlockPrice(Material.WHITE_WOOL));
        blockPrices.put(Material.REPEATER, 3.0 * stone + redStone + 2.0
                * getBlockPrice(Material.REDSTONE_TORCH));
        blockPrices.put(Material.COOKIE, 2.0 * getBlockPrice(Material.WHEAT)
                + getBlockPrice(Material.COCOA) / 3.0);
        blockPrices.put(Material.SHEARS, 2.0 * ironBar);
        blockPrices.put(Material.COOKED_BEEF,
                getBlockPrice(Material.BEEF) + fuel);
        blockPrices.put(Material.COOKED_CHICKEN,
                getBlockPrice(Material.CHICKEN) + fuel);
        blockPrices.put(Material.GOLD_NUGGET, goldBar / 9.0);
        blockPrices.put(Material.GLASS_BOTTLE, getBlockPrice(Material.GLASS));
        // This next one is actually just the water bottle and not a potion -
        // glass bottle + water, which is free
        blockPrices.put(Material.POTION, getBlockPrice(Material.GLASS_BOTTLE));
        blockPrices.put(
                Material.FERMENTED_SPIDER_EYE,
                getBlockPrice(Material.SUGAR)
                + getBlockPrice(Material.BROWN_MUSHROOM)
                + getBlockPrice(Material.SPIDER_EYE));
        blockPrices.put(
                Material.MAGMA_CREAM,
                getBlockPrice(Material.SLIME_BALL)
                + getBlockPrice(Material.BLAZE_POWDER));
        blockPrices.put(Material.BREWING_STAND,
                3.0 * cobble + getBlockPrice(Material.BLAZE_ROD));
        blockPrices.put(Material.CAULDRON, 7.0 * ironBar);
        blockPrices.put(Material.GLISTERING_MELON_SLICE, 8.0 / 9.0 * goldBar
                + getBlockPrice(Material.MELON));
        blockPrices.put(
                Material.FIRE_CHARGE,
                getBlockPrice(Material.COAL)
                + getBlockPrice(Material.BLAZE_POWDER)
                + getBlockPrice(Material.GUNPOWDER));
        blockPrices.put(
                Material.KNOWLEDGE_BOOK,
                getBlockPrice(Material.INK_SAC)
                + getBlockPrice(Material.BOOK)
                + getBlockPrice(Material.FEATHER));
        blockPrices.put(Material.ITEM_FRAME,
                8.0 * stick + getBlockPrice(Material.LEATHER));
        blockPrices.put(Material.FLOWER_POT,
                3.0 * getBlockPrice(Material.BRICK));
        blockPrices.put(Material.BAKED_POTATO,
                getBlockPrice(Material.POTATO) + fuel);
        // blockPrices.put(Material.BAKED_POTATO, 20.0);
        blockPrices.put(
                Material.MAP,
                8.0 * getBlockPrice(Material.PAPER)
                + getBlockPrice(Material.COMPASS));
        blockPrices.put(Material.GOLDEN_CARROT, (8.0 / 9.0 * goldBar)
                + getBlockPrice(Material.CARROT));
        blockPrices.put(
                Material.CARROT_ON_A_STICK,
                getBlockPrice(Material.CARROT)
                + getBlockPrice(Material.FISHING_ROD));
        blockPrices.put(Material.PUMPKIN_PIE,
                getBlockPrice(Material.SUGAR) + getBlockPrice(Material.EGG)
                + getBlockPrice(Material.PUMPKIN));
        blockPrices.put(Material.COMPARATOR,
                3.0 * stone + 3.0 * getBlockPrice(Material.REDSTONE_TORCH)
                + getBlockPrice(Material.QUARTZ));
        blockPrices.put(
                Material.TNT_MINECART,
                getBlockPrice(Material.MINECART)
                + getBlockPrice(Material.TNT));
        blockPrices.put(Material.HOPPER_MINECART,
                getBlockPrice(Material.MINECART)
                + getBlockPrice(Material.HOPPER));
        blockPrices.put(Material.LEAD,
                (4.0 * getBlockPrice(Material.STRING) + getBlockPrice(Material.SLIME_BALL) / 2.0));
        // FISH
        // Fish are randomly caught using fishing rods. The probability of
        // catching each type of fish is in the code somewhere, but unknown to
        // me.
        // I used data from actual fishing as reported
        // http://www.minecraftforum.net/topic/2076618-fishing-topic/
        // Salmon
        fishPrices.put(Material.SALMON, getBlockPrice(Material.COD) * 2.30);
        // Clown fish
        fishPrices.put(Material.TROPICAL_FISH, getBlockPrice(Material.COD) * 42.82);
        // Puffer fish
        fishPrices.put(Material.PUFFERFISH, getBlockPrice(Material.COD) * 3.89);

        // POTIONS - base potion is always with netherwart, anything else is
        // worthless/priceless :-)
        double potionValue = getBlockPrice(Material.GLASS_BOTTLE)
                + getBlockPrice(Material.NETHER_WART) / 3;
        // 373:16 Awkward Potion
        potionPrices.put(new Potions(PotionType.AWKWARD, 0, false, false), potionValue);

        // 373:8198 Night Vision Potion (3:00) - does this exist?
        potionPrices.put(new Potions(PotionType.NIGHT_VISION, 1, false, false), getBlockPrice(Material.GOLDEN_CARROT) / 3.0
                + potionValue);
        // 373:8230 Night Vision II
        potionPrices.put(new Potions(PotionType.NIGHT_VISION, 2, false, false), getBlockPrice(Material.GOLDEN_CARROT) / 3.0
                + potionValue + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0);
        // 373:8262 Extended Night Vision Potion (8:00)
        potionPrices.put(new Potions(PotionType.NIGHT_VISION, 1, true, false), getBlockPrice(Material.GOLDEN_CARROT) / 3.0
                + potionValue + redStone / 3.0);
        // 373: Splash Night Vision Potion (3:00)
        potionPrices.put(new Potions(PotionType.NIGHT_VISION, 1, false, true), getBlockPrice(Material.GOLDEN_CARROT) / 3.0
                + potionValue + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // 373:16422 Splash Night Vision II
        potionPrices.put(new Potions(PotionType.NIGHT_VISION, 2, false, true), getBlockPrice(Material.GOLDEN_CARROT) / 3.0
                + potionValue + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0
                + getBlockPrice(Material.GUNPOWDER) / 3.0);
        /*
        // Splash Extended Night Vision Potion (8:00)
        potionPrices.put(16454,
                getBlockPrice(Material.GOLDEN_CARROT) / 3.0 + potionValue
                + redStone / 3.0 + getBlockPrice(Material.GUNPOWDER)
                / 3.0);

        // 373:8238 Invisibility
        potionPrices.put(8238, getBlockPrice(Material.GOLDEN_CARROT) / 3.0
                + getBlockPrice(Material.FERMENTED_SPIDER_EYE) / 3.0
                + potionValue);
        // 373:8270 Extended invisibility
        potionPrices.put(8270, getBlockPrice(Material.GOLDEN_CARROT) / 3.0
                + getBlockPrice(Material.FERMENTED_SPIDER_EYE) / 3.0
                + potionValue + redStone / 3.0);
        // 373:16430 Splash Invisibility
        potionPrices.put(16430, getBlockPrice(Material.GOLDEN_CARROT) / 3.0
                + getBlockPrice(Material.FERMENTED_SPIDER_EYE) / 3.0
                + potionValue + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // 373:16462 Splash Extended invisibility
        potionPrices.put(
                16462,
                getBlockPrice(Material.GOLDEN_CARROT) / 3.0
                + getBlockPrice(Material.FERMENTED_SPIDER_EYE) / 3.0
                + potionValue + redStone / 3.0
                + getBlockPrice(Material.GUNPOWDER) / 3.0);

        // Harm
        potionPrices.put(8268, getBlockPrice(Material.SPIDER_EYE) / 3.0
                + getBlockPrice(Material.FERMENTED_SPIDER_EYE) / 3.0
                + potionValue);
        // Harm 2
        potionPrices.put(8236, getBlockPrice(Material.SPIDER_EYE) / 3.0
                + getBlockPrice(Material.FERMENTED_SPIDER_EYE) / 3.0
                + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0 + potionValue);
        // Splash Harm
        potionPrices.put(16460, getBlockPrice(Material.SPIDER_EYE) / 3.0
                + getBlockPrice(Material.FERMENTED_SPIDER_EYE) / 3.0
                + getBlockPrice(Material.GUNPOWDER) / 3.0 + potionValue);
        // Splash harm 2
        potionPrices
        .put(16428, getBlockPrice(Material.SPIDER_EYE) / 3.0
                + getBlockPrice(Material.FERMENTED_SPIDER_EYE) / 3.0
                + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0
                + getBlockPrice(Material.GUNPOWDER) / 3.0 + potionValue);
        // Poison
        potionPrices.put(8196, getBlockPrice(Material.SPIDER_EYE) / 3.0
                + potionValue);
        // Poison II
        potionPrices.put(8228, getBlockPrice(Material.SPIDER_EYE) / 3.0
                + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0 + potionValue);
        // Poison Extended
        potionPrices.put(8260, getBlockPrice(Material.SPIDER_EYE) / 3.0
                + redStone / 3.0 + potionValue);
        // Splash Poison
        potionPrices.put(16388, getBlockPrice(Material.SPIDER_EYE) / 3.0
                + potionValue + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Splash Poison II
        potionPrices.put(16420, getBlockPrice(Material.SPIDER_EYE) / 3.0
                + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0 + potionValue
                + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Splash Poison Extended
        potionPrices
        .put(16452,
                getBlockPrice(Material.SPIDER_EYE) / 3.0 + redStone
                / 3.0 + potionValue
                + getBlockPrice(Material.GUNPOWDER) / 3.0);

        // Regeneration
        potionPrices.put(8193, getBlockPrice(Material.GHAST_TEAR) / 3.0
                + potionValue);
        // Regen II
        potionPrices.put(8225, getBlockPrice(Material.GHAST_TEAR) / 3.0
                + potionValue + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0);
        // Extended Regen
        potionPrices.put(8257, getBlockPrice(Material.GHAST_TEAR) / 3.0
                + potionValue + redStone / 3.0);

        // Regeneration Splash
        potionPrices.put(16385, getBlockPrice(Material.GHAST_TEAR) / 3.0
                + potionValue + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Regen II Splash
        potionPrices.put(16417, getBlockPrice(Material.GHAST_TEAR) / 3.0
                + potionValue + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0
                + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Extended Regen Splash
        potionPrices.put(16449,
                getBlockPrice(Material.GHAST_TEAR) / 3.0 + potionValue
                + redStone / 3.0 + getBlockPrice(Material.GUNPOWDER)
                / 3.0);

        // Strength
        potionPrices.put(8201, getBlockPrice(Material.BLAZE_POWDER) / 3.0
                + potionValue);
        // Strength II
        potionPrices.put(8233, getBlockPrice(Material.BLAZE_POWDER) / 3.0
                + potionValue + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0);
        // Extended Strength
        potionPrices.put(8265, getBlockPrice(Material.BLAZE_POWDER) / 3.0
                + potionValue + redStone / 3.0);

        // Strength Splash
        potionPrices.put(16393, getBlockPrice(Material.BLAZE_POWDER) / 3.0
                + potionValue + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Strength II Splash
        potionPrices.put(16425, getBlockPrice(Material.BLAZE_POWDER) / 3.0
                + potionValue + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0
                + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Extended Strength Splash
        potionPrices.put(16457,
                getBlockPrice(Material.BLAZE_POWDER) / 3.0 + potionValue
                + redStone / 3.0 + getBlockPrice(Material.GUNPOWDER)
                / 3.0);

        // Speed
        potionPrices.put(8194, getBlockPrice(Material.SUGAR) / 3.0
                + potionValue);
        // Speed II
        potionPrices.put(8226, getBlockPrice(Material.SUGAR) / 3.0
                + potionValue + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0);
        // Extended Speed
        potionPrices.put(8258, getBlockPrice(Material.SUGAR) / 3.0
                + potionValue + redStone / 3.0);

        // Speed Splash
        potionPrices.put(16386, getBlockPrice(Material.SUGAR) / 3.0
                + potionValue + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Speed II Splash
        potionPrices.put(16418, getBlockPrice(Material.SUGAR) / 3.0
                + potionValue + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0
                + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Extended Speed Splash
        potionPrices.put(16450,
                getBlockPrice(Material.SUGAR) / 3.0 + potionValue + redStone
                / 3.0 + getBlockPrice(Material.GUNPOWDER) / 3.0);

        // Fire Resistance
        potionPrices.put(8195, getBlockPrice(Material.MAGMA_CREAM) / 3.0
                + potionValue);
        // Fire Resistance II
        potionPrices.put(8227, getBlockPrice(Material.MAGMA_CREAM) / 3.0
                + potionValue + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0);
        // Extended Fire Resistance
        potionPrices.put(8259, getBlockPrice(Material.MAGMA_CREAM) / 3.0
                + potionValue + redStone / 3.0);

        // Fire Resistance Splash
        potionPrices.put(16387, getBlockPrice(Material.MAGMA_CREAM) / 3.0
                + potionValue + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Fire Resistance II Splash
        potionPrices.put(16419, getBlockPrice(Material.MAGMA_CREAM) / 3.0
                + potionValue + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0
                + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Extended Fire Resistance Splash
        potionPrices.put(16451,
                getBlockPrice(Material.MAGMA_CREAM) / 3.0 + potionValue
                + redStone / 3.0 + getBlockPrice(Material.GUNPOWDER)
                / 3.0);

        // Weakness
        potionPrices.put(8200, getBlockPrice(Material.FERMENTED_SPIDER_EYE)
                / 3.0 + potionValue);
        // Weakness II
        potionPrices.put(8232, getBlockPrice(Material.FERMENTED_SPIDER_EYE)
                / 3.0 + potionValue + getBlockPrice(Material.GLOWSTONE_DUST)
                / 3.0);
        // Extended Weakness
        potionPrices.put(8264, getBlockPrice(Material.FERMENTED_SPIDER_EYE)
                / 3.0 + potionValue + redStone / 3.0);

        // Weakness Splash
        potionPrices.put(16392, getBlockPrice(Material.FERMENTED_SPIDER_EYE)
                / 3.0 + potionValue + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Weakness II Splash
        potionPrices.put(16424, getBlockPrice(Material.FERMENTED_SPIDER_EYE)
                / 3.0 + potionValue + getBlockPrice(Material.GLOWSTONE_DUST)
                / 3.0 + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Extended Weakness Splash
        potionPrices.put(
                16456,
                getBlockPrice(Material.FERMENTED_SPIDER_EYE) / 3.0
                + potionValue + redStone / 3.0
                + getBlockPrice(Material.GUNPOWDER) / 3.0);

        // Slowness
        potionPrices.put(8202, getBlockPrice(Material.FERMENTED_SPIDER_EYE)
                / 3.0 + getBlockPrice(Material.SUGAR) / 3.0 + potionValue);
        // Slowness II
        potionPrices.put(8234, getBlockPrice(Material.FERMENTED_SPIDER_EYE)
                / 3.0 + getBlockPrice(Material.SUGAR) / 3.0 + potionValue
                + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0);
        // Extended Slowness
        potionPrices.put(8266, getBlockPrice(Material.FERMENTED_SPIDER_EYE)
                / 3.0 + getBlockPrice(Material.SUGAR) / 3.0 + potionValue
                + redStone / 3.0);

        // Slowness Splash
        potionPrices.put(16394, getBlockPrice(Material.FERMENTED_SPIDER_EYE)
                / 3.0 + getBlockPrice(Material.SUGAR) / 3.0 + potionValue
                + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Slowness II Splash
        potionPrices.put(16426,
                getBlockPrice(Material.FERMENTED_SPIDER_EYE) / 3.0
                + getBlockPrice(Material.SUGAR) / 3.0 + potionValue
                + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0
                + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Extended Slowness Splash
        potionPrices.put(16458, getBlockPrice(Material.FERMENTED_SPIDER_EYE)
                / 3.0 + getBlockPrice(Material.SUGAR) / 3.0 + potionValue
                + redStone / 3.0 + getBlockPrice(Material.GUNPOWDER) / 3.0);

        // Healing
        potionPrices.put(8197, getBlockPrice(Material.GLISTERING_MELON_SLICE) / 3.0
                + potionValue);
        // Healing II
        potionPrices.put(8229, getBlockPrice(Material.GLISTERING_MELON_SLICE) / 3.0
                + potionValue + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0);
        // Extended Healing
        potionPrices.put(8261, getBlockPrice(Material.GLISTERING_MELON_SLICE) / 3.0
                + potionValue + redStone / 3.0);

        // Healing Splash
        potionPrices.put(16389, getBlockPrice(Material.GLISTERING_MELON_SLICE) / 3.0
                + potionValue + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Healing II Splash
        potionPrices.put(16421, getBlockPrice(Material.GLISTERING_MELON_SLICE) / 3.0
                + potionValue + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0
                + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Extended Healing Splash
        potionPrices.put(16453,
                getBlockPrice(Material.GLISTERING_MELON_SLICE) / 3.0 + potionValue
                + redStone / 3.0 + getBlockPrice(Material.GUNPOWDER)
                / 3.0);

        // Technically this potion is made from PUFFERFISH 349:3
        // Water Breathing
        potionPrices.put(13, fishPrices.get(Material.PUFFERFISH) / 3.0 + potionValue);
        potionPrices.put(8205, fishPrices.get(Material.PUFFERFISH) / 3.0 + potionValue);
        // Water Breathing II
        potionPrices.put(8237, fishPrices.get(Material.PUFFERFISH) / 3.0 + potionValue
                + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0);
        // Extended Water Breathing
        potionPrices.put(8269, fishPrices.get(Material.PUFFERFISH) / 3.0 + potionValue + redStone
                / 3.0);

        // Water Breathing Splash
        potionPrices.put(16397, fishPrices.get(Material.PUFFERFISH) / 3.0 + potionValue
                + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Water Breathing II Splash
        potionPrices.put(
                16429,
                fishPrices.get(Material.PUFFERFISH) / 3.0 + potionValue
                + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0
                + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Extended Water Breathing Splash
        potionPrices.put(16461, fishPrices.get(Material.PUFFERFISH) / 3.0 + potionValue
                + redStone / 3.0 + getBlockPrice(Material.GUNPOWDER) / 3.0);
        // Leaping
        if (blockPrices.containsKey(Material.RABBIT_FOOT)) {
            // 8203 - Jump boost
            potionPrices.put(8203, getBlockPrice(Material.RABBIT_FOOT) / 3D + potionValue);
            // 8235 - Jump boost II
            potionPrices.put(8235, getBlockPrice(Material.RABBIT_FOOT) / 3D + potionValue
                    + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0);
            // 8267 - Jump boost extended
            potionPrices.put(8267, getBlockPrice(Material.RABBIT_FOOT) + potionValue
                    + redStone / 3.0);
            // 16395 - Splash jump boost
            potionPrices.put(16395, getBlockPrice(Material.RABBIT_FOOT) + potionValue
                    + getBlockPrice(Material.GUNPOWDER) / 3.0);
            // 16427 - Splash jump boost II
            potionPrices.put(16427, getBlockPrice(Material.RABBIT_FOOT) / 3D + potionValue
                    + getBlockPrice(Material.GLOWSTONE_DUST) / 3.0
                    + getBlockPrice(Material.GUNPOWDER) / 3.0);
            // 16459 - Splash jump boost extended
            potionPrices.put(16459, getBlockPrice(Material.RABBIT_FOOT) + potionValue
                    + redStone / 3.0
                    + getBlockPrice(Material.GUNPOWDER) / 3.0);
        }
         */
    }


    private Double getBlockPrice(Material mat) {
        return blockPrices.getOrDefault(mat, -10000D);
    }

    /**
     * Fetches an ItemStack's name - For example, converting INK_SAC:11 to
     * Dandelion Yellow, or WOOL:14 to Red Wool
     *
     * @param i
     *            The itemstack to fetch the name of
     * @return The human readable item name.
     */
    public static String getName(ItemStack i) {
        // If the item has had its name changed, then let's use that
        String vanillaName = "";
        String displayName = i.getItemMeta().getDisplayName();
        if (displayName == null) {
            vanillaName = prettifyText(i.getType().name());
        } else {
            vanillaName = displayName;
        }
        /*
		if (!i.getEnchantments().isEmpty()) {
			vanillaName += " (Enchanted)";
			// Loop through every enchantment on the item
			/*
			for (Map.Entry<Enchantment, Integer> item : i.getEnchantments()
					.entrySet()) {
				String name = item.getKey().getName();
				vanillaName += "\n" + name + " " + toRoman(item.getValue());
			}
		}*/
        return vanillaName;
    }

    private static final String[] ROMAN = { "X", "IX", "V", "IV", "I" };
    private static final int[] DECIMAL = { 10, 9, 5, 4, 1 };

    /**
     * Converts the given number to roman numerals. If the number is >= 40 or <=
     * 0, it will just return the number as a string.
     *
     * @param n
     *            The number to convert
     * @return The roman numeral representation of this number, or the number in
     *         decimal form as a string if n <= 0 || n >= 40.
     */
    public static String toRoman(int n) {
        if (n <= 0 || n >= 40)
            return "" + n;
        String roman = "";

        for (int i = 0; i < ROMAN.length; i++) {
            while (n >= DECIMAL[i]) {
                n -= DECIMAL[i];
                roman += ROMAN[i];
            }
        }

        return roman;
    }

    /**
     * Converts a name like IRON_INGOT into Iron Ingot to improve readability
     *
     * @param ugly
     *            The string such as IRON_INGOT
     * @return A nicer version, such as Iron Ingot
     *
     *         Credits to mikenon on GitHub!
     */
    public static String prettifyText(String ugly) {
        if (!ugly.contains("_") && (!ugly.equals(ugly.toUpperCase())))
            return ugly;
        String fin = "";
        ugly = ugly.toLowerCase();
        if (ugly.contains("_")) {
            String[] splt = ugly.split("_");
            int i = 0;
            for (String s : splt) {
                i += 1;
                fin += Character.toUpperCase(s.charAt(0)) + s.substring(1);
                if (i < splt.length)
                    fin += " ";
            }
        } else {
            fin += Character.toUpperCase(ugly.charAt(0)) + ugly.substring(1);
        }
        return fin;
    }

    /**
     * @param mat
     *            The material to check
     * @return Returns true if the item is a tool (Has durability) or false if
     *         it doesn't.
     */
    public static boolean isTool(Material mat) {
        return Tag.ITEMS_TOOLS.isTagged(mat);
    }

    /**
     * Formats the given number into a price. Adds commas to enable easy reading and the currency symbol or word
     *
     * @return The formatted string.
     */
    public static String format(double n) {
        //TODO complete
        NumberFormat formatter;
        String number;
        formatter = new DecimalFormat("#,###,###,###.00");
        number = formatter.format(n);
        if (currency.length() == 1) {
            return  currency + number;
        } else {
            return number + " " + currency;
        }
    }
    /*
     * Sets the currency to be used in formating
     */
    public void setCurrency(String c) {
        if (c != null) {
            currency = c;
        }
    }

    /**
     * Returns the price of a block of the ItemStack or -100000 if unknown
     *
     * @return The price of the block
     */
    public static double getEnchantmentValue(ItemStack i) {
        // TODO
        if (!i.getEnchantments().isEmpty()) {
            // Loop through every enchantment on the item and value it
            /*
             * The value is a multiplier to the base value of the item, so
             * diamond items are more valuable than wooden ones. Method to
             * calculate the multiplier is as follows:
             *
             * 1. Add the value of all the enchantments on the item The value is
             * determine by the probability of obtaining the enchantment as
             * follows:
             *
             * ==Armor==
             * Protection 1
             *	Enchantment.PROTECTION_ENVIRONMENTAL;
             * Feather Fall 2
             *	Enchantment.PROTECTION_FALL;
             * Fire Protection 2
             *	Enchantment.PROTECTION_FIRE;
             * Projectile Protection 2
             *	Enchantment.PROTECTION_PROJECTILE;
             * Aqua Affinity 5
             *	Enchantment.WATER_WORKER;
             * Blast Protection 5
             *	Enchantment.PROTECTION_EXPLOSIONS;
             * Respiration 5
             *	Enchantment.OXYGEN;
             * Thorns 10
             *	Enchantment.THORNS;
             *
             * ==Sword==
             * Sharpness	1
             * 	Enchantment.DAMAGE_ALL;
             * Bane of Arthropods	2.5
             * 	Enchantment.DAMAGE_ARTHROPODS;
             * Knockback	2.5
             * 	Enchantment.KNOCKBACK;
             * Smite	2.5
             * 	Enchantment.DAMAGE_UNDEAD;
             * Fire Aspect	5
             * 	Enchantment.FIRE_ASPECT;
             * Looting	5
             * 	Enchantment.LOOT_BONUS_MOBS;
             *
             * ==Bow==
             * Power	1
             *	Enchantment.ARROW_DAMAGE;
             * Flame	5
             * 	Enchantment.ARROW_FIRE;
             * Punch	5
             * 	Enchantment.ARROW_KNOCKBACK;
             * Infinity	10
             * 	Enchantment.ARROW_INFINITE;
             *
             * ==Rod==
             * Luck of the Sea 1
             * 	Enchantment.LUCK;
             * Lure 1
             * 	Enchantment.LURE;
             *
             * ==Tools==
             * Efficiency 1
             * 	Enchantment.DIG_SPEED;
             * Unbreaking 2
             * 	Enchantment.DURABILITY;
             * Fortune 5
             * 	Enchantment.LOOT_BONUS_BLOCKS;
             * Silk Touch 10
             * 	Enchantment.SILK_TOUCH;
             *
             * REMOVED: Multiple enchantments make the object more valuable
             * 1 enchantment = 1x
             * 2 enchantments = 3.1x
             * 3 enchantments = 5.88x
             * 4 enchantments = 10.53x
             * This is derived from the probability of obtaining
             * multiple enchantments as calculated 1000's of times (see website calculators)
             */
            /*
			final Map <Integer, Double> multiEnchantValue = new HashMap <Integer, Double>() {
				private static final long serialVersionUID = 1L;
				{
					put(1, 1.0);
					put(2, 3.1);
					put(3, 5.88);
					put(4, 10.53);
				}
			};*/
            double enchantValue = 0.0;
            //int multiplier = 0;
            for (Map.Entry<Enchantment, Integer> item : i.getEnchantments()
                    .entrySet()) {
                // Find rank of the enchantment
                int rank = item.getValue();
                // Add up value
                if (item.getKey().equals(Enchantment.ARROW_DAMAGE)
                        | item.getKey().equals(
                                Enchantment.PROTECTION_ENVIRONMENTAL)
                        | item.getKey().equals(Enchantment.DAMAGE_ALL)
                        | item.getKey().equals(Enchantment.LURE)
                        | item.getKey().equals(Enchantment.LUCK)
                        | item.getKey().equals(Enchantment.DIG_SPEED)) {
                    // Value = 1
                    enchantValue += (1.0 * rank);
                    //plugin.Bukkit.getLogger().info("Enchant +1 rank " + rank);
                } else if (item.getKey().equals(Enchantment.PROTECTION_FALL)
                        | item.getKey().equals(Enchantment.PROTECTION_FIRE)
                        | item.getKey().equals(
                                Enchantment.PROTECTION_PROJECTILE)) {
                    // Value 2
                    enchantValue += (2.0 * rank);
                    //plugin.Bukkit.getLogger().info("Enchant +2 rank " + rank);
                } else if (item.getKey().equals(Enchantment.DAMAGE_ARTHROPODS)
                        | item.getKey().equals(Enchantment.KNOCKBACK)
                        | item.getKey().equals(Enchantment.DAMAGE_UNDEAD)) {
                    // Value 2.5
                    enchantValue += 2.5;
                    //plugin.Bukkit.getLogger().info("Enchant +2.5 rank "+ rank);
                } else if (item.getKey().equals(Enchantment.WATER_WORKER)
                        | item.getKey().equals(
                                Enchantment.PROTECTION_EXPLOSIONS)
                        | item.getKey().equals(Enchantment.OXYGEN)
                        | item.getKey().equals(Enchantment.FIRE_ASPECT)
                        | item.getKey().equals(Enchantment.LOOT_BONUS_MOBS)
                        | item.getKey().equals(Enchantment.ARROW_FIRE)
                        | item.getKey().equals(Enchantment.ARROW_KNOCKBACK)
                        | item.getKey().equals(Enchantment.LOOT_BONUS_BLOCKS)) {
                    // Value 5
                    enchantValue += (5.0 * rank);
                    //plugin.Bukkit.getLogger().info("Enchant +5 rank "+ rank);
                } else if (item.getKey().equals(Enchantment.THORNS)
                        | item.getKey().equals(Enchantment.ARROW_INFINITE)
                        | item.getKey().equals(Enchantment.SILK_TOUCH)) {
                    // Value 10
                    enchantValue += (10.0 * rank);
                    //plugin.Bukkit.getLogger().info("Enchant +10 rank "+ rank);
                }
                //multiplier++;
            }
            // Multiply the value by how many enchantments there are
            //plugin.Bukkit.getLogger().info("Total = "+enchantValue+" enchantments");
            //enchantValue = enchantValue * multiEnchantValue.get(multiplier);
            return enchantValue;
        } else {
            return 0.0;
        }
    }
    /**
     * Returns the price of a block of the ItemStack or -100000.0 if unknown
     *
     * @return The price of the block
     */
    public double getPrice(ItemStack item, String itemWorld) {
        /*
	// Get the world that this item is in
	if (!itemWorld.equalsIgnoreCase(thisWorld)) {
	    // Need to update prices for this new world
	    loadPrices(itemWorld);
	    calculatePrices(itemWorld);
	    thisWorld = itemWorld;
	}
         */
        // Multiplier for damage
        double damageModifier = 1;
        Material type = item.getType();

        // Find out the max durability of the item
        short maxDurability = item.getType().getMaxDurability();

        // Find out if this item is damaged or not, i.e. durability
        ItemMeta meta = item.getItemMeta();
        int durability = 1;
        if (meta instanceof Damageable damageable) {
            durability = damageable.getDamage();
            damageModifier = (double)durability / maxDurability;
        }

        // If this has a prescribed value, then return it, otherwise, calculate it.
        if (blockPrices.containsKey(type)) {
            return blockPrices.get(type) * damageModifier;
        }
        /*
         * Anything else should be calculated based on crafting or rarity.
         * Bed, HangingSign, Sheep, Shulker, Sign, Dye
         */
        if (meta instanceof Colorable c) {
            // Calculate based on dye
            double dyePrice = dyePrices.get(c.getColor());

            if (type.name().contains("_BED")) {
                return 3D * (dyePrice + getBlockPrice(Material.WHITE_WOOL) + getBlockPrice(Material.OAK_WOOD));
            } else if (type.name().contains("STAINED_GLASS")) {
                return dyePrice + getBlockPrice(Material.GLASS);
            } else if (type.name().contains("CARPET")) {
                return dyePrice + 2 / 3 * getBlockPrice(Material.WHITE_WOOL);
            } else if (Tag.WOOL.isTagged(type)) {
                return dyePrice + blockPrices.get(Material.WHITE_WOOL);
            } else {
                return dyePrice;
            }
        }
        // Fish - rarity
        if (Tag.ITEMS_FISHES.isTagged(type)) {
            return (fishPrices.get(type));
        }
        /*
    case INK_SAC:
        try {
            return (dyePrices.get(durability));
        } catch (Exception e) {
            return -10000.0;
        }
    case POTION:
        if (potionPrices.containsKey(durability)) {
            return potionPrices.get(durability);
        }
        return -10000.0;*/


        if (type.equals(Material.GOLDEN_APPLE)) {
            return (getBlockPrice(Material.APPLE) + getBlockPrice(Material.GOLD_BLOCK) * 8.0);
        }



        // Adjust price for damage to the item
        if (maxDurability > 0) {
            damageModifier = 1 - (double) durability
                    / (double) maxDurability;
        }

        // Final total is...
        if (blockPrices.containsKey(type)) {
            return getBlockPrice(type) * damageModifier;
        } else {
            // See if we can work it out via the recipe
            // List of items that have no recipes
            List<IngredientCost> ingredients = new ArrayList<IngredientCost>();
            // List of items that may have recipes still
            List<IngredientCost> crafted = getIngredients(item);
            //Bukkit.getLogger().info("Cost of " + item.getType().toString());
            if (crafted != null) {
                for (int i = 0; i<3; i++) {
                    /*
		    Bukkit.getLogger().info("Iteration " + i);
		    for (IngredientCost ingredient : crafted) {
			Bukkit.getLogger().info(ingredient.getIngredient().toString());
		    }
		    Bukkit.getLogger().info("**********************");
                     */
                    //Bukkit.getLogger().info(crafted.toString());
                    List<IngredientCost> toBeRemoved = new ArrayList<IngredientCost>();
                    List<IngredientCost> toBeAdded = new ArrayList<IngredientCost>();
                    for (IngredientCost ingredient : crafted) {
                        // Check if this block is already of a known price or can be crafted
                        if (ingredient.getCost() > 0) {
                            // Yes we know the price of this, no need to research the recipe any more
                            ingredients.add(ingredient);
                            toBeRemoved.add(ingredient);
                        } else {
                            // Get the next level of recipes
                            List<IngredientCost> subIngredients = getIngredients(ingredient.getIngredient());
                            if (subIngredients == null) {
                                // No
                                ingredients.add(ingredient);
                                toBeRemoved.add(ingredient);
                            } else {
                                // Yes, add the sub ingredients to the crafted list
                                // There are some null ingredients in there so we have to go through and weed them out
                                for (IngredientCost subItem : subIngredients) {
                                    if (subItem != null) {
                                        toBeAdded.add(subItem);
                                    }
                                }
                                toBeRemoved.add(ingredient);
                            }
                        }
                    }
                    // Remove what needs to be removed from the crafted list
                    for (IngredientCost ingredient : toBeRemoved) {
                        crafted.remove(ingredient);
                    }
                    // Add what needs to be added
                    for (IngredientCost ingredient : toBeAdded) {
                        crafted.add(ingredient);
                    }
                    if (crafted.isEmpty()) {
                        break;
                    }
                }
            }
            // Done!
            //Bukkit.getLogger().info("Number of ingredients = " + ingredients.size());
            double cost = 0D;
            for (IngredientCost ingredient : ingredients) {
                if (ingredient.getCost() > 0) {
                    //Bukkit.getLogger().info(ingredient.getIngredient().toString() + " $" + ingredient.getCost());
                    cost += ingredient.getCost();
                } else {
                    Bukkit.getLogger().warning("I need to know what " + ingredient.getIngredient().getType().toString() + " is worth!");
                    Bukkit.getLogger().warning("Please add to Bluebook's config.yml");
                    cost = -10010D;
                    break;
                }
            }
            //Bukkit.getLogger().info("Total = $" + cost);
            return cost;
            //return -10010;
        }

    }

    /**
     * @return the blockPrices
     */
    public Map<Material, Double> getBlockPrices() {
        return blockPrices;
    }
    /**
     * Calculate the recipe for this item in hand
     * @param item
     * @return List of recipe items and their factored cost
     */
    private List<IngredientCost> getIngredients(ItemStack item) {
        List<IngredientCost> ingredients = new ArrayList<IngredientCost>();
        // If we know the price of this item immediately (it's in config) then return that, not the recipe
        if (blockPrices.containsKey(item.getType())) {
            //Bukkit.getLogger().info("Price of " + item.toString() + " is known and is $" + getBlockPrice(item.getType()));
            ingredients.add(new IngredientCost(item, getBlockPrice(item.getType())));
            return ingredients;
        }
        List<Recipe> recipe = Bukkit.getServer().getRecipesFor(item);
        if (!recipe.isEmpty()) {
            // Only do one recipe
            Recipe r = recipe.get(0);
            if (r instanceof ShapedRecipe) {
                //Bukkit.getLogger().info("Shaped Recipe");
                ShapedRecipe sr = (ShapedRecipe)r;
                for (ItemStack ingredient : sr.getIngredientMap().values()) {
                    if (ingredient != null) {
                        //Bukkit.getLogger().info(ingredient.toString());
                        double cost = 0D;
                        if (blockPrices.containsKey(ingredient.getType())) {
                            cost = getBlockPrice(ingredient.getType()) / sr.getResult().getAmount();
                        }
                        ingredients.add(new IngredientCost(ingredient, cost));
                    }
                }
                //Bukkit.getLogger().info("Result = " + sr.getResult().toString());
            } else if (r instanceof ShapelessRecipe) {
                //Bukkit.getLogger().info("Shapeless Recipe");
                ShapelessRecipe slr = (ShapelessRecipe)r;
                for (ItemStack ingredient : slr.getIngredientList()) {
                    if (ingredient != null) {
                        //Bukkit.getLogger().info(ingredient.toString());
                        double cost = 0D;
                        if (blockPrices.containsKey(ingredient.getType())) {
                            cost = getBlockPrice(ingredient.getType()) / slr.getResult().getAmount();
                        }
                        ingredients.add(new IngredientCost(ingredient, cost));
                    }
                }
                //Bukkit.getLogger().info("Result = " + slr.getResult().toString());
            } else if (r instanceof FurnaceRecipe) {
                //Bukkit.getLogger().info("Furnace Recipe");
                FurnaceRecipe fr = (FurnaceRecipe)r;
                //ingredients.add(fr.getInput());
                double cost = 0D;
                if (blockPrices.containsKey(fr.getInput().getType())) {
                    cost = getBlockPrice(fr.getInput().getType()) / fr.getResult().getAmount();
                }
                ingredients.add(new IngredientCost(fr.getInput(), cost));
                // Coal must be in config
                if (blockPrices.containsKey("COAL")) {
                    // Magic number - Coal can cook 8 things...ish
                    cost = getBlockPrice(Material.COAL) / 8D;
                } else {
                    cost = 1;
                }
                ingredients.add(new IngredientCost(new ItemStack(Material.COAL), cost));
                //Bukkit.getLogger().info("Input = " + fr.getInput().toString());
                //Bukkit.getLogger().info("Result = " + fr.getResult().toString());
            }
            return ingredients;
        }
        return null;
    }
}