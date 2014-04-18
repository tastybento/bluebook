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
package com.wasteofplastic.bluebook;

import java.io.IOException;
import java.util.Iterator;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import com.wasteofplastic.bluebook.util.Util;
import com.wasteofplastic.bluebook.MetricsLite;

public class BlueBook extends JavaPlugin implements Listener {
	/** The active instance of BlueBook */
	public static BlueBook instance;
	
	public static Economy econ = null;
	
	public double profit = 0.0;
	public double enchantValue = 1.0;
	public String version = "";
	
	@Override
	public void onDisable() {
		this.reloadConfig();
	}

	public void onEnable() {
		instance = this;
		loadYamls();
		PluginManager pm = getServer().getPluginManager(); // Registers the
															// plugin
		pm.registerEvents(this, this);
		Plugin p = this;
		if (p != null) {
		    PluginDescriptionFile pdf = p.getDescription();
		    version = pdf.getVersion();
		}

	   	// Send stats
    	try {
    	    MetricsLite metrics = new MetricsLite(this);
    	    metrics.start();
    	} catch (IOException e) {
    	    // Failed to submit the stats :-(
    	}

	}

	/**
	 * Handles players left clicking a chest. Left click a NORMAL chest with
	 * item : Provide guide price of held item
	 */

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onClick(PlayerInteractEvent e) {
		// Fast return if this is not a left click
		if (e.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		// Find out what block is being clicked
		Block b = e.getClickedBlock();
		// This plugin triggers if you hit a wall sign, sign, chest or trapped chest
		// TODO: Enable these items to be set in the config.
		if (!b.getType().equals(Material.WALL_SIGN)
				&& !b.getType().equals(Material.SIGN_POST)
				&& !b.getType().equals(Material.CHEST)
				&& !b.getType().equals(Material.TRAPPED_CHEST))
			return;
		// Find out who is doing the clicking
		Player p = e.getPlayer();
		// Get the item they are hitting
		ItemStack item = e.getItem();
		// Get the item in their hand
		ItemStack itemInHand = p.getItemInHand();
		// Initialize variables
		//double damageModifier = 1;

		// Handles informing price and checking the permission
		if (item != null && item.getType() != Material.AIR
				&& (p.hasPermission("bluebook.show") || p.hasPermission("bluebook.signshow"))) {
			// Find out if this item is damaged or not, i.e. durability
			short durability = itemInHand.getDurability();
			// Find out the max durability of the item
			short maxDurability = itemInHand.getType().getMaxDurability();
			// For DEBUG
			//p.sendMessage(ChatColor.BLUE + "[BlueBook "+version+ "] " + ChatColor.GOLD
			//		+ Util.getName(itemInHand) + " durability is " + durability + " out of " + maxDurability);
			// Tell player what the guide price is.
			double price = Util.getPrice(itemInHand);
			// Find out the value of enchantments
			// Find out if this item is enchanted or not
			if (!item.getEnchantments().isEmpty()) {
				//getLogger().info("Item is enchanted - Base price = " + Util.format(price));
				price = price + price * Util.getEnchantmentValue(item) * enchantValue;
			}
			// Add the % profit
			price = price + price * (profit/100);
			// If the price is negative, it is undefined, or parts of the
			// crafted item are unknown
			// Create the text to show the user
			String lineOne = "";
			String lineTwo = "";
			String lineThree = "";
			String enchanted = "";
			if (!itemInHand.getEnchantments().isEmpty()) {
				 enchanted = "Enchanted";
			}

			if (price < 0.0) {
				// We do not know what the price is! This is a catch all for
				// unknown prices - literally price-less
				// TODO: Allow the prefix of the message to be configured
				lineOne = enchanted;
				lineTwo = Util.getName(itemInHand);
				lineThree = "is priceless!";
			} else {
				if (maxDurability > 0 && durability > 0) {
					// Item is worn down somewhat
					lineOne = "Worn " + enchanted;
					lineTwo = Util.getName(itemInHand);
					lineThree = Util.format(price) + " each";
				} else if (durability == 0 && maxDurability > 0){
					lineOne = "Mint " + enchanted;
					lineTwo = Util.getName(itemInHand);
					lineThree = Util.format(price) + " each";
				} else {
					if (lineOne == "") {
						lineOne = Util.getName(itemInHand);
						lineTwo = Util.format(price) + " each";	
					} else {
						lineOne = enchanted;
						lineTwo = Util.getName(itemInHand);
						lineThree = Util.format(price) + " each";	
					}
				}
			}
			if (p.hasPermission("bluebook.signshow")) {
				// Look around for any nearby signs close by that has [VALUE]
				// TODO: Enable this to be determined in settings
				int x = (int)p.getLocation().getX();
				int y = (int)p.getLocation().getY();
				int z = (int)p.getLocation().getZ();
				for (int scanx = x - 5; scanx < x+6; scanx++) {
					for (int scany = y - 5; scany < y+6; scany++) {
						for (int scanz = z - 5; scanz < z+6; scanz++) {
							final Location loc = new Location(p.getWorld(),scanx,scany,scanz);
							//getLogger().info(loc.toString());
							Block block = loc.getBlock();
							//getLogger().info("Block type = " + block.getType().toString());
							if (block.getType().equals(Material.WALL_SIGN) || block.getType().equals(Material.SIGN_POST)) {
								Sign thesign = (Sign) block.getState();
								if (thesign!=null) {
									//getLogger().info("Sign is not null");
						            if (thesign.getLine(0).equalsIgnoreCase("[VALUE]")) {
						            	//getLogger().info("Value found");
						            	thesign.setLine(1, lineOne);
						            	thesign.setLine(2, lineTwo);
						            	thesign.setLine(3, lineThree);
						            	thesign.update();
						            }
								}
							}
						}
					}
				}
			}
			if (p.hasPermission("bluebook.show")) {
				p.sendMessage(ChatColor.BLUE + "[BlueBook " + version + "] " + ChatColor.GOLD
						+ lineOne + " " + lineTwo + " " + lineThree + ".");
			} 
		}
	}
	
	public void loadYamls() {
		saveDefaultConfig(); // Creates the config folder and copies config.yml
		// (If one doesn't exist) as required.
		reloadConfig(); // Reloads config.yml
		getConfig().options().copyDefaults(true); // Load defaults.
		// Pull out a few variables and put some limit checking in just in case
		// someone goes crazy
		Double configProfit = this.getConfig().getDouble("profit");
		if (configProfit != null && configProfit > -1000.0
				&& configProfit < 1000.0) {
			profit = configProfit;
		}
		Double configEnchantValue = this.getConfig().getDouble("enchantments");
		if (configEnchantValue != null && configEnchantValue > -1000.0
				&& configEnchantValue < 1000.0) {
			enchantValue = configEnchantValue;
		}
		String configCurrency = this.getConfig().getString("currency");
		if (configCurrency != null && configCurrency.length() < 10) {
			Util.setCurrency(configCurrency);
		} else {
			Util.setCurrency("$");
		}
		// Load the rest of the prices
		Util.loadPrices();
		Util.calculatePrices();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	if(cmd.getName().equalsIgnoreCase("bbreload")) {
    		// Reload the config file
    		loadYamls();
    		sender.sendMessage(ChatColor.BLUE + "[BlueBook " + version + "] " + ChatColor.GOLD
					+ "Reloaded");
    		return true;
    	} else {
    		return false;
    	}
    }
}
