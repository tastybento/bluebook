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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
//import java.util.Set;



//import org.bukkit.ChatColor;
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
//import org.bukkit.configuration.InvalidConfigurationException;
//import org.bukkit.configuration.file.YamlConfiguration;
//import org.bukkit.enchantments.Enchantment;
//import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.EnchantmentStorageMeta;
//import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import com.wasteofplastic.bluebook.BlueBook;

public class Util {
	private static HashSet<Material> tools = new HashSet<Material>();
	private static HashMap<Material, Double> blockPrices = new HashMap<Material, Double>();
	private static HashMap<Integer, Double> dyePrices = new HashMap<Integer, Double>();
	private static HashMap<Integer, Double> potionPrices = new HashMap<Integer, Double>();
	private static HashMap<Integer, Double> fishPrices = new HashMap<Integer, Double>();

	private static BlueBook plugin;
	// Currency symbol or word
	private static String currency = "$";
	
	static {
		plugin = BlueBook.instance;
		// Initialize all block prices so that if any are missing nothing bad
		// happens
		// The value is set to a large negative number so that even crafted
		// items will probably become priceless
		for (Material mat : Material.values()) {
			// Set the price of everything to -10000
			blockPrices.put(mat, -10000.0);
		}
		// plugin.getLogger().info("BlueBook Loading Prices from config.yml");
		// Load in prices
		Map<String, Object> map = plugin.getConfig()
				.getConfigurationSection("block-prices").getValues(false);
		for (Entry<String, Object> entry : map.entrySet()) { // just to loop
																// through the
																// entries
			Material key = Material.getMaterial(entry.getKey());
			Double price = Double.valueOf(entry.getValue().toString());
			if (key != null) {
				blockPrices.put(key, price); // cast the value so it becomes a
												// "MyObjectClass".
				// plugin.getLogger().info("Block loaded " + key + " $" +
				// price);
			} else {
				plugin.getLogger().info(
						"BlueBook - Invalid block in config.yml: "
								+ entry.getKey());
			}
		}

		// Calculate all the other prices based on these core prices
		calculatePrices();

		// Set up a list of "tools" for the object lister
		tools.add(Material.BOW);
		tools.add(Material.SHEARS);
		tools.add(Material.FISHING_ROD);
		tools.add(Material.FLINT_AND_STEEL);

		tools.add(Material.CHAINMAIL_BOOTS);
		tools.add(Material.CHAINMAIL_CHESTPLATE);
		tools.add(Material.CHAINMAIL_HELMET);
		tools.add(Material.CHAINMAIL_LEGGINGS);

		tools.add(Material.WOOD_AXE);
		tools.add(Material.WOOD_HOE);
		tools.add(Material.WOOD_PICKAXE);
		tools.add(Material.WOOD_SPADE);
		tools.add(Material.WOOD_SWORD);

		tools.add(Material.LEATHER_BOOTS);
		tools.add(Material.LEATHER_CHESTPLATE);
		tools.add(Material.LEATHER_HELMET);
		tools.add(Material.LEATHER_LEGGINGS);

		tools.add(Material.DIAMOND_AXE);
		tools.add(Material.DIAMOND_HOE);
		tools.add(Material.DIAMOND_PICKAXE);
		tools.add(Material.DIAMOND_SPADE);
		tools.add(Material.DIAMOND_SWORD);

		tools.add(Material.DIAMOND_BOOTS);
		tools.add(Material.DIAMOND_CHESTPLATE);
		tools.add(Material.DIAMOND_HELMET);
		tools.add(Material.DIAMOND_LEGGINGS);
		tools.add(Material.STONE_AXE);
		tools.add(Material.STONE_HOE);
		tools.add(Material.STONE_PICKAXE);
		tools.add(Material.STONE_SPADE);
		tools.add(Material.STONE_SWORD);

		tools.add(Material.GOLD_AXE);
		tools.add(Material.GOLD_HOE);
		tools.add(Material.GOLD_PICKAXE);
		tools.add(Material.GOLD_SPADE);
		tools.add(Material.GOLD_SWORD);

		tools.add(Material.GOLD_BOOTS);
		tools.add(Material.GOLD_CHESTPLATE);
		tools.add(Material.GOLD_HELMET);
		tools.add(Material.GOLD_LEGGINGS);
		tools.add(Material.IRON_AXE);
		tools.add(Material.IRON_HOE);
		tools.add(Material.IRON_PICKAXE);
		tools.add(Material.IRON_SPADE);
		tools.add(Material.IRON_SWORD);

		tools.add(Material.IRON_BOOTS);
		tools.add(Material.IRON_CHESTPLATE);
		tools.add(Material.IRON_HELMET);
		tools.add(Material.IRON_LEGGINGS);
	}

	/*************************************
	 * Calculates the prices of materials based on the core block prices that
	 * have been loaded from the config file
	 */
	private static void calculatePrices() {
		// Work through each material to calculate
		// Common materials for crafting
		final double fuel = blockPrices.get(Material.COAL) / 8.0;
		final double wood = blockPrices.get(Material.LOG) / 4.0;
		final double stick = wood / 2.0;
		final double goldBar = blockPrices.get(Material.GOLD_ORE) + fuel;
		final double ironBar = blockPrices.get(Material.IRON_ORE) + fuel;
		final double redStone = blockPrices.get(Material.REDSTONE);
		final double cobble = blockPrices.get(Material.COBBLESTONE);
		final double stone = cobble + fuel;
		final double diamond = blockPrices.get(Material.DIAMOND);
		// Basics
		blockPrices.put(Material.IRON_INGOT, ironBar);
		blockPrices.put(Material.GOLD_INGOT, goldBar);
		blockPrices.put(Material.WOOD, wood);
		blockPrices.put(Material.STICK, stick);
		blockPrices.put(Material.STONE, stone);

		// dyes - all are INK_SACK variants
		// WHITE - bone meal
		dyePrices.put(15, blockPrices.get(Material.BONE));
		// ORANGE - made the traditional way, just like mom used to make it
		dyePrices.put(
				14,
				blockPrices.get(Material.RED_ROSE)
						+ blockPrices.get(Material.YELLOW_FLOWER));
		// MAGENTA
		dyePrices.put(
				13,
				blockPrices.get(Material.RED_ROSE) * 2.0
						+ blockPrices.get(Material.BONE)
						+ blockPrices.get(Material.LAPIS_ORE));
		// LIGHT BLUE
		dyePrices.put(
				12,
				blockPrices.get(Material.BONE)
						+ blockPrices.get(Material.LAPIS_ORE));
		// YELLOW
		dyePrices.put(11, blockPrices.get(Material.YELLOW_FLOWER));
		// LIME
		dyePrices.put(
				10,
				blockPrices.get(Material.CACTUS) + fuel
						+ blockPrices.get(Material.BONE));
		// PINK
		dyePrices.put(
				9,
				blockPrices.get(Material.RED_ROSE)
						+ blockPrices.get(Material.BONE));
		// GRAY
		dyePrices.put(
				8,
				blockPrices.get(Material.INK_SACK)
						+ blockPrices.get(Material.BONE));
		// LIGHT GRAY
		dyePrices.put(
				7,
				blockPrices.get(Material.INK_SACK)
						+ blockPrices.get(Material.BONE) * 2.0);
		// CYAN
		dyePrices.put(
				6,
				blockPrices.get(Material.CACTUS) + fuel
						+ blockPrices.get(Material.LAPIS_ORE));
		// PURPLE
		dyePrices.put(
				5,
				blockPrices.get(Material.RED_ROSE)
						+ blockPrices.get(Material.LAPIS_ORE));
		// BLUE
		dyePrices.put(4, blockPrices.get(Material.LAPIS_ORE));
		// BROWN
		dyePrices.put(3, blockPrices.get(Material.COCOA) / 3.0);
		// GREEN
		dyePrices.put(2, blockPrices.get(Material.CACTUS) + fuel);
		// RED
		dyePrices.put(1, blockPrices.get(Material.RED_ROSE));
		// BLACK
		dyePrices.put(0, blockPrices.get(Material.INK_SACK));

		// Other crafted items
		blockPrices.put(Material.GLASS, blockPrices.get(Material.SAND) + fuel);
		blockPrices.put(Material.LAPIS_BLOCK,
				blockPrices.get(Material.LAPIS_ORE) / 9.0);
		blockPrices.put(
				Material.DISPENSER,
				blockPrices.get(Material.COBBLESTONE) * 7.0
						+ blockPrices.get(Material.HOPPER) + redStone);
		blockPrices.put(Material.SANDSTONE,
				blockPrices.get(Material.SAND) * 4.0);
		blockPrices.put(Material.NOTE_BLOCK, 8.0 * wood + redStone);
		blockPrices.put(Material.POWERED_RAIL,
				(6.0 * goldBar + redStone + stick) / 6.0);
		blockPrices.put(Material.STONE_PLATE, 2.0 * stone);
		blockPrices.put(Material.DETECTOR_RAIL,
				(6.0 * ironBar + redStone + blockPrices
						.get(Material.STONE_PLATE)) / 6.0);
		blockPrices.put(Material.PISTON_BASE, 3.0 * wood + ironBar + 4.0
				* cobble + redStone);
		blockPrices.put(Material.PISTON_STICKY_BASE, 3.0 * wood + ironBar + 4.0
				* cobble + redStone + blockPrices.get(Material.SLIME_BALL));
		blockPrices.put(Material.GOLD_BLOCK, 9.0 * goldBar);
		blockPrices.put(Material.IRON_BLOCK, 9.0 * ironBar);
		blockPrices.put(Material.STEP, 3.0 * wood / 6.0);
		blockPrices.put(Material.CLAY_BRICK,
				blockPrices.get(Material.CLAY_BALL) + fuel);
		blockPrices.put(Material.TNT, blockPrices.get(Material.SAND) * 4.0
				+ blockPrices.get(Material.SULPHUR) * 5.0);
		blockPrices.put(Material.PAPER, blockPrices.get(Material.SUGAR_CANE));
		blockPrices.put(Material.BOOK, blockPrices.get(Material.PAPER) * 3.0
				+ blockPrices.get(Material.LEATHER));
		blockPrices.put(Material.BOOKSHELF, blockPrices.get(Material.BOOK)
				* 3.0 + blockPrices.get(Material.WOOD) * 6.0);
		blockPrices.put(Material.TORCH, stick + blockPrices.get(Material.COAL)
				/ 4.0);
		blockPrices.put(Material.WOOD_STAIRS, 6.0 * wood / 4.0);
		blockPrices.put(Material.CHEST, 8.0 * wood);
		blockPrices.put(Material.DIAMOND_BLOCK, 9.0 * diamond);
		blockPrices.put(Material.WORKBENCH, 4.0 * wood);
		blockPrices.put(Material.FURNACE, 8.0 * cobble);
		blockPrices.put(Material.LADDER, 7.0 * stick / 3.0);
		blockPrices.put(Material.RAILS, (6.0 * ironBar + stick) / 16.0);
		blockPrices.put(Material.COBBLESTONE_STAIRS, 6.0 * cobble / 4.0);
		blockPrices.put(Material.LEVER, cobble + stick);
		blockPrices.put(Material.STONE_PLATE, 2.0 * stone);
		blockPrices.put(Material.WOOD_PLATE, 2.0 * wood);
		blockPrices.put(Material.REDSTONE_TORCH_OFF, stick + redStone);
		blockPrices.put(Material.REDSTONE_TORCH_ON, stick + redStone);
		blockPrices.put(Material.STONE_BUTTON, stone);
		blockPrices.put(Material.SNOW_BLOCK,
				4.0 * blockPrices.get(Material.SNOW_BALL));
		blockPrices.put(Material.BRICK,
				4.0 * blockPrices.get(Material.CLAY_BRICK));
		blockPrices.put(Material.JUKEBOX, 9.0 * wood + diamond);
		blockPrices.put(Material.FENCE, 6.0 * stick / 3.0);
		blockPrices.put(Material.GLOWSTONE,
				4.0 * blockPrices.get(Material.GLOWSTONE_DUST));
		blockPrices.put(
				Material.JACK_O_LANTERN,
				blockPrices.get(Material.TORCH)
						+ blockPrices.get(Material.PUMPKIN));
		blockPrices.put(Material.TRAP_DOOR, 6.0 * wood / 2.0);
		blockPrices.put(Material.SMOOTH_BRICK, stone);
		blockPrices.put(Material.IRON_FENCE, 6.0 * ironBar / 16.0);
		blockPrices.put(Material.THIN_GLASS,
				6.0 * blockPrices.get(Material.GLASS) / 16.0);
		blockPrices.put(Material.MELON_BLOCK,
				9.0 * blockPrices.get(Material.MELON));
		blockPrices.put(Material.FENCE_GATE, 4.0 * stick + 2.0 * wood);
		blockPrices.put(Material.BRICK_STAIRS,
				6.0 * blockPrices.get(Material.BRICK) / 4.0);
		blockPrices.put(Material.SMOOTH_STAIRS,
				6.0 * blockPrices.get(Material.SMOOTH_BRICK) / 4.0);
		blockPrices.put(Material.NETHER_BRICK_ITEM,
				blockPrices.get(Material.NETHERRACK) + fuel);
		blockPrices.put(Material.NETHER_BRICK,
				4.0 * blockPrices.get(Material.NETHER_BRICK_ITEM));
		blockPrices.put(Material.NETHER_FENCE,
				blockPrices.get(Material.NETHER_BRICK));
		blockPrices.put(Material.NETHER_BRICK_STAIRS,
				6.0 * blockPrices.get(Material.NETHER_BRICK) / 4.0);
		blockPrices.put(Material.ENCHANTMENT_TABLE,
				4.0 * blockPrices.get(Material.OBSIDIAN) + diamond
						+ blockPrices.get(Material.BOOK));
		blockPrices.put(Material.REDSTONE_LAMP_ON,
				4.0 * redStone + blockPrices.get(Material.GLOWSTONE));
		blockPrices.put(Material.REDSTONE_LAMP_OFF, 4.0 * redStone
				+ blockPrices.get(Material.GLOWSTONE));
		blockPrices.put(Material.WOOD_STEP, 3.0 * wood / 6.0);
		blockPrices.put(Material.SANDSTONE_STAIRS,
				6.0 * blockPrices.get(Material.SANDSTONE) / 4.0);
		blockPrices.put(Material.BLAZE_POWDER,
				blockPrices.get(Material.BLAZE_ROD) / 2.0);
		blockPrices.put(
				Material.EYE_OF_ENDER,
				blockPrices.get(Material.BLAZE_POWDER)
						+ blockPrices.get(Material.ENDER_PEARL));
		blockPrices.put(
				Material.ENDER_CHEST,
				8.0 * blockPrices.get(Material.OBSIDIAN)
						+ blockPrices.get(Material.EYE_OF_ENDER));
		blockPrices.put(Material.TRIPWIRE_HOOK, ironBar + stick + wood);
		blockPrices.put(Material.EMERALD_BLOCK,
				9.0 * blockPrices.get(Material.EMERALD));
		blockPrices.put(Material.SPRUCE_WOOD_STAIRS, 6.0 * wood / 4.0);
		blockPrices.put(Material.BIRCH_WOOD_STAIRS, 6.0 * wood / 4.0);
		blockPrices.put(Material.JUNGLE_WOOD_STAIRS, 6.0 * wood / 4.0);
		blockPrices.put(
				Material.BEACON,
				5.0 * blockPrices.get(Material.GLASS)
						+ blockPrices.get(Material.OBSIDIAN)
						+ blockPrices.get(Material.NETHER_STAR));
		blockPrices.put(Material.COBBLE_WALL, cobble);
		blockPrices.put(Material.WOOD_BUTTON, wood);
		blockPrices.put(Material.ANVIL,
				3.0 * blockPrices.get(Material.IRON_BLOCK) + 4.0 * ironBar);
		blockPrices.put(Material.TRAPPED_CHEST, blockPrices.get(Material.CHEST)
				+ blockPrices.get(Material.TRIPWIRE_HOOK));
		blockPrices.put(Material.GOLD_PLATE, 2.0 * goldBar);
		blockPrices.put(Material.IRON_PLATE, 2.0 * ironBar);
		blockPrices.put(
				Material.DAYLIGHT_DETECTOR,
				3.0 * blockPrices.get(Material.GLASS) + 3.0
						* blockPrices.get(Material.QUARTZ) + 3.0
						* blockPrices.get(Material.STEP));
		blockPrices.put(Material.REDSTONE_BLOCK, 9.0 * redStone);
		blockPrices.put(Material.HOPPER,
				5.0 * ironBar + blockPrices.get(Material.CHEST));
		blockPrices.put(Material.QUARTZ_BLOCK,
				9.0 * blockPrices.get(Material.QUARTZ));
		blockPrices.put(Material.QUARTZ_STAIRS,
				6.0 * blockPrices.get(Material.QUARTZ) / 4.0);
		blockPrices.put(Material.ACTIVATOR_RAIL, 6.0 * ironBar + 2.0 * stick
				+ blockPrices.get(Material.REDSTONE_TORCH_ON));
		blockPrices.put(Material.DROPPER, 7.0 * cobble + redStone);
		blockPrices.put(Material.CLAY,
				4.0 * blockPrices.get(Material.CLAY_BALL));
		blockPrices.put(Material.HARD_CLAY, blockPrices.get(Material.CLAY)
				+ fuel);
		blockPrices.put(Material.STAINED_CLAY,
				(8.0 * blockPrices.get(Material.HARD_CLAY) + blockPrices
						.get(Material.INK_SACK)) / 8.0);
		blockPrices.put(Material.HAY_BLOCK,
				9.0 * blockPrices.get(Material.WHEAT));
		blockPrices.put(Material.CARPET,
				2.0 * blockPrices.get(Material.WOOL) / 3.0);
		blockPrices.put(Material.COAL_BLOCK,
				9.0 * blockPrices.get(Material.COAL));
		blockPrices.put(Material.IRON_SPADE, ironBar + 2.0 * stick);
		blockPrices.put(Material.IRON_PICKAXE, 3.0 * ironBar + 2.0 * stick);
		blockPrices.put(Material.IRON_AXE, 3.0 * ironBar + 2.0 * stick);
		blockPrices.put(Material.FLINT_AND_STEEL,
				ironBar + blockPrices.get(Material.FLINT));
		blockPrices.put(Material.BOW,
				3.0 * stick + blockPrices.get(Material.STRING));
		blockPrices.put(Material.ARROW, (blockPrices.get(Material.FLINT)
				+ stick + blockPrices.get(Material.FEATHER)) / 4.0);
		blockPrices.put(Material.IRON_SWORD, 2.0 * ironBar + stick);
		blockPrices.put(Material.WOOD_SWORD, 2.0 * wood + stick);
		blockPrices.put(Material.WOOD_SPADE, wood + 2.0 * stick);
		blockPrices.put(Material.WOOD_PICKAXE, 3.0 * wood + 2.0 * stick);
		blockPrices.put(Material.WOOD_AXE, 3.0 * wood + 2.0 * stick);
		blockPrices.put(Material.STONE_SWORD, 2.0 * stone + stick);
		blockPrices.put(Material.STONE_SPADE, stone + 2.0 * stick);
		blockPrices.put(Material.STONE_PICKAXE, 3.0 * stone + 2.0 * stick);
		blockPrices.put(Material.STONE_AXE, 3.0 * stone + 2.0 * stick);
		blockPrices.put(Material.DIAMOND_SWORD, 2.0 * diamond + stick);
		blockPrices.put(Material.DIAMOND_SPADE, diamond + 2.0 * stick);
		blockPrices.put(Material.DIAMOND_PICKAXE, 3.0 * diamond + 2.0 * stick);
		blockPrices.put(Material.DIAMOND_AXE, 3.0 * diamond + 2.0 * stick);
		blockPrices.put(Material.BOWL, 3.0 * wood / 4.0);
		blockPrices.put(
				Material.MUSHROOM_SOUP,
				blockPrices.get(Material.RED_MUSHROOM)
						+ blockPrices.get(Material.BROWN_MUSHROOM)
						+ (3.0 * wood / 4.0));
		blockPrices.put(Material.GOLD_SWORD, 2.0 * goldBar + stick);
		blockPrices.put(Material.GOLD_SPADE, goldBar + 2.0 * stick);
		blockPrices.put(Material.GOLD_PICKAXE, 3.0 * goldBar + 2.0 * stick);
		blockPrices.put(Material.GOLD_AXE, 3.0 * goldBar + 2.0 * stick);
		blockPrices.put(Material.WOOD_HOE, 2.0 * wood + 2.0 * stick);
		blockPrices.put(Material.STONE_HOE, 2.0 * stone + 2.0 * stick);
		blockPrices.put(Material.IRON_HOE, 2.0 * ironBar + 2.0 * stick);
		blockPrices.put(Material.DIAMOND_HOE, 2.0 * diamond + 2.0 * stick);
		blockPrices.put(Material.GOLD_HOE, 2.0 * goldBar + 2.0 * stick);
		blockPrices.put(Material.BREAD, 3.0 * blockPrices.get(Material.WHEAT));
		blockPrices.put(Material.LEATHER_HELMET,
				5.0 * blockPrices.get(Material.LEATHER));
		blockPrices.put(Material.LEATHER_CHESTPLATE,
				8.0 * blockPrices.get(Material.LEATHER));
		blockPrices.put(Material.LEATHER_LEGGINGS,
				7.0 * blockPrices.get(Material.LEATHER));
		blockPrices.put(Material.LEATHER_BOOTS,
				4.0 * blockPrices.get(Material.LEATHER));
		blockPrices.put(Material.CHAINMAIL_HELMET,
				5.0 * blockPrices.get(Material.FIRE));
		blockPrices.put(Material.CHAINMAIL_CHESTPLATE,
				8.0 * blockPrices.get(Material.FIRE));
		blockPrices.put(Material.CHAINMAIL_LEGGINGS,
				7.0 * blockPrices.get(Material.FIRE));
		blockPrices.put(Material.CHAINMAIL_BOOTS,
				4.0 * blockPrices.get(Material.FIRE));
		blockPrices.put(Material.IRON_HELMET, 5.0 * ironBar);
		blockPrices.put(Material.IRON_CHESTPLATE, 8.0 * ironBar);
		blockPrices.put(Material.IRON_LEGGINGS, 7.0 * ironBar);
		blockPrices.put(Material.IRON_BOOTS, 4.0 * ironBar);
		blockPrices.put(Material.DIAMOND_HELMET, 5.0 * diamond);
		blockPrices.put(Material.DIAMOND_CHESTPLATE, 8.0 * diamond);
		blockPrices.put(Material.DIAMOND_LEGGINGS, 7.0 * diamond);
		blockPrices.put(Material.DIAMOND_BOOTS, 4.0 * diamond);
		blockPrices.put(Material.GOLD_HELMET, 5.0 * goldBar);
		blockPrices.put(Material.GOLD_CHESTPLATE, 8.0 * goldBar);
		blockPrices.put(Material.GOLD_LEGGINGS, 7.0 * goldBar);
		blockPrices.put(Material.GOLD_BOOTS, 4.0 * goldBar);
		blockPrices.put(Material.GRILLED_PORK, blockPrices.get(Material.PORK)
				+ fuel);
		blockPrices.put(Material.PAINTING,
				8.0 * stick + blockPrices.get(Material.WOOL));
		blockPrices.put(Material.GOLDEN_APPLE,
				8.0 * goldBar + blockPrices.get(Material.APPLE));
		blockPrices.put(Material.SIGN, (6.0 * wood + stick) / 3.0);
		blockPrices.put(Material.WOODEN_DOOR, 6.0 * wood);
		blockPrices.put(Material.BUCKET, 3.0 * ironBar);
		blockPrices.put(Material.MINECART, 5.0 * ironBar);
		blockPrices.put(Material.IRON_DOOR, 6.0 * ironBar);
		blockPrices.put(Material.BOAT, 6.0 * wood);
		blockPrices.put(
				Material.STORAGE_MINECART,
				blockPrices.get(Material.CHEST)
						+ blockPrices.get(Material.MINECART));
		blockPrices.put(
				Material.POWERED_MINECART,
				blockPrices.get(Material.FURNACE)
						+ blockPrices.get(Material.MINECART));
		blockPrices.put(Material.COMPASS, 4.0 * ironBar + redStone);
		blockPrices.put(Material.FISHING_ROD,
				3.0 * stick + 2.0 * blockPrices.get(Material.STRING));
		blockPrices.put(Material.WATCH, 4.0 * goldBar + redStone);
		blockPrices.put(Material.COOKED_FISH,
				blockPrices.get(Material.RAW_FISH) + fuel);
		blockPrices.put(Material.SUGAR, blockPrices.get(Material.SUGAR_CANE));
		blockPrices.put(
				Material.CAKE,
				3.0 * blockPrices.get(Material.WHEAT)
						+ blockPrices.get(Material.EGG) + 2.0
						* blockPrices.get(Material.SUGAR) + 3.0
						* blockPrices.get(Material.MILK_BUCKET));
		blockPrices.put(Material.BED,
				3.0 * wood + 3.0 * blockPrices.get(Material.WOOL));
		blockPrices.put(Material.DIODE, 3.0 * stone + redStone + 2.0
				* blockPrices.get(Material.REDSTONE_TORCH_ON));
		blockPrices.put(Material.COOKIE, 2.0 * blockPrices.get(Material.WHEAT)
				+ blockPrices.get(Material.COCOA) / 3.0);
		blockPrices.put(Material.SHEARS, 2.0 * ironBar);
		blockPrices.put(Material.COOKED_BEEF,
				blockPrices.get(Material.RAW_BEEF) + fuel);
		blockPrices.put(Material.COOKED_CHICKEN,
				blockPrices.get(Material.RAW_CHICKEN) + fuel);
		blockPrices.put(Material.GOLD_NUGGET, goldBar / 9.0);
		blockPrices.put(Material.GLASS_BOTTLE, blockPrices.get(Material.GLASS));
		// This next one is actually just the water bottle and not a potion -
		// glass bottle + water, which is free
		blockPrices
				.put(Material.POTION, blockPrices.get(Material.GLASS_BOTTLE));
		blockPrices.put(
				Material.FERMENTED_SPIDER_EYE,
				blockPrices.get(Material.SUGAR)
						+ blockPrices.get(Material.BROWN_MUSHROOM)
						+ blockPrices.get(Material.SPIDER_EYE));
		blockPrices.put(
				Material.MAGMA_CREAM,
				blockPrices.get(Material.SLIME_BALL)
						+ blockPrices.get(Material.BLAZE_POWDER));
		blockPrices.put(Material.BREWING_STAND_ITEM,
				3.0 * cobble + blockPrices.get(Material.BLAZE_ROD));
		blockPrices.put(Material.CAULDRON_ITEM, 7.0 * ironBar);
		blockPrices.put(Material.SPECKLED_MELON, 8.0 / 9.0 * goldBar
				+ blockPrices.get(Material.MELON));
		blockPrices.put(
				Material.FIREBALL,
				blockPrices.get(Material.COAL)
						+ blockPrices.get(Material.BLAZE_POWDER)
						+ blockPrices.get(Material.SULPHUR));
		blockPrices.put(
				Material.BOOK_AND_QUILL,
				blockPrices.get(Material.INK_SACK)
						+ blockPrices.get(Material.BOOK)
						+ blockPrices.get(Material.FEATHER));
		blockPrices.put(Material.ITEM_FRAME,
				8.0 * stick + blockPrices.get(Material.LEATHER));
		blockPrices.put(Material.FLOWER_POT_ITEM,
				3.0 * blockPrices.get(Material.CLAY_BRICK));
		blockPrices.put(Material.BAKED_POTATO,
				blockPrices.get(Material.POTATO_ITEM) + fuel);
		// blockPrices.put(Material.BAKED_POTATO, 20.0);
		blockPrices.put(
				Material.EMPTY_MAP,
				8.0 * blockPrices.get(Material.PAPER)
						+ blockPrices.get(Material.COMPASS));
		blockPrices.put(Material.GOLDEN_CARROT, (8.0 / 9.0 * goldBar)
				+ blockPrices.get(Material.CARROT_ITEM));
		blockPrices.put(
				Material.CARROT_STICK,
				blockPrices.get(Material.CARROT_ITEM)
						+ blockPrices.get(Material.FISHING_ROD));
		blockPrices.put(Material.PUMPKIN_PIE,
				blockPrices.get(Material.SUGAR) + blockPrices.get(Material.EGG)
						+ blockPrices.get(Material.PUMPKIN));
		blockPrices.put(Material.REDSTONE_COMPARATOR,
				3.0 * stone + 3.0 * blockPrices.get(Material.REDSTONE_TORCH_ON)
						+ blockPrices.get(Material.QUARTZ));
		blockPrices.put(
				Material.EXPLOSIVE_MINECART,
				blockPrices.get(Material.MINECART)
						+ blockPrices.get(Material.TNT));
		blockPrices.put(
				Material.HOPPER_MINECART,
				blockPrices.get(Material.MINECART)
						+ blockPrices.get(Material.HOPPER));
		blockPrices.put(Material.LEASH,
				(4.0 * blockPrices.get(Material.STRING) + blockPrices
						.get(Material.SLIME_BALL) / 2.0));
		// FISH
		// Fish are randomly caught using fishing rods. The probability of
		// catching each type of fish is in the code somewhere, but unknown to
		// me.
		// I used data from actual fishing as reported
		// http://www.minecraftforum.net/topic/2076618-fishing-topic/
		// Salmon
		fishPrices.put(1, blockPrices.get(Material.RAW_FISH) * 2.30);
		// Clown fish
		fishPrices.put(2, blockPrices.get(Material.RAW_FISH) * 42.82);
		// Puffer fish
		fishPrices.put(3, blockPrices.get(Material.RAW_FISH) * 3.89);

		// POTIONS - base potion is always with netherwart, anything else is
		// worthless/priceless :-)
		double potionValue = blockPrices.get(Material.GLASS_BOTTLE)
				+ blockPrices.get(Material.NETHER_STALK) / 3;
		// 373:16 Awkward Potion
		potionPrices.put(16, potionValue);

		// 373:8198 Night Vision Potion (3:00) - does this exist?
		potionPrices.put(8198, blockPrices.get(Material.GOLDEN_CARROT) / 3.0
				+ potionValue);
		// 373:8230 Night Vision II
		potionPrices.put(8230, blockPrices.get(Material.GOLDEN_CARROT) / 3.0
				+ potionValue + blockPrices.get(Material.GLOWSTONE_DUST) / 3.0);
		// 373:8262 Extended Night Vision Potion (8:00)
		potionPrices.put(8262, blockPrices.get(Material.GOLDEN_CARROT) / 3.0
				+ potionValue + redStone / 3.0);
		// 373: Splash Night Vision Potion (3:00)
		potionPrices.put(16390, blockPrices.get(Material.GOLDEN_CARROT) / 3.0
				+ potionValue + blockPrices.get(Material.SULPHUR) / 3.0);
		// 373:16422 Splash Night Vision II
		potionPrices.put(16422, blockPrices.get(Material.GOLDEN_CARROT) / 3.0
				+ potionValue + blockPrices.get(Material.GLOWSTONE_DUST) / 3.0
				+ blockPrices.get(Material.SULPHUR) / 3.0);
		// Splash Extended Night Vision Potion (8:00)
		potionPrices.put(16454,
				blockPrices.get(Material.GOLDEN_CARROT) / 3.0 + potionValue
						+ redStone / 3.0 + blockPrices.get(Material.SULPHUR)
						/ 3.0);

		// 373:8238 Invisibility
		potionPrices.put(8238, blockPrices.get(Material.GOLDEN_CARROT) / 3.0
				+ blockPrices.get(Material.FERMENTED_SPIDER_EYE) / 3.0
				+ potionValue);
		// 373:8270 Extended invisibility
		potionPrices.put(8270, blockPrices.get(Material.GOLDEN_CARROT) / 3.0
				+ blockPrices.get(Material.FERMENTED_SPIDER_EYE) / 3.0
				+ potionValue + redStone / 3.0);
		// 373:16430 Splash Invisibility
		potionPrices.put(16430, blockPrices.get(Material.GOLDEN_CARROT) / 3.0
				+ blockPrices.get(Material.FERMENTED_SPIDER_EYE) / 3.0
				+ potionValue + blockPrices.get(Material.SULPHUR) / 3.0);
		// 373:16462 Splash Extended invisibility
		potionPrices.put(
				16462,
				blockPrices.get(Material.GOLDEN_CARROT) / 3.0
						+ blockPrices.get(Material.FERMENTED_SPIDER_EYE) / 3.0
						+ potionValue + redStone / 3.0
						+ blockPrices.get(Material.SULPHUR) / 3.0);

		// Harm
		potionPrices.put(8268, blockPrices.get(Material.SPIDER_EYE) / 3.0
				+ blockPrices.get(Material.FERMENTED_SPIDER_EYE) / 3.0
				+ potionValue);
		// Harm 2
		potionPrices.put(8236, blockPrices.get(Material.SPIDER_EYE) / 3.0
				+ blockPrices.get(Material.FERMENTED_SPIDER_EYE) / 3.0
				+ blockPrices.get(Material.GLOWSTONE_DUST) / 3.0 + potionValue);
		// Splash Harm
		potionPrices.put(16460, blockPrices.get(Material.SPIDER_EYE) / 3.0
				+ blockPrices.get(Material.FERMENTED_SPIDER_EYE) / 3.0
				+ blockPrices.get(Material.SULPHUR) / 3.0 + potionValue);
		// Splash harm 2
		potionPrices
				.put(16428, blockPrices.get(Material.SPIDER_EYE) / 3.0
						+ blockPrices.get(Material.FERMENTED_SPIDER_EYE) / 3.0
						+ blockPrices.get(Material.GLOWSTONE_DUST) / 3.0
						+ blockPrices.get(Material.SULPHUR) / 3.0 + potionValue);
		// Poison
		potionPrices.put(8196, blockPrices.get(Material.SPIDER_EYE) / 3.0
				+ potionValue);
		// Poison II
		potionPrices.put(8228, blockPrices.get(Material.SPIDER_EYE) / 3.0
				+ blockPrices.get(Material.GLOWSTONE_DUST) / 3.0 + potionValue);
		// Poison Extended
		potionPrices.put(8260, blockPrices.get(Material.SPIDER_EYE) / 3.0
				+ redStone / 3.0 + potionValue);
		// Splash Poison
		potionPrices.put(16388, blockPrices.get(Material.SPIDER_EYE) / 3.0
				+ potionValue + blockPrices.get(Material.SULPHUR) / 3.0);
		// Splash Poison II
		potionPrices.put(16420, blockPrices.get(Material.SPIDER_EYE) / 3.0
				+ blockPrices.get(Material.GLOWSTONE_DUST) / 3.0 + potionValue
				+ blockPrices.get(Material.SULPHUR) / 3.0);
		// Splash Poison Extended
		potionPrices
				.put(16452,
						blockPrices.get(Material.SPIDER_EYE) / 3.0 + redStone
								/ 3.0 + potionValue
								+ blockPrices.get(Material.SULPHUR) / 3.0);

		// Regeneration
		potionPrices.put(8193, blockPrices.get(Material.GHAST_TEAR) / 3.0
				+ potionValue);
		// Regen II
		potionPrices.put(8225, blockPrices.get(Material.GHAST_TEAR) / 3.0
				+ potionValue + blockPrices.get(Material.GLOWSTONE_DUST) / 3.0);
		// Extended Regen
		potionPrices.put(8257, blockPrices.get(Material.GHAST_TEAR) / 3.0
				+ potionValue + redStone / 3.0);

		// Regeneration Splash
		potionPrices.put(16385, blockPrices.get(Material.GHAST_TEAR) / 3.0
				+ potionValue + blockPrices.get(Material.SULPHUR) / 3.0);
		// Regen II Splash
		potionPrices.put(16417, blockPrices.get(Material.GHAST_TEAR) / 3.0
				+ potionValue + blockPrices.get(Material.GLOWSTONE_DUST) / 3.0
				+ blockPrices.get(Material.SULPHUR) / 3.0);
		// Extended Regen Splash
		potionPrices.put(16449,
				blockPrices.get(Material.GHAST_TEAR) / 3.0 + potionValue
						+ redStone / 3.0 + blockPrices.get(Material.SULPHUR)
						/ 3.0);

		// Strength
		potionPrices.put(8201, blockPrices.get(Material.BLAZE_POWDER) / 3.0
				+ potionValue);
		// Strength II
		potionPrices.put(8233, blockPrices.get(Material.BLAZE_POWDER) / 3.0
				+ potionValue + blockPrices.get(Material.GLOWSTONE_DUST) / 3.0);
		// Extended Strength
		potionPrices.put(8265, blockPrices.get(Material.BLAZE_POWDER) / 3.0
				+ potionValue + redStone / 3.0);

		// Strength Splash
		potionPrices.put(16393, blockPrices.get(Material.BLAZE_POWDER) / 3.0
				+ potionValue + blockPrices.get(Material.SULPHUR) / 3.0);
		// Strength II Splash
		potionPrices.put(16425, blockPrices.get(Material.BLAZE_POWDER) / 3.0
				+ potionValue + blockPrices.get(Material.GLOWSTONE_DUST) / 3.0
				+ blockPrices.get(Material.SULPHUR) / 3.0);
		// Extended Strength Splash
		potionPrices.put(16457,
				blockPrices.get(Material.BLAZE_POWDER) / 3.0 + potionValue
						+ redStone / 3.0 + blockPrices.get(Material.SULPHUR)
						/ 3.0);

		// Speed
		potionPrices.put(8194, blockPrices.get(Material.SUGAR) / 3.0
				+ potionValue);
		// Speed II
		potionPrices.put(8226, blockPrices.get(Material.SUGAR) / 3.0
				+ potionValue + blockPrices.get(Material.GLOWSTONE_DUST) / 3.0);
		// Extended Speed
		potionPrices.put(8258, blockPrices.get(Material.SUGAR) / 3.0
				+ potionValue + redStone / 3.0);

		// Speed Splash
		potionPrices.put(16386, blockPrices.get(Material.SUGAR) / 3.0
				+ potionValue + blockPrices.get(Material.SULPHUR) / 3.0);
		// Speed II Splash
		potionPrices.put(16418, blockPrices.get(Material.SUGAR) / 3.0
				+ potionValue + blockPrices.get(Material.GLOWSTONE_DUST) / 3.0
				+ blockPrices.get(Material.SULPHUR) / 3.0);
		// Extended Speed Splash
		potionPrices.put(16450,
				blockPrices.get(Material.SUGAR) / 3.0 + potionValue + redStone
						/ 3.0 + blockPrices.get(Material.SULPHUR) / 3.0);

		// Fire Resistance
		potionPrices.put(8195, blockPrices.get(Material.MAGMA_CREAM) / 3.0
				+ potionValue);
		// Fire Resistance II
		potionPrices.put(8227, blockPrices.get(Material.MAGMA_CREAM) / 3.0
				+ potionValue + blockPrices.get(Material.GLOWSTONE_DUST) / 3.0);
		// Extended Fire Resistance
		potionPrices.put(8259, blockPrices.get(Material.MAGMA_CREAM) / 3.0
				+ potionValue + redStone / 3.0);

		// Fire Resistance Splash
		potionPrices.put(16387, blockPrices.get(Material.MAGMA_CREAM) / 3.0
				+ potionValue + blockPrices.get(Material.SULPHUR) / 3.0);
		// Fire Resistance II Splash
		potionPrices.put(16419, blockPrices.get(Material.MAGMA_CREAM) / 3.0
				+ potionValue + blockPrices.get(Material.GLOWSTONE_DUST) / 3.0
				+ blockPrices.get(Material.SULPHUR) / 3.0);
		// Extended Fire Resistance Splash
		potionPrices.put(16451,
				blockPrices.get(Material.MAGMA_CREAM) / 3.0 + potionValue
						+ redStone / 3.0 + blockPrices.get(Material.SULPHUR)
						/ 3.0);

		// Weakness
		potionPrices.put(8200, blockPrices.get(Material.FERMENTED_SPIDER_EYE)
				/ 3.0 + potionValue);
		// Weakness II
		potionPrices.put(8232, blockPrices.get(Material.FERMENTED_SPIDER_EYE)
				/ 3.0 + potionValue + blockPrices.get(Material.GLOWSTONE_DUST)
				/ 3.0);
		// Extended Weakness
		potionPrices.put(8264, blockPrices.get(Material.FERMENTED_SPIDER_EYE)
				/ 3.0 + potionValue + redStone / 3.0);

		// Weakness Splash
		potionPrices.put(16392, blockPrices.get(Material.FERMENTED_SPIDER_EYE)
				/ 3.0 + potionValue + blockPrices.get(Material.SULPHUR) / 3.0);
		// Weakness II Splash
		potionPrices.put(16424, blockPrices.get(Material.FERMENTED_SPIDER_EYE)
				/ 3.0 + potionValue + blockPrices.get(Material.GLOWSTONE_DUST)
				/ 3.0 + blockPrices.get(Material.SULPHUR) / 3.0);
		// Extended Weakness Splash
		potionPrices.put(
				16456,
				blockPrices.get(Material.FERMENTED_SPIDER_EYE) / 3.0
						+ potionValue + redStone / 3.0
						+ blockPrices.get(Material.SULPHUR) / 3.0);

		// Slowness
		potionPrices.put(8202, blockPrices.get(Material.FERMENTED_SPIDER_EYE)
				/ 3.0 + blockPrices.get(Material.SUGAR) / 3.0 + potionValue);
		// Slowness II
		potionPrices.put(8234, blockPrices.get(Material.FERMENTED_SPIDER_EYE)
				/ 3.0 + blockPrices.get(Material.SUGAR) / 3.0 + potionValue
				+ blockPrices.get(Material.GLOWSTONE_DUST) / 3.0);
		// Extended Slowness
		potionPrices.put(8266, blockPrices.get(Material.FERMENTED_SPIDER_EYE)
				/ 3.0 + blockPrices.get(Material.SUGAR) / 3.0 + potionValue
				+ redStone / 3.0);

		// Slowness Splash
		potionPrices.put(16394, blockPrices.get(Material.FERMENTED_SPIDER_EYE)
				/ 3.0 + blockPrices.get(Material.SUGAR) / 3.0 + potionValue
				+ blockPrices.get(Material.SULPHUR) / 3.0);
		// Slowness II Splash
		potionPrices.put(16426,
				blockPrices.get(Material.FERMENTED_SPIDER_EYE) / 3.0
						+ blockPrices.get(Material.SUGAR) / 3.0 + potionValue
						+ blockPrices.get(Material.GLOWSTONE_DUST) / 3.0
						+ blockPrices.get(Material.SULPHUR) / 3.0);
		// Extended Slowness Splash
		potionPrices.put(16458, blockPrices.get(Material.FERMENTED_SPIDER_EYE)
				/ 3.0 + blockPrices.get(Material.SUGAR) / 3.0 + potionValue
				+ redStone / 3.0 + blockPrices.get(Material.SULPHUR) / 3.0);

		// Healing
		potionPrices.put(8197, blockPrices.get(Material.SPECKLED_MELON) / 3.0
				+ potionValue);
		// Healing II
		potionPrices.put(8229, blockPrices.get(Material.SPECKLED_MELON) / 3.0
				+ potionValue + blockPrices.get(Material.GLOWSTONE_DUST) / 3.0);
		// Extended Healing
		potionPrices.put(8261, blockPrices.get(Material.SPECKLED_MELON) / 3.0
				+ potionValue + redStone / 3.0);

		// Healing Splash
		potionPrices.put(16389, blockPrices.get(Material.SPECKLED_MELON) / 3.0
				+ potionValue + blockPrices.get(Material.SULPHUR) / 3.0);
		// Healing II Splash
		potionPrices.put(16421, blockPrices.get(Material.SPECKLED_MELON) / 3.0
				+ potionValue + blockPrices.get(Material.GLOWSTONE_DUST) / 3.0
				+ blockPrices.get(Material.SULPHUR) / 3.0);
		// Extended Healing Splash
		potionPrices.put(16453,
				blockPrices.get(Material.SPECKLED_MELON) / 3.0 + potionValue
						+ redStone / 3.0 + blockPrices.get(Material.SULPHUR)
						/ 3.0);

		// Technically this potion is made from PUFFERFISH 349:3
		// Water Breathing
		potionPrices.put(8205, fishPrices.get(3) / 3.0 + potionValue);
		// Water Breathing II
		potionPrices.put(8237, fishPrices.get(3) / 3.0 + potionValue
				+ blockPrices.get(Material.GLOWSTONE_DUST) / 3.0);
		// Extended Water Breathing
		potionPrices.put(8269, fishPrices.get(3) / 3.0 + potionValue + redStone
				/ 3.0);

		// Water Breathing Splash
		potionPrices.put(16397, fishPrices.get(3) / 3.0 + potionValue
				+ blockPrices.get(Material.SULPHUR) / 3.0);
		// Water Breathing II Splash
		potionPrices.put(
				16429,
				fishPrices.get(3) / 3.0 + potionValue
						+ blockPrices.get(Material.GLOWSTONE_DUST) / 3.0
						+ blockPrices.get(Material.SULPHUR) / 3.0);
		// Extended Water Breathing Splash
		potionPrices.put(16461, fishPrices.get(3) / 3.0 + potionValue
				+ redStone / 3.0 + blockPrices.get(Material.SULPHUR) / 3.0);

		/*
		 * 
		 * Potion 373:8193 Regeneration Potion (0:45) 373:8194 Swiftness Potion
		 * (3:00) 373:8195 Fire Resistance Potion (3:00) 373:8196 Poison Potion
		 * (0:45) 373:8197 Healing Potion 373:8200 Weakness Potion (1:30)
		 * 373:8201 Strength Potion (3:00) 373:8202 Slowness Potion (1:30)
		 * 373:8204 Harming Potion 373:8205 Water Breathing Potion (3:00)
		 * 373:8206 Invisibility Potion (3:00) 373:8225 Regeneration Potion II
		 * (0:22) 373:8226 Swiftness Potion II (1:30) 373:8228 Poison Potion II
		 * (0:22) 373:8229 Healing Potion II 373:8233 Strength Potion II (1:30)
		 * 373:8236 Harming Potion II 373:8257 Regeneration Potion (2:00)
		 * 373:8258 Swiftness Potion (8:00) 373:8259 Fire Resistance Potion
		 * (8:00) 373:8260 Poison Potion (2:00) 373:8264 Weakness Potion (4:00)
		 * 373:8265 Strength Potion (8:00) 373:8266 Slowness Potion (4:00)
		 * 373:8269 Water Breathing Potion (8:00) 373:8270 Invisibility Potion
		 * (8:00) 373:8289 Regeneration Potion II (1:00) 373:8290 Swiftness
		 * Potion II (4:00) 373:8292 Poison Potion II (1:00) 373:8297 Strength
		 * Potion II (4:00) 373:16385 Regeneration Splash (0:33) 373:16386
		 * Swiftness Splash (2:15) 373:16387 Fire Resistance Splash (2:15)
		 * 373:16388 Poison Splash (0:33) 373:16389 Healing Splash 373:16390
		 * Night Vision Splash (2:15) 373:16392 Weakness Splash (1:07) 373:16393
		 * Strength Splash (2:15) 373:16394 Slowness Splash (1:07) 373:16396
		 * Harming Splash 373:16397 Breathing Splash (2:15) 373:16398
		 * Invisibility Splash (2:15) 373:16417 Regeneration Splash II (0:16)
		 * 373:16418 Swiftness Splash II (1:07) 373:16420 Poison Splash II
		 * (0:16) 373:16421 Healing Splash II 373:16425 Strength Splash II
		 * (1:07) 373:16428 Harming Splash II 373:16449 Regeneration Splash
		 * (1:30) 373:16450 Swiftness Splash (6:00) 373:16451 Fire Resistance
		 * Splash (6:00) 373:16452 Poison Splash (1:30) 373:e Night Vision
		 * Splash (6:00) 373:16456 Weakness Splash (3:00) 373:16457 Strength
		 * Splash (6:00) 373:16458 Slowness Splash (3:00) 373:16461 Breathing
		 * Splash (6:00) 373:16462 Invisibility Splash (6:00) 373:16481
		 * Regeneration Splash II (0:45) 373:16482 Swiftness Splash II (3:00)
		 * 373:16484 Poison Splash II (0:45) 373:16489 Strength Splash II (3:00)
		 */
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
			vanillaName = prettifyText(getDataName(i));
		} else {
			vanillaName = displayName;
		}
		if (!i.getEnchantments().isEmpty()) {
			vanillaName += " (Enchanted)";
			// Loop through every enchantment on the item
			/*
			for (Map.Entry<Enchantment, Integer> item : i.getEnchantments()
					.entrySet()) {
				String name = item.getKey().getName();
				vanillaName += "\n" + name + " " + toRoman(item.getValue());
			}
			*/
		}
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
	 * Converts a given ItemStack into a pretty string
	 * 
	 * @param item
	 *            The item stack
	 * @return A string with the name of the item.
	 */
	private static String getDataName(ItemStack item) {
		Material mat = item.getType();
		// Find out durability, which indicates additional information on the
		// item, color, etc.
		short damage = item.getDurability();
		switch (mat) {
		case WOOL:
			switch ((int) damage) {
			case 0:
				return "WHITE_WOOL";
			case 1:
				return "ORANGE_WOOL";
			case 2:
				return "MAGENTA_WOOL";
			case 3:
				return "LIGHT_BLUE_WOOL";
			case 4:
				return "YELLOW_WOOL";
			case 5:
				return "LIME_WOOL";
			case 6:
				return "PINK_WOOL";
			case 7:
				return "GRAY_WOOL";
			case 8:
				return "LIGHT_GRAY_WOOL";
			case 9:
				return "CYAN_WOOL";
			case 10:
				return "PURPLE_WOOL";
			case 11:
				return "BLUE_WOOL";
			case 12:
				return "BROWN_WOOL";
			case 13:
				return "GREEN_WOOL";
			case 14:
				return "RED_WOOL";
			case 15:
				return "BLACK_WOOL";
			}
			return mat.toString();
		case INK_SACK:
			switch ((int) damage) {
			case 0:
				return "INK_SAC";
			case 1:
				return "ROSE_RED";
			case 2:
				return "CACTUS_GREEN";
			case 3:
				return "COCOA_BEANS";
			case 4:
				return "LAPIS_LAZULI";
			case 5:
				return "PURPLE_DYE";
			case 6:
				return "CYAN_DYE";
			case 7:
				return "LIGHT_GRAY_DYE";
			case 8:
				return "GRAY_DYE";
			case 9:
				return "PINK_DYE";
			case 10:
				return "LIME_DYE";
			case 11:
				return "DANDELION_YELLOW";
			case 12:
				return "LIGHT_BLUE_DYE";
			case 13:
				return "MAGENTA_DYE";
			case 14:
				return "ORANGE_DYE";
			case 15:
				return "BONE_MEAL";
			}
			return mat.toString();
		case SMOOTH_BRICK:
			switch ((int) damage) {
			case 0:
				return "STONE_BRICKS";
			case 1:
				return "MOSSY_STONE_BRICKS";
			case 2:
				return "CRACKED_STONE_BRICKS";
			case 3:
				return "CHISELED_STONE_BRICKS";
			}
			return mat.toString();
		case POTION:
			// Special case,.. Why?
			if (damage == 0)
				return "WATER_BOTTLE";
			Potion pot;
			// Convert the item stack to a potion. The try is just in case this
			// is not a potion, which it should be

			try {
				pot = Potion.fromItemStack(item);
			} catch (Exception e) {
				return "CUSTOM_POTION";
			}
			// Now we can parse out what the potion is from its effects and type
			String prefix = "";
			String suffix = "";
			if (pot.getLevel() > 0)
				suffix += "_" + pot.getLevel();
			if (pot.hasExtendedDuration())
				prefix += "EXTENDED_";
			if (pot.isSplash())
				prefix += "SPLASH_";
			// These are the useless or unused potions. Usually, these can only
			// be obtained by /give
			if (pot.getEffects().isEmpty()) {
				switch ((int) damage) {
				case 64:
					return prefix + "MUNDANE_POTION" + suffix;
				case 7:
					return prefix + "CLEAR_POTION" + suffix;
				case 11:
					return prefix + "DIFFUSE_POTION" + suffix;
				case 13:
					return prefix + "ARTLESS_POTION" + suffix;
				case 15:
					return prefix + "THIN_POTION" + suffix;
				case 16:
					return prefix + "AWKWARD_POTION" + suffix;
				case 32:
					return prefix + "THICK_POTION" + suffix;
				case 23:
					return prefix + "BUNGLING_POTION" + suffix;
				case 27:
					return prefix + "SMOOTH_POTION" + suffix;
				case 31:
					return prefix + "DEBONAIR_POTION" + suffix;
				case 39:
					return prefix + "CHARMING_POTION" + suffix;
				case 43:
					return prefix + "REFINED_POTION" + suffix;
				case 47:
					return prefix + "SPARKLING_POTION" + suffix;
				case 48:
					return prefix + "POTENT_POTION" + suffix;
				case 55:
					return prefix + "RANK_POTION" + suffix;
				case 59:
					return prefix + "ACRID_POTION" + suffix;
				case 63:
					return prefix + "STINKY_POTION" + suffix;
				}
			} else {
				String effects = "";
				for (PotionEffect effect : pot.getEffects()) {
					effects += effect.toString().split(":")[0];
				}
				return prefix + effects + suffix;
			}
			return mat.toString();
		case SAPLING:
			switch ((int) damage) {
			case 0:
				return "OAK_SAPLING";
			case 1:
				return "PINE_SAPLING";
			case 2:
				return "BIRCH_SAPLING";
			case 3:
				return "JUNGLE_TREE_SAPLING";
			case 4:
				return "Acacia_Sapling";
			case 5:
				return "Dark_Oak_Sapling";
			}
			return mat.toString();

		case WOOD:
			switch ((int) damage) {
			case 0:
				return "OAK_PLANKS";
			case 1:
				return "PINE_PLANKS";
			case 2:
				return "BIRCH_PLANKS";
			case 3:
				return "JUNGLE_PLANKS";
			case 4:
				return "Acacia Planks";
			case 5:
				return "Dark Oak Planks";
			}
			return mat.toString();
		case LOG:
			switch (damage) {
			case 0:
				return "OAK_LOG";
			case 1:
				return "PINE_LOG";
			case 2:
				return "BIRCH_LOG";
			case 3:
				return "JUNGLE_LOG";
			}
			return mat.toString();
		case LEAVES:
			damage = (short) (damage % 4);
			switch (damage) {
			case 0:
				return "OAK_LEAVES";
			case 1:
				return "PINE_LEAVES";
			case 2:
				return "BIRCH_LEAVES";
			case 3:
				return "JUNGLE_LEAVES";
			} // Note Acacia and Dark Oak are LEAVES_2 for some reason...
		case COAL:
			switch (damage) {
			case 0:
				return "COAL";
			case 1:
				return "CHARCOAL";
			}
			return mat.toString();
		case SANDSTONE:
			switch ((int) damage) {
			case 0:
				return "SANDSTONE";
			case 1:
				return "CHISELED_SANDSTONE";
			case 2:
				return "SMOOTH_SANDSTONE";
			}
			return mat.toString();
		case LONG_GRASS:
			switch ((int) damage) {
			case 0:
				return "DEAD_SHRUB";
			case 1:
				return "TALL_GRASS";
			case 2:
				return "FERN";
			}
			return mat.toString();
		case STEP:
			switch ((int) damage) {
			case 0:
				return "STONE_SLAB";
			case 1:
				return "SANDSTONE_SLAB";
			case 2:
				return "WOODEN_SLAB";
			case 3:
				return "COBBLESTONE_SLAB";
			case 4:
				return "BRICK_SLAB";
			case 5:
				return "STONE_BRICK_SLAB";
			case 6:
				return "Nether Brick Slab";
			case 7:
				return "Quartz Slab";
			}
			return mat.toString();
		case MONSTER_EGG:
			switch ((int) damage) {
			case 50:
				return "CREEPER_EGG";
			case 51:
				return "SKELETON_EGG";
			case 52:
				return "SPIDER_EGG";
			case 53:
				// Unused
				return "GIANT_EGG";
			case 54:
				return "ZOMBIE_EGG";
			case 55:
				return "SLIME_EGG";
			case 56:
				return "GHAST_EGG";
			case 57:
				return "ZOMBIE_PIGMAN_EGG";
			case 58:
				return "ENDERMAN_EGG";
			case 59:
				return "CAVE_SPIDER_EGG";
			case 60:
				return "SILVERFISH_EGG";
			case 61:
				return "BLAZE_EGG";
			case 62:
				return "MAGMA_CUBE_EGG";
			case 63:
				return "ENDER_DRAGON_EGG";
			case 65:
				return "BAT_EGG";
			case 66:
				return "WITCH_EGG";
			case 90:
				return "PIG_EGG";
			case 91:
				return "SHEEP_EGG";
			case 92:
				return "COW_EGG";
			case 93:
				return "CHICKEN_EGG";
			case 94:
				return "SQUID_EGG";
			case 95:
				return "WOLF_EGG";
			case 96:
				return "MOOSHROOM_EGG";
			case 97:
				return "SNOW_GOLEM_EGG";
			case 98:
				return "OCELOT_EGG";
			case 99:
				return "IRON_GOLEM_EGG";
			case 100:
				return "HORSE_EGG";
			case 120:
				return "VILLAGER_EGG";
			case 200:
				return "ENDER_CRYSTAL_EGG";
			case 14:
				return "PRIMED_TNT_EGG";
			}
			return mat.toString();
		case SKULL_ITEM:
			switch ((int) damage) {
			case 0:
				return "SKELETON_SKULL";
			case 1:
				return "WITHER_SKULL";
			case 2:
				return "ZOMBIE_HEAD";
			case 3:
				return "PLAYER_HEAD";
			case 4:
				return "CREEPER_HEAD";
			}
			break;
		case REDSTONE_TORCH_OFF:
		case REDSTONE_TORCH_ON:
			return "REDSTONE_TORCH";
		case NETHER_STALK:
			return "NETHER_WART";
		case WEB:
			return "COBWEB";
		case THIN_GLASS:
			return "GLASS_PANE";
		case IRON_FENCE:
			return "IRON_BARS";
		case WORKBENCH:
			return "CRAFTING_TABLE";
		case REDSTONE_LAMP_ON:
		case REDSTONE_LAMP_OFF:
			return "REDSTONE_LAMP";
		case POTATO_ITEM:
			return "POTATO";
		case SULPHUR:
			return "GUNPOWDER";
		case CARROT_ITEM:
			return "CARROT";
		case GOLDEN_APPLE:
			switch ((int) damage) {
			case 0:
				return "GOLDEN_APPLE";
			case 1:
				return "ENCHANTED_GOLDEN_APPLE";
			}
			break;
		case FLOWER_POT:
			return "FLOWER_POT";
		case ANVIL:
			switch ((int) damage) {
			case 0:
				return "ANVIL";
			case 1:
				return "SLIGHTLY_DAMAGED_ANVIL";
			case 2:
				return "VERY_DAMAGED:ANVIL";
			}
			break;
		case EXP_BOTTLE:
			return "BOTTLE_O'_ENCHANTING";
		case FIREWORK_CHARGE:
			return "FIREWORK_STAR";
		case FIREBALL:
			return "FIREWORK_CHARGE";
		case ACACIA_STAIRS:
			break;
		case ACTIVATOR_RAIL:
			break;
		case AIR:
			break;
		case APPLE:
			break;
		case ARROW:
			break;
		case BAKED_POTATO:
			break;
		case BEACON:
			break;
		case BED:
			break;
		case BEDROCK:
			break;
		case BED_BLOCK:
			break;
		case BIRCH_WOOD_STAIRS:
			break;
		case BLAZE_POWDER:
			break;
		case BLAZE_ROD:
			break;
		case BOAT:
			break;
		case BONE:
			break;
		case BOOK:
			break;
		case BOOKSHELF:
			break;
		case BOOK_AND_QUILL:
			break;
		case BOW:
			break;
		case BOWL:
			break;
		case BREAD:
			break;
		case BREWING_STAND:
			break;
		case BREWING_STAND_ITEM:
			return "Brewing Stand";
		case BRICK:
			break;
		case BRICK_STAIRS:
			break;
		case BROWN_MUSHROOM:
			break;
		case BUCKET:
			break;
		case BURNING_FURNACE:
			break;
		case CACTUS:
			break;
		case CAKE:
			break;
		case CAKE_BLOCK:
			break;
		case CARPET:
			switch ((int) damage) {
			case 0:
				return "WHITE_CARPET";
			case 1:
				return "ORANGE_CARPET";
			case 2:
				return "MAGENTA_CARPET";
			case 3:
				return "LIGHT_BLUE_CARPET";
			case 4:
				return "YELLOW_CARPET";
			case 5:
				return "LIME_CARPET";
			case 6:
				return "PINK_CARPET";
			case 7:
				return "GRAY_CARPET";
			case 8:
				return "LIGHT_GRAY_CARPET";
			case 9:
				return "CYAN_CARPET";
			case 10:
				return "PURPLE_CARPET";
			case 11:
				return "BLUE_CARPET";
			case 12:
				return "BROWN_CARPET";
			case 13:
				return "GREEN_CARPET";
			case 14:
				return "RED_CARPET";
			case 15:
				return "BLACK_CARPET";
			}
			return mat.toString();
		case CARROT:
			break;
		case CARROT_STICK:
			break;
		case CAULDRON:
			break;
		case CAULDRON_ITEM:
			return "CAULDRON";
		case CHAINMAIL_BOOTS:
			break;
		case CHAINMAIL_CHESTPLATE:
			break;
		case CHAINMAIL_HELMET:
			break;
		case CHAINMAIL_LEGGINGS:
			break;
		case CHEST:
			break;
		case CLAY:
			break;
		case CLAY_BALL:
			break;
		case CLAY_BRICK:
			break;
		case COAL_BLOCK:
			break;
		case COAL_ORE:
			break;
		case COBBLESTONE:
			break;
		case COBBLESTONE_STAIRS:
			break;
		case COBBLE_WALL:
			break;
		case COCOA:
			break;
		case COMMAND:
			return "COMMAND_BLOCK";
		case COMMAND_MINECART:
			break;
		case COMPASS:
			break;
		case COOKED_BEEF:
			break;
		case COOKED_CHICKEN:
			break;
		case COOKED_FISH:
			break;
		case COOKIE:
			break;
		case CROPS:
			break;
		case DARK_OAK_STAIRS:
			break;
		case DAYLIGHT_DETECTOR:
			break;
		case DEAD_BUSH:
			break;
		case DETECTOR_RAIL:
			break;
		case DIAMOND:
			break;
		case DIAMOND_AXE:
			break;
		case DIAMOND_BARDING:
			break;
		case DIAMOND_BLOCK:
			break;
		case DIAMOND_BOOTS:
			break;
		case DIAMOND_CHESTPLATE:
			break;
		case DIAMOND_HELMET:
			break;
		case DIAMOND_HOE:
			break;
		case DIAMOND_LEGGINGS:
			break;
		case DIAMOND_ORE:
			break;
		case DIAMOND_PICKAXE:
			break;
		case DIAMOND_SPADE:
			return "Diamond Shovel";
		case DIAMOND_SWORD:
			break;
		case DIODE:
			break;
		case DIODE_BLOCK_OFF:
			break;
		case DIODE_BLOCK_ON:
			break;
		case DIRT:
			break;
		case DISPENSER:
			break;
		case DOUBLE_PLANT:
			switch ((int) damage) {
			case 0:
				return "SUNFLOWER";
			case 1:
				return "LILAC";
			case 2:
				return "DOUBLE_TALL_GRASS";
			case 3:
				return "LARGE_FERN";
			case 4:
				return "Rose Bush";
			case 5:
				return "Peony";
			}
			break;
		case DOUBLE_STEP:
			switch ((int) damage) {
			case 0:
				return "STONE_SLAB (DOUBLE)";
			case 1:
				return "SANDSTONE_SLAB (DOUBLE)";
			case 2:
				return "WOODEN_SLAB (DOUBLE)";
			case 3:
				return "COBBLESTONE_SLAB (DOUBLE)";
			case 4:
				return "BRICK_SLAB (DOUBLE)";
			case 5:
				return "STONE_BRICK_SLAB (DOUBLE)";
			case 6:
				return "Nether Brick Slab (DOUBLE)";
			case 7:
				return "Quartz Slab (DOUBLE)";
			case 8:
				return "Smooth Stone Slab (Double)";
			case 9:
				return "Smooth Sandstone Slab (Double)";
			}
			break;
		case DRAGON_EGG:
			break;
		case DROPPER:
			break;
		case EGG:
			break;
		case EMERALD:
			break;
		case EMERALD_BLOCK:
			break;
		case EMERALD_ORE:
			break;
		case EMPTY_MAP:
			break;
		case ENCHANTED_BOOK:
			break;
		case ENCHANTMENT_TABLE:
			break;
		case ENDER_CHEST:
			break;
		case ENDER_PEARL:
			break;
		case ENDER_PORTAL:
			break;
		case ENDER_PORTAL_FRAME:
			break;
		case ENDER_STONE:
			break;
		case EXPLOSIVE_MINECART:
			break;
		case EYE_OF_ENDER:
			break;
		case FEATHER:
			break;
		case FENCE:
			break;
		case FENCE_GATE:
			break;
		case FERMENTED_SPIDER_EYE:
			break;
		case FIRE:
			break;
		case FIREWORK:
			return "Firework Rocket";
		case FISHING_ROD:
			break;
		case FLINT:
			break;
		case FLINT_AND_STEEL:
			break;
		case FLOWER_POT_ITEM:
			return "Flower Pot";
		case FURNACE:
			break;
		case GHAST_TEAR:
			break;
		case GLASS:
			break;
		case GLASS_BOTTLE:
			break;
		case GLOWING_REDSTONE_ORE:
			break;
		case GLOWSTONE:
			break;
		case GLOWSTONE_DUST:
			break;
		case GOLDEN_CARROT:
			break;
		case GOLD_AXE:
			break;
		case GOLD_BARDING:
			return "Gold Horse Armor";
		case GOLD_BLOCK:
			break;
		case GOLD_BOOTS:
			return "Golden Boots";
		case GOLD_CHESTPLATE:
			return "Golden Chestplate";
		case GOLD_HELMET:
			return "Golden Helmet";
		case GOLD_HOE:
			return "Golden Hoe";
		case GOLD_INGOT:
			break;
		case GOLD_LEGGINGS:
			return "Golden Leggings";
		case GOLD_NUGGET:
			break;
		case GOLD_ORE:
			break;
		case GOLD_PICKAXE:
			return "Golden_Pickaxe";
		case GOLD_PLATE:
			return "Weighted_Pressure_Plate_(Light)";
		case GOLD_RECORD:
			return "Golden Record";
		case GOLD_SPADE:
			return "Golden Shovel";
		case GOLD_SWORD:
			return "Golden Sword";
		case GRASS:
			break;
		case GRAVEL:
			break;
		case GREEN_RECORD:
			break;
		case GRILLED_PORK:
			break;
		case HARD_CLAY:
			break;
		case HAY_BLOCK:
			break;
		case HOPPER:
			break;
		case HOPPER_MINECART:
			break;
		case HUGE_MUSHROOM_1:
			break;
		case HUGE_MUSHROOM_2:
			break;
		case ICE:
			break;
		case IRON_AXE:
			break;
		case IRON_BARDING:
			return "Iron_Horse_Armor";
		case IRON_BLOCK:
			break;
		case IRON_BOOTS:
			break;
		case IRON_CHESTPLATE:
			break;
		case IRON_DOOR:
			break;
		case IRON_DOOR_BLOCK:
			break;
		case IRON_HELMET:
			break;
		case IRON_HOE:
			break;
		case IRON_INGOT:
			break;
		case IRON_LEGGINGS:
			break;
		case IRON_ORE:
			break;
		case IRON_PICKAXE:
			break;
		case IRON_PLATE:
			break;
		case IRON_SPADE:
			return "Iron_Shovel";
		case IRON_SWORD:
			break;
		case ITEM_FRAME:
			break;
		case JACK_O_LANTERN:
			return "Jack_O'Lantern";
		case JUKEBOX:
			break;
		case JUNGLE_WOOD_STAIRS:
			break;
		case LADDER:
			break;
		case LAPIS_BLOCK:
			break;
		case LAPIS_ORE:
			break;
		case LAVA:
			break;
		case LAVA_BUCKET:
			break;
		case LEASH:
			break;
		case LEATHER:
			break;
		case LEATHER_BOOTS:
			break;
		case LEATHER_CHESTPLATE:
			break;
		case LEATHER_HELMET:
			break;
		case LEATHER_LEGGINGS:
			break;
		case LEAVES_2:
			switch ((int) damage) {
			case 0:
				return "Acacia_Leaves";
			case 1:
				return "Dark_Oak_Leaves";
			}
			return mat.toString();
		case LEVER:
			break;
		case LOG_2:
			switch ((int) damage) {
			case 0:
				return "ACACIA_LOG";
			case 1:
				return "DARK_OAK_LOG";
			}
			return mat.toString();
		case MAGMA_CREAM:
			break;
		case MAP:
			break;
		case MELON:
			break;
		case MELON_BLOCK:
			break;
		case MELON_SEEDS:
			break;
		case MELON_STEM:
			break;
		case MILK_BUCKET:
			break;
		case MINECART:
			break;
		case MOB_SPAWNER:
			break;
		case MONSTER_EGGS:
			break;
		case MOSSY_COBBLESTONE:
			break;
		case MUSHROOM_SOUP:
			break;
		case MYCEL:
			return "MYCELIUM";
		case NAME_TAG:
			break;
		case NETHERRACK:
			break;
		case NETHER_BRICK:
			break;
		case NETHER_BRICK_ITEM:
			return "Nether Brick (Small)";
		case NETHER_BRICK_STAIRS:
			break;
		case NETHER_FENCE:
			break;
		case NETHER_STAR:
			break;
		case NETHER_WARTS:
			break;
		case NOTE_BLOCK:
			break;
		case OBSIDIAN:
			break;
		case PACKED_ICE:
			break;
		case PAINTING:
			break;
		case PAPER:
			break;
		case PISTON_BASE:
			break;
		case PISTON_EXTENSION:
			break;
		case PISTON_MOVING_PIECE:
			break;
		case PISTON_STICKY_BASE:
			break;
		case POISONOUS_POTATO:
			break;
		case PORK:
			break;
		case PORTAL:
			break;
		case POTATO:
			break;
		case POWERED_MINECART:
			break;
		case POWERED_RAIL:
			break;
		case PUMPKIN:
			break;
		case PUMPKIN_PIE:
			break;
		case PUMPKIN_SEEDS:
			break;
		case PUMPKIN_STEM:
			break;
		case QUARTZ:
			break;
		case QUARTZ_BLOCK:
			break;
		case QUARTZ_ORE:
			break;
		case QUARTZ_STAIRS:
			break;
		case RAILS:
			break;
		case RAW_BEEF:
			break;
		case RAW_CHICKEN:
			break;
		case RAW_FISH:
			break;
		case RECORD_10:
			return "Ward Record";
		case RECORD_11:
			break;
		case RECORD_12:
			return "Wait Record (12)";
		case RECORD_3:
			return "Blocks Record (3)";
		case RECORD_4:
			return "Chirp Record (4)";
		case RECORD_5:
			return "Far Record (5)";
		case RECORD_6:
			return "Mall Record (6)";
		case RECORD_7:
			return "Mellohi Record (7)";
		case RECORD_8:
			return "Stal Record (8)";
		case RECORD_9:
			return "Strad Record (9)";
		case REDSTONE:
			break;
		case REDSTONE_BLOCK:
			break;
		case REDSTONE_COMPARATOR:
			break;
		case REDSTONE_COMPARATOR_OFF:
			break;
		case REDSTONE_COMPARATOR_ON:
			break;
		case REDSTONE_ORE:
			break;
		case REDSTONE_WIRE:
			break;
		case RED_MUSHROOM:
			break;
		case RED_ROSE:
			return "Poppy";
		case ROTTEN_FLESH:
			break;
		case SADDLE:
			break;
		case SAND:
			break;
		case SANDSTONE_STAIRS:
			break;
		case SEEDS:
			break;
		case SHEARS:
			break;
		case SIGN:
			break;
		case SIGN_POST:
			break;
		case SKULL:
			break;
		case SLIME_BALL:
			break;
		case SMOOTH_STAIRS:
			break;
		case SNOW:
			break;
		case SNOW_BALL:
			break;
		case SNOW_BLOCK:
			break;
		case SOIL:
			break;
		case SOUL_SAND:
			break;
		case SPECKLED_MELON:
			return "Glistering Melon";
		case SPIDER_EYE:
			break;
		case SPONGE:
			break;
		case SPRUCE_WOOD_STAIRS:
			break;
		case STAINED_CLAY:
			switch ((int) damage) {
			case 0:
				return "WHITE_STAINED_CLAY";
			case 1:
				return "ORANGE_STAINED_CLAY";
			case 2:
				return "MAGENTA_STAINED_CLAY";
			case 3:
				return "LIGHT_BLUE_STAINED_CLAY";
			case 4:
				return "YELLOW_STAINED_CLAY";
			case 5:
				return "LIME_STAINED_CLAY";
			case 6:
				return "PINK_STAINED_CLAY";
			case 7:
				return "GRAY_STAINED_CLAY";
			case 8:
				return "LIGHT_GRAY_STAINED_CLAY";
			case 9:
				return "CYAN_STAINED_CLAY";
			case 10:
				return "PURPLE_STAINED_CLAY";
			case 11:
				return "BLUE_STAINED_CLAY";
			case 12:
				return "BROWN_STAINED_CLAY";
			case 13:
				return "GREEN_STAINED_CLAY";
			case 14:
				return "RED_STAINED_CLAY";
			case 15:
				return "BLACK_STAINED_CLAY";
			}
			return mat.toString();
		case STAINED_GLASS:
			switch ((int) damage) {
			case 0:
				return "WHITE_STAINED_GLASS";
			case 1:
				return "ORANGE_STAINED_GLASS";
			case 2:
				return "MAGENTA_STAINED_GLASS";
			case 3:
				return "LIGHT_BLUE_STAINED_GLASS";
			case 4:
				return "YELLOW_STAINED_GLASS";
			case 5:
				return "LIME_STAINED_GLASS";
			case 6:
				return "PINK_STAINED_GLASS";
			case 7:
				return "GRAY_STAINED_GLASS";
			case 8:
				return "LIGHT_GRAY_STAINED_GLASS";
			case 9:
				return "CYAN_STAINED_GLASS";
			case 10:
				return "PURPLE_STAINED_GLASS";
			case 11:
				return "BLUE_STAINED_GLASS";
			case 12:
				return "BROWN_STAINED_GLASS";
			case 13:
				return "GREEN_STAINED_GLASS";
			case 14:
				return "RED_STAINED_GLASS";
			case 15:
				return "BLACK_STAINED_GLASS";
			}
			return mat.toString();
		case STAINED_GLASS_PANE:
			switch ((int) damage) {
			case 0:
				return "WHITE_STAINED_GLASS_PANE";
			case 1:
				return "ORANGE_STAINED_GLASS_PANE";
			case 2:
				return "MAGENTA_STAINED_GLASS_PANE";
			case 3:
				return "LIGHT_BLUE_STAINED_GLASS_PANE";
			case 4:
				return "YELLOW_STAINED_GLASS_PANE";
			case 5:
				return "LIME_STAINED_GLASS_PANE";
			case 6:
				return "PINK_STAINED_GLASS_PANE";
			case 7:
				return "GRAY_STAINED_GLASS_PANE";
			case 8:
				return "LIGHT_GRAY_STAINED_GLASS_PANE";
			case 9:
				return "CYAN_STAINED_GLASS_PANE";
			case 10:
				return "PURPLE_STAINED_GLASS_PANE";
			case 11:
				return "BLUE_STAINED_GLASS_PANE";
			case 12:
				return "BROWN_STAINED_GLASS_PANE";
			case 13:
				return "GREEN_STAINED_GLASS_PANE";
			case 14:
				return "RED_STAINED_GLASS_PANE";
			case 15:
				return "BLACK_STAINED_GLASS_PANE";
			}
			return mat.toString();
		case STATIONARY_LAVA:
			break;
		case STATIONARY_WATER:
			break;
		case STICK:
			break;
		case STONE:
			break;
		case STONE_AXE:
			break;
		case STONE_BUTTON:
			break;
		case STONE_HOE:
			break;
		case STONE_PICKAXE:
			break;
		case STONE_PLATE:
			break;
		case STONE_SPADE:
			return "Stone Shovel";
		case STONE_SWORD:
			break;
		case STORAGE_MINECART:
			break;
		case STRING:
			break;
		case SUGAR:
			break;
		case SUGAR_CANE:
			break;
		case SUGAR_CANE_BLOCK:
			break;
		case TNT:
			break;
		case TORCH:
			break;
		case TRAPPED_CHEST:
			break;
		case TRAP_DOOR:
			break;
		case TRIPWIRE:
			break;
		case TRIPWIRE_HOOK:
			break;
		case VINE:
			break;
		case WALL_SIGN:
			break;
		case WATCH:
			break;
		case WATER:
			break;
		case WATER_BUCKET:
			break;
		case WATER_LILY:
			break;
		case WHEAT:
			break;
		case WOODEN_DOOR:
			break;
		case WOOD_AXE:
			return "Wooden Axe";
		case WOOD_BUTTON:
			return "Wooden Button";
		case WOOD_DOOR:
			return "Wooden Door";
		case WOOD_DOUBLE_STEP:
			return "Wooden Double Step";
		case WOOD_HOE:
			return "Wooden Hoe";
		case WOOD_PICKAXE:
			return "Wooden Pickaxe";
		case WOOD_PLATE:
			return "Pressure Plate";
		case WOOD_SPADE:
			return "Wooden Shovel";
		case WOOD_STAIRS:
			return "Wooden Stairs";
		case WOOD_STEP:
			return "Wooden Slab";
		case WOOD_SWORD:
			return "Wooden Sword";
		case WRITTEN_BOOK:
			break;
		case YELLOW_FLOWER:
			return "Dandelion";
		default:
			break;
		}
		// This covers the rest of the items that have a "reasonable" name
		if (damage == 0 || isTool(mat))
			return mat.toString();
		// This returns something that has a durability qualifier, but we don't
		// know what it is.
		return mat.toString() + ":" + damage;
	}

	/**
	 * @param mat
	 *            The material to check
	 * @return Returns true if the item is a tool (Has durability) or false if
	 *         it doesn't.
	 */
	public static boolean isTool(Material mat) {
		return tools.contains(mat);
	}

	/**
	 * Compares two items to each other. Returns true if they match.
	 * 
	 * @param stack1
	 *            The first item stack
	 * @param stack2
	 *            The second item stack
	 * @return true if the itemstacks match. (Material, durability, enchants)
	 */
	/*
	 * public static boolean matches(ItemStack stack1, ItemStack stack2) { if
	 * (stack1 == stack2) return true; // Referring to the same thing, or both
	 * are null. if (stack1 == null || stack2 == null) return false; // One of
	 * them is null (Can't be both, see above)
	 * 
	 * if (stack1.getType() != stack2.getType()) return false; // Not the same
	 * material if (stack1.getDurability() != stack2.getDurability()) return
	 * false; // Not the same durability if
	 * (!stack1.getEnchantments().equals(stack2.getEnchantments())) return
	 * false; // They have the same enchants
	 * 
	 * try { Class.forName("org.bukkit.inventory.meta.EnchantmentStorageMeta");
	 * boolean book1 = stack1.getItemMeta() instanceof EnchantmentStorageMeta;
	 * boolean book2 = stack2.getItemMeta() instanceof EnchantmentStorageMeta;
	 * if (book1 != book2) return false;// One has enchantment meta, the other
	 * does not. if (book1 == true) { // They are the same here (both true or
	 * both // false). So if one is true, the other is // true. Map<Enchantment,
	 * Integer> ench1 = ((EnchantmentStorageMeta) stack1
	 * .getItemMeta()).getStoredEnchants(); Map<Enchantment, Integer> ench2 =
	 * ((EnchantmentStorageMeta) stack2 .getItemMeta()).getStoredEnchants(); if
	 * (!ench1.equals(ench2)) return false; // Enchants aren't the same. } }
	 * catch (ClassNotFoundException e) { // Nothing. They dont have a build
	 * high enough to support this. }
	 * 
	 * return true; }
	 */
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
	public static void setCurrency(String c) {
		if (c != null) {
			currency = c;
		}
	}
	
	/**
	 * Counts the number of items in the given inventory where
	 * Util.matches(inventory item, item) is true.
	 * 
	 * @param inv
	 *            The inventory to search
	 * @param item
	 *            The ItemStack to search for
	 * @return The number of items that match in this inventory.
	 */
	/*
	 * public static int countItems(Inventory inv, ItemStack item) { int items =
	 * 0; for (ItemStack iStack : inv.getContents()) { if (iStack == null)
	 * continue; if (Util.matches(item, iStack)) { items += iStack.getAmount();
	 * } } return items; }
	 */
	/**
	 * Returns the number of items that can be given to the inventory safely.
	 * 
	 * @param inv
	 *            The inventory to count
	 * @param item
	 *            The item prototype. Material, durabiltiy and enchants must
	 *            match for 'stackability' to occur.
	 * @return The number of items that can be given to the inventory safely.
	 */
	/*
	 * public static int countSpace(Inventory inv, ItemStack item) { int space =
	 * 0; for (ItemStack iStack : inv.getContents()) { if (iStack == null ||
	 * iStack.getType() == Material.AIR) { space += item.getMaxStackSize(); }
	 * else if (matches(item, iStack)) { space += item.getMaxStackSize() -
	 * iStack.getAmount(); } } return space; }
	 */
	/**
	 * Returns true if the given location is loaded or not.
	 * 
	 * @param loc
	 *            The location
	 * @return true if the given location is loaded or not.
	 */
	/*
	 * public static boolean isLoaded(Location loc) { //
	 * System.out.println("Checking isLoaded(Location loc)"); if (loc.getWorld()
	 * == null) { // System.out.println("Is not loaded. (No world)"); return
	 * false; } // Calculate the chunks coordinates. These are 1,2,3 for each
	 * chunk, NOT // location rounded to the nearest 16. int x = (int)
	 * Math.floor((loc.getBlockX()) / 16.0); int z = (int)
	 * Math.floor((loc.getBlockZ()) / 16.0);
	 * 
	 * if (loc.getWorld().isChunkLoaded(x, z)) { //
	 * System.out.println("Chunk is loaded " + x + ", " + z); return true; }
	 * else { // System.out.println("Chunk is NOT loaded " + x + ", " + z);
	 * return false; } }
	 */
	/**
	 * Returns the price of a block of the material or -100000 if unknown
	 * 
	 * @return The price of the block
	 */
	/*
	 * public static double getPrice(Material type) { if
	 * (blockPrices.containsKey(type)) { return (Double) blockPrices.get(type);
	 * } return -10000; }
	 */
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
			 * Multiple enchantments make the object more valuable 
			 * 1 enchantment = 1x 
			 * 2 enchantments = 3.1x 
			 * 3 enchantments = 5.88x 
			 * 4 enchantments = 10.53x 
			 * This is derived from the probability of obtaining
			 * multiple enchantments as calculated 1000's of times (see website calculators)
			 */
			final Map <Integer, Double> multiEnchantValue = new HashMap <Integer, Double>() {
				private static final long serialVersionUID = 1L;
				{
					put(1, 1.0);
					put(2, 3.1);
					put(3, 5.88);
					put(4, 10.53);
				}
			};
			double enchantValue = 0;
			int multiplier = 0;
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
					plugin.getLogger().info("Enchant +1 rank " + rank);
				} else if (item.getKey().equals(Enchantment.PROTECTION_FALL)
						| item.getKey().equals(Enchantment.PROTECTION_FIRE)
						| item.getKey().equals(
								Enchantment.PROTECTION_PROJECTILE)) {
					// Value 2
					enchantValue += (2.0 * rank);
					plugin.getLogger().info("Enchant +2 rank " + rank);
				} else if (item.getKey().equals(Enchantment.DAMAGE_ARTHROPODS)
						| item.getKey().equals(Enchantment.KNOCKBACK)
						| item.getKey().equals(Enchantment.DAMAGE_UNDEAD)) {
					// Value 2.5
					enchantValue += 2.5;
					plugin.getLogger().info("Enchant +2.5 rank "+ rank);
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
					plugin.getLogger().info("Enchant +5 rank "+ rank);
				} else if (item.getKey().equals(Enchantment.THORNS)
						| item.getKey().equals(Enchantment.ARROW_INFINITE)
						| item.getKey().equals(Enchantment.SILK_TOUCH)) {
					// Value 10
					enchantValue += (10.0 * rank);
					plugin.getLogger().info("Enchant +10 rank "+ rank);
				}
				multiplier++;
			}
			// Multiply the value by how many enchantments there are
			//plugin.getLogger().info("There are "+multiplier+" enchantments so multiplier is " + multiEnchantValue.get(multiplier));
			enchantValue = enchantValue * multiEnchantValue.get(multiplier);
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
	public static double getPrice(ItemStack item) {
		// Multiplier for damage
		double damageModifier = 1;
		Material type = item.getType();
		// Find out if this item is damaged or not, i.e. durability
		short durability = item.getDurability();
		// Find out the max durability of the item
		short maxDurability = item.getType().getMaxDurability();
		// If the max and current durability are zero then this is just a block
		// or normal item
		// If the current durability is greater than the max, then it's a
		// variant on the current item, e.g. potion, etc.
		if (durability > maxDurability) {
			// This is a special item
			switch (type) {
			case WOOL:
				try {
					return (dyePrices.get((int) (15 - durability)) + blockPrices
							.get(Material.WOOL));
				} catch (Exception e) {
					return -10000.0;
				}
			case RAW_FISH:
				try {
					return (fishPrices.get((int) durability));
				} catch (Exception e) {
					return -10000.0;
				}
			case INK_SACK:
				try {
					return (dyePrices.get((int) durability));
				} catch (Exception e) {
					return -10000.0;
				}
			case SMOOTH_BRICK:
				switch ((int) durability) {
				case 0:
					// "STONE_BRICKS";
				case 1:
					// "MOSSY_STONE_BRICKS";
				case 2:
					// "CRACKED_STONE_BRICKS";
				case 3:
					// "CHISELED_STONE_BRICKS";
				}
				break;
			case POTION:
				try {
					return potionPrices.get((int) durability);
				} catch (Exception e) {
					return -10000.0;
				}
			case SAPLING:
				switch ((int) durability) {
				case 0:
					// "OAK_SAPLING";
				case 1:
					// "PINE_SAPLING";
				case 2:
					// "BIRCH_SAPLING";
				case 3:
					// "JUNGLE_TREE_SAPLING";
				}
				break;
			case WOOD:
				switch ((int) durability) {
				case 0:
					// "OAK_PLANKS";
				case 1:
					// "PINE_PLANKS";
				case 2:
					// "BIRCH_PLANKS";
				case 3:
					// "JUNGLE_PLANKS";
				}
			case LOG:
				switch (durability) {
				case 0:
					// "OAK_LOG";
				case 1:
					// "PINE_LOG";
				case 2:
					// "BIRCH_LOG";
				case 3:
					// "JUNGLE_LOG";
				}
				break;
			case LEAVES:
				switch (durability) {
				case 0:
					// "OAK_LEAVES";
				case 1:
					// "PINE_LEAVES";
				case 2:
					// "BIRCH_LEAVES";
				case 3:
					// "JUNGLE_LEAVES";
				}
				break;
			case COAL:
				switch (durability) {
				case 0:
					// return "COAL";
				case 1:
					// "CHARCOAL - is wood + fuel
				}
				break;
			case SANDSTONE:
				switch ((int) durability) {
				case 0:
					// "SANDSTONE";
				case 1:
					// "CHISELED_SANDSTONE";
				case 2:
					// "SMOOTH_SANDSTONE";
				}
				break;
			case LONG_GRASS:
				switch ((int) durability) {
				case 0:
					// "DEAD_SHRUB";
				case 1:
					// "TALL_GRASS";
				case 2:
					// "FERN";
				}
				break;
			case STEP:
				switch ((int) durability) {
				case 0:
					// "STONE_SLAB";
				case 1:
					// "SANDSTONE_SLAB";
				case 2:
					// "WOODEN_SLAB";
				case 3:
					// "COBBLESTONE_SLAB";
				case 4:
					// "BRICK_SLAB";
				case 5:
					// "STONE_BRICK_SLAB";
					/*
					 * 
					 * 43:6 Nether Brick Slab (Double) 43:7 Quartz Slab (Double)
					 * 43:8 Smooth Stone Slab (Double) 43:9 Smooth Sandstone
					 * Slab (Double)
					 */
				}
				break;
			case MONSTER_EGG:
				// All monster eggs are priceless. They cannot be crafted.
				return -10000;
			case SKULL_ITEM:
				// All of these are priceless. The Wither Skull is a rare drop
				// and the rest are in creative only.
				// "SKELETON_SKULL";
				// "WITHER_SKULL";
				// "ZOMBIE_HEAD";
				// "PLAYER_HEAD";
				// "CREEPER_HEAD";
				return -10000;
			case GOLDEN_APPLE:
				switch ((int) durability) {
				case 0:
					// "GOLDEN_APPLE";
				case 1:
					// "ENCHANTED_GOLDEN_APPLE"
					return (blockPrices.get(Material.APPLE) + blockPrices
							.get(Material.GOLD_BLOCK) * 8.0);
				}
				break;
			case ANVIL:
				// Anvils have a 12% chance of being damaged every time they are
				// used.
				switch ((int) durability) {
				case 0:
					// "ANVIL";
					return blockPrices.get(Material.ANVIL);
				case 1:
					// "SLIGHTLY_DAMAGED_ANVIL";
					return blockPrices.get(Material.ANVIL) * 0.12;
				case 2:
					// "VERY_DAMAGED:ANVIL";
					return blockPrices.get(Material.ANVIL) * 0.0144;
				}
				break;
			case RED_ROSE:
				switch ((int) durability) {
				case 0:
					// "POPPY";
				default:
					/*
					 * 38:1 Blue Orchid 38:2 Allium 38:4 Red Tulip 38:5 Orange
					 * Tulip 38:6 White Tulip 38:7 Pink Tulip 38:8 Oxeye Daisy
					 */
				}
				break;
			case STAINED_GLASS:
				// plugin.getLogger().info("Stained glass");
				try {
					return (dyePrices.get((int) (15 - durability)) + blockPrices
							.get(Material.GLASS));
				} catch (Exception e) {
					return -10000.0;
				}
				/*
				 * 
				 * 95:1 Stained Glass (Orange) 95:2 Stained Glass (Magenta) 95:3
				 * Stained Glass (Light Blue) 95:4 Stained Glass (Yellow) 95:5
				 * Stained Glass (Lime) 95:6 Stained Glass (Pink) 95:7 Stained
				 * Glass (Gray) 95:8 Stained Glass (Light Grey) 95:9 Stained
				 * Glass (Cyan) 95:10 Stained Glass (Purple) 95:11 Stained Glass
				 * (Blue) 95:12 Stained Glass (Brown) 95:13 Stained Glass
				 * (Green) 95:14 Stained Glass (Red) 95:15 Stained Glass (Black)
				 */
			case CARPET:
				try {
					return (dyePrices.get((int) (15 - durability)) + 2 / 3 * blockPrices
							.get(Material.WOOL));
				} catch (Exception e) {
					return -10000.0;
				}
				/*
				 * 
				 * 171:1 Carpet (Orange) 171:2 Carpet (Magenta) 171:3 Carpet
				 * (Light Blue) 171:4 Carpet (Yellow) 171:5 Carpet (Lime) 171:6
				 * Carpet (Pink) 171:7 Carpet (Grey) 171:8 Carpet (Light Gray)
				 * 171:9 Carpet (Cyan) 171:10 Carpet (Purple) 171:11 Carpet
				 * (Blue) 171:12 Carpet (Brown) 171:13 Carpet (Green) 171:14
				 * Carpet (Red) 171:15 Carpet (Black)
				 */
			case ACACIA_STAIRS:
				break;
			case ACTIVATOR_RAIL:
				break;
			case AIR:
				break;
			case APPLE:
				break;
			case ARROW:
				break;
			case BAKED_POTATO:
				break;
			case BEACON:
				break;
			case BED:
				break;
			case BEDROCK:
				break;
			case BED_BLOCK:
				break;
			case BIRCH_WOOD_STAIRS:
				break;
			case BLAZE_POWDER:
				break;
			case BLAZE_ROD:
				break;
			case BOAT:
				break;
			case BONE:
				break;
			case BOOK:
				break;
			case BOOKSHELF:
				break;
			case BOOK_AND_QUILL:
				break;
			case BOW:
				break;
			case BOWL:
				break;
			case BREAD:
				break;
			case BREWING_STAND:
				break;
			case BREWING_STAND_ITEM:
				break;
			case BRICK:
				break;
			case BRICK_STAIRS:
				break;
			case BROWN_MUSHROOM:
				break;
			case BUCKET:
				break;
			case BURNING_FURNACE:
				break;
			case CACTUS:
				break;
			case CAKE:
				break;
			case CAKE_BLOCK:
				break;
			case CARROT:
				break;
			case CARROT_ITEM:
				break;
			case CARROT_STICK:
				break;
			case CAULDRON:
				break;
			case CAULDRON_ITEM:
				break;
			case CHAINMAIL_BOOTS:
				break;
			case CHAINMAIL_CHESTPLATE:
				break;
			case CHAINMAIL_HELMET:
				break;
			case CHAINMAIL_LEGGINGS:
				break;
			case CHEST:
				break;
			case CLAY:
				break;
			case CLAY_BALL:
				break;
			case CLAY_BRICK:
				break;
			case COAL_BLOCK:
				break;
			case COAL_ORE:
				break;
			case COBBLESTONE:
				break;
			case COBBLESTONE_STAIRS:
				break;
			case COBBLE_WALL:
				break;
			case COCOA:
				break;
			case COMMAND:
				break;
			case COMMAND_MINECART:
				break;
			case COMPASS:
				break;
			case COOKED_BEEF:
				break;
			case COOKED_CHICKEN:
				break;
			case COOKED_FISH:
				break;
			case COOKIE:
				break;
			case CROPS:
				break;
			case DARK_OAK_STAIRS:
				break;
			case DAYLIGHT_DETECTOR:
				break;
			case DEAD_BUSH:
				break;
			case DETECTOR_RAIL:
				break;
			case DIAMOND:
				break;
			case DIAMOND_AXE:
				break;
			case DIAMOND_BARDING:
				break;
			case DIAMOND_BLOCK:
				break;
			case DIAMOND_BOOTS:
				break;
			case DIAMOND_CHESTPLATE:
				break;
			case DIAMOND_HELMET:
				break;
			case DIAMOND_HOE:
				break;
			case DIAMOND_LEGGINGS:
				break;
			case DIAMOND_ORE:
				break;
			case DIAMOND_PICKAXE:
				break;
			case DIAMOND_SPADE:
				break;
			case DIAMOND_SWORD:
				break;
			case DIODE:
				break;
			case DIODE_BLOCK_OFF:
				break;
			case DIODE_BLOCK_ON:
				break;
			case DIRT:
				break;
			case DISPENSER:
				break;
			case DOUBLE_PLANT:
				break;
			// These cannot be crafted, so priceless
			case DOUBLE_STEP:
				switch ((int) durability) {
				case 0:
					// "STONE_SLAB (DOUBLE)";
				case 1:
					// "SANDSTONE_SLAB (DOUBLE)";
				case 2:
					// "WOODEN_SLAB (DOUBLE)";
				case 3:
					// "COBBLESTONE_SLAB (DOUBLE)";
				case 4:
					// "BRICK_SLAB (DOUBLE)";
				case 5:
					// "STONE_BRICK_SLAB (DOUBLE)";
				case 6:
					// "Nether Brick Slab (DOUBLE)";
				case 7:
					// "Quartz Slab (DOUBLE)";
				case 8:
					// "Smooth Stone Slab (Double)";
				case 9:
					// "Smooth Sandstone Slab (Double)";
				}

				break;
			case DRAGON_EGG:
				break;
			case DROPPER:
				break;
			case EGG:
				break;
			case EMERALD:
				break;
			case EMERALD_BLOCK:
				break;
			case EMERALD_ORE:
				break;
			case EMPTY_MAP:
				break;
			case ENCHANTED_BOOK:
				break;
			case ENCHANTMENT_TABLE:
				break;
			case ENDER_CHEST:
				break;
			case ENDER_PEARL:
				break;
			case ENDER_PORTAL:
				break;
			case ENDER_PORTAL_FRAME:
				break;
			case ENDER_STONE:
				break;
			case EXPLOSIVE_MINECART:
				break;
			case EXP_BOTTLE:
				break;
			case EYE_OF_ENDER:
				break;
			case FEATHER:
				break;
			case FENCE:
				break;
			case FENCE_GATE:
				break;
			case FERMENTED_SPIDER_EYE:
				break;
			case FIRE:
				break;
			case FIREBALL:
				break;
			case FIREWORK:
				break;
			case FIREWORK_CHARGE:
				break;
			case FISHING_ROD:
				break;
			case FLINT:
				break;
			case FLINT_AND_STEEL:
				break;
			case FLOWER_POT:
				break;
			case FLOWER_POT_ITEM:
				break;
			case FURNACE:
				break;
			case GHAST_TEAR:
				break;
			case GLASS:
				break;
			case GLASS_BOTTLE:
				break;
			case GLOWING_REDSTONE_ORE:
				break;
			case GLOWSTONE:
				break;
			case GLOWSTONE_DUST:
				break;
			case GOLDEN_CARROT:
				break;
			case GOLD_AXE:
				break;
			case GOLD_BARDING:
				break;
			case GOLD_BLOCK:
				break;
			case GOLD_BOOTS:
				break;
			case GOLD_CHESTPLATE:
				break;
			case GOLD_HELMET:
				break;
			case GOLD_HOE:
				break;
			case GOLD_INGOT:
				break;
			case GOLD_LEGGINGS:
				break;
			case GOLD_NUGGET:
				break;
			case GOLD_ORE:
				break;
			case GOLD_PICKAXE:
				break;
			case GOLD_PLATE:
				break;
			case GOLD_RECORD:
				break;
			case GOLD_SPADE:
				break;
			case GOLD_SWORD:
				break;
			case GRASS:
				break;
			case GRAVEL:
				break;
			case GREEN_RECORD:
				break;
			case GRILLED_PORK:
				break;
			case HARD_CLAY:
				break;
			case HAY_BLOCK:
				break;
			case HOPPER:
				break;
			case HOPPER_MINECART:
				break;
			case HUGE_MUSHROOM_1:
				break;
			case HUGE_MUSHROOM_2:
				break;
			case ICE:
				break;
			case IRON_AXE:
				break;
			case IRON_BARDING:
				break;
			case IRON_BLOCK:
				break;
			case IRON_BOOTS:
				break;
			case IRON_CHESTPLATE:
				break;
			case IRON_DOOR:
				break;
			case IRON_DOOR_BLOCK:
				break;
			case IRON_FENCE:
				break;
			case IRON_HELMET:
				break;
			case IRON_HOE:
				break;
			case IRON_INGOT:
				break;
			case IRON_LEGGINGS:
				break;
			case IRON_ORE:
				break;
			case IRON_PICKAXE:
				break;
			case IRON_PLATE:
				break;
			case IRON_SPADE:
				break;
			case IRON_SWORD:
				break;
			case ITEM_FRAME:
				break;
			case JACK_O_LANTERN:
				break;
			case JUKEBOX:
				break;
			case JUNGLE_WOOD_STAIRS:
				break;
			case LADDER:
				break;
			case LAPIS_BLOCK:
				break;
			case LAPIS_ORE:
				break;
			case LAVA:
				break;
			case LAVA_BUCKET:
				break;
			case LEASH:
				break;
			case LEATHER:
				break;
			case LEATHER_BOOTS:
				break;
			case LEATHER_CHESTPLATE:
				break;
			case LEATHER_HELMET:
				break;
			case LEATHER_LEGGINGS:
				break;
			case LEAVES_2:
				break;
			case LEVER:
				break;
			case LOG_2:
				break;
			case MAGMA_CREAM:
				break;
			case MAP:
				break;
			case MELON:
				break;
			case MELON_BLOCK:
				break;
			case MELON_SEEDS:
				break;
			case MELON_STEM:
				break;
			case MILK_BUCKET:
				break;
			case MINECART:
				break;
			case MOB_SPAWNER:
				break;
			case MONSTER_EGGS:
				break;
			case MOSSY_COBBLESTONE:
				break;
			case MUSHROOM_SOUP:
				break;
			case MYCEL:
				break;
			case NAME_TAG:
				break;
			case NETHERRACK:
				break;
			case NETHER_BRICK:
				break;
			case NETHER_BRICK_ITEM:
				break;
			case NETHER_BRICK_STAIRS:
				break;
			case NETHER_FENCE:
				break;
			case NETHER_STALK:
				break;
			case NETHER_STAR:
				break;
			case NETHER_WARTS:
				break;
			case NOTE_BLOCK:
				break;
			case OBSIDIAN:
				break;
			case PACKED_ICE:
				break;
			case PAINTING:
				break;
			case PAPER:
				break;
			case PISTON_BASE:
				break;
			case PISTON_EXTENSION:
				break;
			case PISTON_MOVING_PIECE:
				break;
			case PISTON_STICKY_BASE:
				break;
			case POISONOUS_POTATO:
				break;
			case PORK:
				break;
			case PORTAL:
				break;
			case POTATO:
				break;
			case POTATO_ITEM:
				break;
			case POWERED_MINECART:
				break;
			case POWERED_RAIL:
				break;
			case PUMPKIN:
				break;
			case PUMPKIN_PIE:
				break;
			case PUMPKIN_SEEDS:
				break;
			case PUMPKIN_STEM:
				break;
			case QUARTZ:
				break;
			case QUARTZ_BLOCK:
				break;
			case QUARTZ_ORE:
				break;
			case QUARTZ_STAIRS:
				break;
			case RAILS:
				break;
			case RAW_BEEF:
				break;
			case RAW_CHICKEN:
				break;
			case RECORD_10:
				break;
			case RECORD_11:
				break;
			case RECORD_12:
				break;
			case RECORD_3:
				break;
			case RECORD_4:
				break;
			case RECORD_5:
				break;
			case RECORD_6:
				break;
			case RECORD_7:
				break;
			case RECORD_8:
				break;
			case RECORD_9:
				break;
			case REDSTONE:
				break;
			case REDSTONE_BLOCK:
				break;
			case REDSTONE_COMPARATOR:
				break;
			case REDSTONE_COMPARATOR_OFF:
				break;
			case REDSTONE_COMPARATOR_ON:
				break;
			case REDSTONE_LAMP_OFF:
				break;
			case REDSTONE_LAMP_ON:
				break;
			case REDSTONE_ORE:
				break;
			case REDSTONE_TORCH_OFF:
				break;
			case REDSTONE_TORCH_ON:
				break;
			case REDSTONE_WIRE:
				break;
			case RED_MUSHROOM:
				break;
			case ROTTEN_FLESH:
				break;
			case SADDLE:
				break;
			case SAND:
				break;
			case SANDSTONE_STAIRS:
				break;
			case SEEDS:
				break;
			case SHEARS:
				break;
			case SIGN:
				break;
			case SIGN_POST:
				break;
			case SKULL:
				break;
			case SLIME_BALL:
				break;
			case SMOOTH_STAIRS:
				break;
			case SNOW:
				break;
			case SNOW_BALL:
				break;
			case SNOW_BLOCK:
				break;
			case SOIL:
				break;
			case SOUL_SAND:
				break;
			case SPECKLED_MELON:
				break;
			case SPIDER_EYE:
				break;
			case SPONGE:
				break;
			case SPRUCE_WOOD_STAIRS:
				break;
			case STAINED_CLAY:
				break;
			case STAINED_GLASS_PANE:
				break;
			case STATIONARY_LAVA:
				break;
			case STATIONARY_WATER:
				break;
			case STICK:
				break;
			case STONE:
				break;
			case STONE_AXE:
				break;
			case STONE_BUTTON:
				break;
			case STONE_HOE:
				break;
			case STONE_PICKAXE:
				break;
			case STONE_PLATE:
				break;
			case STONE_SPADE:
				break;
			case STONE_SWORD:
				break;
			case STORAGE_MINECART:
				break;
			case STRING:
				break;
			case SUGAR:
				break;
			case SUGAR_CANE:
				break;
			case SUGAR_CANE_BLOCK:
				break;
			case SULPHUR:
				break;
			case THIN_GLASS:
				break;
			case TNT:
				break;
			case TORCH:
				break;
			case TRAPPED_CHEST:
				break;
			case TRAP_DOOR:
				break;
			case TRIPWIRE:
				break;
			case TRIPWIRE_HOOK:
				break;
			case VINE:
				break;
			case WALL_SIGN:
				break;
			case WATCH:
				break;
			case WATER:
				break;
			case WATER_BUCKET:
				break;
			case WATER_LILY:
				break;
			case WEB:
				break;
			case WHEAT:
				break;
			case WOODEN_DOOR:
				break;
			case WOOD_AXE:
				break;
			case WOOD_BUTTON:
				break;
			case WOOD_DOOR:
				break;
			case WOOD_DOUBLE_STEP:
				break;
			case WOOD_HOE:
				break;
			case WOOD_PICKAXE:
				break;
			case WOOD_PLATE:
				break;
			case WOOD_SPADE:
				break;
			case WOOD_STAIRS:
				break;
			case WOOD_STEP:
				break;
			case WOOD_SWORD:
				break;
			case WORKBENCH:
				break;
			case WRITTEN_BOOK:
				break;
			case YELLOW_FLOWER:
				break;
			default:
				break;
			}
		} else {
			// Adjust price for damage to the item
			if (maxDurability > 0) {
				damageModifier = 1 - (double) durability
						/ (double) maxDurability;
			}
		}
		// Final total is...
		if (blockPrices.containsKey(type)) {
			return (Double) blockPrices.get(type) * damageModifier;
		} else {
			return -10010;
		}

	}
}