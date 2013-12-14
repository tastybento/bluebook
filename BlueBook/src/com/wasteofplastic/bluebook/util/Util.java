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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
//import java.util.Set;

//import org.bukkit.ChatColor;
//import org.bukkit.ChatColor;
//import org.bukkit.Location;
import org.bukkit.Material;
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
	public static HashMap<Material, Double> blockPrices = new HashMap<Material, Double>();
	private static HashMap<Integer, Double> dyePrices = new HashMap<Integer, Double>();

	private static BlueBook plugin;

	static {
		plugin = BlueBook.instance;
		// Initialize all block prices so that if any are missing nothing bad
		// happens
		// The value is set to a large negative number so that even crafted
		// items will probably become priceless
		for (int i = 0; i < 2268; i++) {
			Material mat = Material.getMaterial(i);
			if (mat != null) {
				blockPrices.put(mat, -10000.0);
			}
		}
		plugin.getLogger().info("******************Loading prices");
		// Load in prices
		Map<String, Object> map = plugin.getConfig()
				.getConfigurationSection("block-prices").getValues(false);
		for (Entry<String, Object> entry : map.entrySet()) { // just to loop
																// through the
																// entries
			// plugin.getLogger().info("******************Loading price");
			// plugin.getLogger().info("Block loaded " +
			// entry.getKey().toString() + " $" + (Double)entry.getValue());
			Material key = Material.getMaterial(entry.getKey());
			Double price = Double.valueOf(entry.getValue().toString());
			if (key != null) {
				blockPrices.put(key, price); // cast the value so it becomes a
												// "MyObjectClass".
				// plugin.getLogger().info("Block loaded " + key + " $" +
				// price);
			} else {
				plugin.getLogger().info("Invalid block " + entry.getKey());
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
		dyePrices.put(14, blockPrices.get(Material.RED_ROSE) + blockPrices.get(Material.YELLOW_FLOWER));
		// MAGENTA
		dyePrices.put(13, blockPrices.get(Material.RED_ROSE) * 2.0 + blockPrices.get(Material.BONE) + blockPrices.get(Material.LAPIS_ORE));
		// LIGHT BLUE
		dyePrices.put(12, blockPrices.get(Material.BONE) + blockPrices.get(Material.LAPIS_ORE));
		// YELLOW
		dyePrices.put(11, blockPrices.get(Material.YELLOW_FLOWER));
		// LIME
		dyePrices.put(10, blockPrices.get(Material.CACTUS) + fuel + blockPrices.get(Material.BONE));
		// PINK
		dyePrices.put(9, blockPrices.get(Material.RED_ROSE) + blockPrices.get(Material.BONE));
		// GRAY
		dyePrices.put(8, blockPrices.get(Material.INK_SACK) + blockPrices.get(Material.BONE));
		// LIGHT GRAY
		dyePrices.put(7, blockPrices.get(Material.INK_SACK) + blockPrices.get(Material.BONE) * 2.0);
		// CYAN
		dyePrices.put(6, blockPrices.get(Material.CACTUS) + fuel + blockPrices.get(Material.LAPIS_ORE));
		// PURPLE
		dyePrices.put(5, blockPrices.get(Material.RED_ROSE) + blockPrices.get(Material.LAPIS_ORE));
		// BLUE
		dyePrices.put(4, blockPrices.get(Material.LAPIS_ORE));
		// BROWN
		dyePrices.put(3, blockPrices.get(Material.COCOA)/3.0);
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
		blockPrices.put(Material.SANDSTONE, blockPrices.get(Material.SAND) * 4.0);
		blockPrices.put(Material.NOTE_BLOCK, 8.0 * wood + redStone);
		blockPrices.put(Material.POWERED_RAIL,
				(6.0 * goldBar + redStone + stick) / 6.0);
		blockPrices.put(Material.STONE_PLATE, 2.0 * stone);
		blockPrices
				.put(Material.DETECTOR_RAIL,
						(6.0 * ironBar + redStone + blockPrices
								.get(Material.STONE_PLATE)) / 6.0);
		blockPrices.put(Material.PISTON_BASE, 3.0 * wood + ironBar + 4.0 * cobble
				+ redStone);
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
		blockPrices.put(Material.BOOKSHELF, blockPrices.get(Material.BOOK) * 3.0
				+ blockPrices.get(Material.WOOD) * 6.0);
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
		blockPrices.put(
				Material.ENCHANTMENT_TABLE,
				4.0 * blockPrices.get(Material.OBSIDIAN) + diamond
						+ blockPrices.get(Material.BOOK));
		blockPrices.put(Material.REDSTONE_LAMP_ON,
				4.0 * redStone + blockPrices.get(Material.GLOWSTONE));
		blockPrices.put(Material.REDSTONE_LAMP_OFF,
				4.0 * redStone + blockPrices.get(Material.GLOWSTONE));
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
		blockPrices.put(Material.CLAY, 4.0 * blockPrices.get(Material.CLAY_BALL));
		blockPrices.put(Material.HARD_CLAY, blockPrices.get(Material.CLAY)
				+ fuel);
		// TODO: Need dyes
		blockPrices.put(Material.STAINED_CLAY,
				(8.0 * blockPrices.get(Material.HARD_CLAY) + blockPrices
						.get(Material.INK_SACK)) / 8.0);
		blockPrices
				.put(Material.HAY_BLOCK, 9.0 * blockPrices.get(Material.WHEAT));
		// TODO: Add colors
		blockPrices
				.put(Material.CARPET, 2.0 * blockPrices.get(Material.WOOL) / 3.0);
		blockPrices
				.put(Material.COAL_BLOCK, 9.0 * blockPrices.get(Material.COAL));
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
		blockPrices.put(
				Material.DIODE,
				3.0 * stone + redStone + 2.0
						* blockPrices.get(Material.REDSTONE_TORCH_ON));
		// TODO: Fix to charge for COCOA
		 blockPrices.put(Material.COOKIE, 2.0 * blockPrices.get(Material.WHEAT)
		 + blockPrices.get(Material.COCOA)/3.0);
		//blockPrices.put(Material.COOKIE, 2 * blockPrices.get(Material.WHEAT)
		//		+ blockPrices.get(Material.INK_SACK));
		blockPrices.put(Material.SHEARS, 2.0 * ironBar);
		blockPrices.put(Material.COOKED_BEEF,
				blockPrices.get(Material.RAW_BEEF) + fuel);
		blockPrices.put(Material.COOKED_CHICKEN,
				blockPrices.get(Material.RAW_CHICKEN) + fuel);
		blockPrices.put(Material.GOLD_NUGGET, goldBar / 9.0);
		// TODO: Add all potions
		//blockPrices.put(Material.POTION, -1.0);
		blockPrices.put(Material.GLASS_BOTTLE, blockPrices.get(Material.GLASS));
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
		blockPrices.put(Material.SPECKLED_MELON,
				8.0 / 9.0 * goldBar + blockPrices.get(Material.MELON));
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
		blockPrices.put(Material.BAKED_POTATO, blockPrices.get(Material.POTATO_ITEM) + fuel);
		//blockPrices.put(Material.BAKED_POTATO, 20.0);
		blockPrices.put(Material.EMPTY_MAP, 8.0 * blockPrices.get(Material.PAPER)
				+ blockPrices.get(Material.COMPASS));
		blockPrices.put(Material.GOLDEN_CARROT,
				(8.0 / 9.0 * goldBar) + blockPrices.get(Material.CARROT_ITEM));
		blockPrices.put(Material.CARROT_STICK, blockPrices.get(Material.CARROT_ITEM)
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

	}

	/*
	 * public static void parseColours(YamlConfiguration config) { Set<String>
	 * keys = config.getKeys(true);
	 * 
	 * for (String key : keys) { String filtered = config.getString(key); if
	 * (filtered.startsWith("MemorySection")) { continue; } filtered =
	 * ChatColor.translateAlternateColorCodes('&', filtered); config.set(key,
	 * filtered); } }
	 */
	/**
	 * Converts a string into an item from the database.
	 * 
	 * @param itemString
	 *            The database string. Is the result of makeString(ItemStack
	 *            item).
	 * @return A new itemstack, with the properties given in the string
	 */
	/*
	 * public static ItemStack makeItem(String itemString) { String[] itemInfo =
	 * itemString.split(":");
	 * 
	 * ItemStack item = new ItemStack(Material.getMaterial(itemInfo[0]));
	 * MaterialData data = new MaterialData(Integer.parseInt(itemInfo[1]));
	 * item.setData(data); item.setDurability(Short.parseShort(itemInfo[2]));
	 * item.setAmount(Integer.parseInt(itemInfo[3]));
	 * 
	 * for (int i = 4; i < itemInfo.length; i = i + 2) { int level =
	 * Integer.parseInt(itemInfo[i + 1]);
	 * 
	 * Enchantment ench = Enchantment.getByName(itemInfo[i]); if (ench == null)
	 * continue; // Invalid if (ench.canEnchantItem(item)) { if (level <= 0)
	 * continue; level = Math.min(ench.getMaxLevel(), level);
	 * 
	 * item.addEnchantment(ench, level); }
	 * 
	 * } return item; }
	 */
	/*
	 * public static String serialize(ItemStack iStack) { YamlConfiguration cfg
	 * = new YamlConfiguration(); cfg.set("item", iStack); return
	 * cfg.saveToString(); }
	 * 
	 * public static ItemStack deserialize(String config) throws
	 * InvalidConfigurationException { YamlConfiguration cfg = new
	 * YamlConfiguration(); cfg.loadFromString(config); ItemStack stack =
	 * cfg.getItemStack("item"); return stack; }
	 */
	/**
	 * Fetches an ItemStack's name - For example, converting INK_SAC:11 to
	 * Dandellion Yellow, or WOOL:14 to Red Wool
	 * 
	 * @param i
	 *            The itemstack to fetch the name of
	 * @return The human readable item name.
	 */
	public static String getName(ItemStack i) {
		String vanillaName = getDataName(i.getType(), i.getDurability());
		return prettifyText(vanillaName);
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

	public static String toRomain(Integer value) {
		return toRoman(value.intValue());
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
	 * Converts a given material and data value into a format similar to
	 * Material.<?>.toString(). Upper case, with underscores. Includes material
	 * name in result.
	 * 
	 * @param mat
	 *            The base material.
	 * @param damage
	 *            The durability/damage of the item.
	 * @return A string with the name of the item.
	 */
	private static String getDataName(Material mat, short damage) {
		int id = mat.getId();
		switch (id) {
		case 35:
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
		case 351:
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
		case 98:
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
		case 373:
			// Special case,.. Why?
			if (damage == 0)
				return "WATER_BOTTLE";

			Potion pot;
			try {
				pot = Potion.fromDamage(damage);
			} catch (Exception e) {
				return "CUSTOM_POTION";
			}

			String prefix = "";
			String suffix = "";
			if (pot.getLevel() > 0)
				suffix += "_" + pot.getLevel();
			if (pot.hasExtendedDuration())
				prefix += "EXTENDED_";
			if (pot.isSplash())
				prefix += "SPLASH_";

			if (pot.getEffects().isEmpty()) {
				switch ((int) pot.getNameId()) {
				case 0:
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
				}
			} else {
				String effects = "";
				for (PotionEffect effect : pot.getEffects()) {
					effects += effect.toString().split(":")[0];
				}
				return prefix + effects + suffix;
			}
			return mat.toString();
		case 6:
			switch ((int) damage) {
			case 0:
				return "OAK_SAPLING";
			case 1:
				return "PINE_SAPLING";
			case 2:
				return "BIRCH_SAPLING";
			case 3:
				return "JUNGLE_TREE_SAPLING";
			}
			return mat.toString();

		case 5:
			switch ((int) damage) {
			case 0:
				return "OAK_PLANKS";
			case 1:
				return "PINE_PLANKS";
			case 2:
				return "BIRCH_PLANKS";
			case 3:
				return "JUNGLE_PLANKS";
			}
			return mat.toString();
		case 17:
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
		case 18:
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
			}
		case 263:
			switch (damage) {
			case 0:
				return "COAL";
			case 1:
				return "CHARCOAL";
			}
			return mat.toString();
		case 24:
			switch ((int) damage) {
			case 0:
				return "SANDSTONE";
			case 1:
				return "CHISELED_SANDSTONE";
			case 2:
				return "SMOOTH_SANDSTONE";
			}
			return mat.toString();
		case 31:
			switch ((int) damage) {
			case 0:
				return "DEAD_SHRUB";
			case 1:
				return "TALL_GRASS";
			case 2:
				return "FERN";
			}
			return mat.toString();
		case 44:
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
			}
			return mat.toString();
		case 383:
			switch ((int) damage) {
			case 50:
				return "CREEPER_EGG";
			case 51:
				return "SKELETON_EGG";
			case 52:
				return "SPIDER_EGG";
			case 53:
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
			case 120:
				return "VILLAGER_EGG";
			case 200:
				return "ENDER_CRYSTAL_EGG";
			case 14:
				return "PRIMED_TNT_EGG";
			case 66:
				return "WITCH_EGG";
			case 65:
				return "BAT_EGG";
			}
			return mat.toString();
		case 397:
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
		case 76:
			return "REDSTONE_TORCH";
		case 115:
			return "NETHER_WART";
		case 30:
			return "COBWEB";
		case 102:
			return "GLASS_PANE";
		case 101:
			return "IRON_BARS";
		case 58:
			return "CRAFTING_TABLE";
		case 123:
			return "REDSTONE_LAMP";
		case 392:
			return "POTATO";
		case 289:
			return "GUNPOWDER";
		case 391:
			return "CARROT";
		case 322:
			switch ((int) damage) {
			case 0:
				return "GOLDEN_APPLE";
			case 1:
				return "ENCHANTED_GOLDEN_APPLE";
			}
			break;
		case 390:
			return "FLOWER_POT";
		case 145:
			switch ((int) damage) {
			case 0:
				return "ANVIL";
			case 1:
				return "SLIGHTLY_DAMAGED_ANVIL";
			case 2:
				return "VERY_DAMAGED:ANVIL";
			}
			break;
		case 384:
			return "BOTTLE_O'_ENCHANTING";
		case 402:
			return "FIREWORK_STAR";
		case 385:
			return "FIREWORK_CHARGE";
		}

		if (damage == 0 || isTool(mat))
			return mat.toString();
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
	 * Formats the given number according to how vault would like it. E.g. $50
	 * or 5 dollars.
	 * 
	 * @return The formatted string.
	 */
	/*
	 * public static String format(double n) { try { return
	 * plugin.getEcon().format(n); } catch (NumberFormatException e) { return
	 * "$" + n; } }
	 */

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
	 * Returns the price of a block of the material or -1 if unknown
	 * 
	 * @return The price of the block
	 */

	public static double getPrice(Material type) {
		if (blockPrices.containsKey(type)) {
			return (Double) blockPrices.get(type);
		}
		return -10000;
	}

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
				return (dyePrices.get((int)(15-durability)) + blockPrices.get(Material.WOOL));
			case INK_SACK:
				return (dyePrices.get((int)durability));
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
			case POTION:
				switch ((int) durability) {
				case 0:
					// WATER BOTTLE
				case 7:
					/*
					 * 373:16 Awkward Potion 373:32 Thick Potion 373:64 Mundane
					 * Potion 373:8193 Regeneration Potion (0:45) 373:8194
					 * Swiftness Potion (3:00) 373:8195 Fire Resistance Potion
					 * (3:00) 373:8196 Poison Potion (0:45) 373:8197 Healing
					 * Potion 373:8198 Night Vision Potion (3:00) 373:8200
					 * Weakness Potion (1:30) 373:8201 Strength Potion (3:00)
					 * 373:8202 Slowness Potion (1:30) 373:8204 Harming Potion
					 * 373:8205 Water Breathing Potion (3:00) 373:8206
					 * Invisibility Potion (3:00) 373:8225 Regeneration Potion
					 * II (0:22) 373:8226 Swiftness Potion II (1:30) 373:8228
					 * Poison Potion II (0:22) 373:8229 Healing Potion II
					 * 373:8233 Strength Potion II (1:30) 373:8236 Harming
					 * Potion II 373:8257 Regeneration Potion (2:00) 373:8258
					 * Swiftness Potion (8:00) 373:8259 Fire Resistance Potion
					 * (8:00) 373:8260 Poison Potion (2:00) 373:8262 Night
					 * Vision Potion (8:00) 373:8264 Weakness Potion (4:00)
					 * 373:8265 Strength Potion (8:00) 373:8266 Slowness Potion
					 * (4:00) 373:8269 Water Breathing Potion (8:00) 373:8270
					 * Invisibility Potion (8:00) 373:8289 Regeneration Potion
					 * II (1:00) 373:8290 Swiftness Potion II (4:00) 373:8292
					 * Poison Potion II (1:00) 373:8297 Strength Potion II
					 * (4:00) 373:16385 Regeneration Splash (0:33) 373:16386
					 * Swiftness Splash (2:15) 373:16387 Fire Resistance Splash
					 * (2:15) 373:16388 Poison Splash (0:33) 373:16389 Healing
					 * Splash 373:16390 Night Vision Splash (2:15) 373:16392
					 * Weakness Splash (1:07) 373:16393 Strength Splash (2:15)
					 * 373:16394 Slowness Splash (1:07) 373:16396 Harming Splash
					 * 373:16397 Breathing Splash (2:15) 373:16398 Invisibility
					 * Splash (2:15) 373:16417 Regeneration Splash II (0:16)
					 * 373:16418 Swiftness Splash II (1:07) 373:16420 Poison
					 * Splash II (0:16) 373:16421 Healing Splash II 373:16425
					 * Strength Splash II (1:07) 373:16428 Harming Splash II
					 * 373:16449 Regeneration Splash (1:30) 373:16450 Swiftness
					 * Splash (6:00) 373:16451 Fire Resistance Splash (6:00)
					 * 373:16452 Poison Splash (1:30) 373:16454 Night Vision
					 * Splash (6:00) 373:16456 Weakness Splash (3:00) 373:16457
					 * Strength Splash (6:00) 373:16458 Slowness Splash (3:00)
					 * 373:16461 Breathing Splash (6:00) 373:16462 Invisibility
					 * Splash (6:00) 373:16481 Regeneration Splash II (0:45)
					 * 373:16482 Swiftness Splash II (3:00) 373:16484 Poison
					 * Splash II (0:45) 373:16489 Strength Splash II (3:00)
					 */
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
			case COAL:
				switch (durability) {
				case 0:
					// return "COAL";
				case 1:
					// "CHARCOAL";
				}
			case SANDSTONE:
				switch ((int) durability) {
				case 0:
					// "SANDSTONE";
				case 1:
					// "CHISELED_SANDSTONE";
				case 2:
					// "SMOOTH_SANDSTONE";
				}

			case LONG_GRASS:
				switch ((int) durability) {
				case 0:
					// "DEAD_SHRUB";
				case 1:
					// "TALL_GRASS";
				case 2:
					// "FERN";
				}

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

			case MONSTER_EGG:
				switch ((int) durability) {
				case 50:
					// "CREEPER_EGG";
				case 51:
					// "SKELETON_EGG";
				case 52:
					// "SPIDER_EGG";
				case 53:
					// "GIANT_EGG";
				case 54:
					// "ZOMBIE_EGG";
				case 55:
					// "SLIME_EGG";
				case 56:
					// "GHAST_EGG";
				case 57:
					// "ZOMBIE_PIGMAN_EGG";
				case 58:
					// "ENDERMAN_EGG";
				case 59:
					// "CAVE_SPIDER_EGG";
				case 60:
					// "SILVERFISH_EGG";
				case 61:
					// "BLAZE_EGG";
				case 62:
					// "MAGMA_CUBE_EGG";
				case 63:
					// "ENDER_DRAGON_EGG";
				case 90:
					// "PIG_EGG";
				case 91:
					// "SHEEP_EGG";
				case 92:
					// "COW_EGG";
				case 93:
					// "CHICKEN_EGG";
				case 94:
					// "SQUID_EGG";
				case 95:
					// "WOLF_EGG";
				case 96:
					// "MOOSHROOM_EGG";
				case 97:
					// "SNOW_GOLEM_EGG";
				case 98:
					// "OCELOT_EGG";
				case 99:
					// "IRON_GOLEM_EGG";
				case 120:
					// "VILLAGER_EGG";
				case 200:
					// "ENDER_CRYSTAL_EGG";
				case 14:
					// "PRIMED_TNT_EGG";
				case 66:
					// "WITCH_EGG";
				case 65:
					// "BAT_EGG";
				}

			case SKULL_ITEM:
				switch ((int) durability) {
				case 0:
					// "SKELETON_SKULL";
				case 1:
					// "WITHER_SKULL";
				case 2:
					// "ZOMBIE_HEAD";
				case 3:
					// "PLAYER_HEAD";
				case 4:
					// "CREEPER_HEAD";
				}
				break;
			case GOLDEN_APPLE:
				switch ((int) durability) {
				case 0:
					// "GOLDEN_APPLE";
				case 1:
					// "ENCHANTED_GOLDEN_APPLE";
				}
				break;
			case ANVIL:
				switch ((int) durability) {
				case 0:
					// "ANVIL";
				case 1:
					// "SLIGHTLY_DAMAGED_ANVIL";
				case 2:
					// "VERY_DAMAGED:ANVIL";
				}
				break;
			case RED_ROSE:
				switch ((int) durability) {
				case 0:
					// "POPPY";
				case 1:
					/*
					 * 38:1 Blue Orchid 38:2 Allium 38:4 Red Tulip 38:5 Orange
					 * Tulip 38:6 White Tulip 38:7 Pink Tulip 38:8 Oxeye Daisy
					 */
				}
			case STAINED_GLASS:
				plugin.getLogger().info("Stained glass");
				return (dyePrices.get((int)(15-durability)) + blockPrices.get(Material.GLASS));
					/*
					 * 
					  95:1 Stained Glass (Orange)
					  95:2 Stained Glass (Magenta)
					  95:3 Stained Glass (Light Blue)
					  95:4 Stained Glass (Yellow)
					  95:5 Stained Glass (Lime)
					  95:6 Stained Glass (Pink)
					  95:7 Stained Glass (Gray)
					  95:8 Stained Glass (Light Grey)
					  95:9 Stained Glass (Cyan)
					  95:10 Stained Glass (Purple)
					  95:11 Stained Glass (Blue)
					  95:12 Stained Glass (Brown)
					  95:13 Stained Glass (Green)
					  95:14 Stained Glass (Red)
					  95:15 Stained Glass (Black)
					 */
			case CARPET:
				return (dyePrices.get((int)(15-durability)) + 2/3 * blockPrices.get(Material.WOOL));
				/*
				 * 
				 171:1 Carpet (Orange)
				 171:2 Carpet (Magenta)
				 171:3 Carpet (Light Blue)
				 171:4 Carpet (Yellow)
				 171:5 Carpet (Lime)
				 171:6 Carpet (Pink)
				 171:7 Carpet (Grey)
				 171:8 Carpet (Light Gray)
				 171:9 Carpet (Cyan)
				 171:10 Carpet (Purple)
				 171:11 Carpet (Blue)
				 171:12 Carpet (Brown)
				 171:13 Carpet (Green)
				 171:14 Carpet (Red)
				 171:15 Carpet (Black)
				 */
			default:
				// Nothing
			}
		} else {
			// Adjust price for damage to the item
			if (maxDurability > 0) {
				damageModifier = 1 - (double) durability
						/ (double) maxDurability;
			}
		}
		if (blockPrices.containsKey(type)) {
			return (Double) blockPrices.get(type) * damageModifier;
			//return (Double) blockPrices.get(Material.BAKED_POTATO);
		} else {
			return -10010;
		}
	}
}
