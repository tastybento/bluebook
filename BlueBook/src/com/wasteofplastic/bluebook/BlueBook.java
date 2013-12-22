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

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.block.Block;

import com.wasteofplastic.bluebook.util.Util;
import com.wasteofplastic.bluebook.MetricsLite;

public class BlueBook extends JavaPlugin implements Listener {
	/** The active instance of BlueBook */
	public static BlueBook instance;
	
	public static Economy econ = null;
	
	public double profit = 0.0;

	// Version of the Plugin
	private static double version = 1.2;
	
	@Override
	public void onDisable() {
		this.reloadConfig();
	}

	public void onEnable() {
		instance = this;
		saveDefaultConfig(); // Creates the config folder and copies config.yml
								// (If one doesn't exist) as required.
		reloadConfig(); // Reloads config.yml
		getConfig().options().copyDefaults(true); // Load defaults.
		// Pull out a few variables
		Double configProfit = this.getConfig().getDouble("profit");
		if (configProfit != null && configProfit > 0.0) {
			profit = configProfit;
		}
		
		if (!setupEconomy()) {
			getLogger().severe("Vault not found. Plugin Disabling");
			getServer().getPluginManager().disablePlugin(this);
		}
		PluginManager pm = getServer().getPluginManager(); // Registers the
															// plugin
		pm.registerEvents(this, this);
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
		// This plugin triggers if you hit a wall sign, chest or trapped chest
		// TODO: Enable these items to be set in the config.
		if (!b.getType().equals(Material.WALL_SIGN)
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
				&& p.hasPermission("bluebook.show")) {
			// Find out if this item is damaged or not, i.e. durability
			short durability = itemInHand.getDurability();
			// Find out the max durability of the item
			short maxDurability = itemInHand.getType().getMaxDurability();
			//p.sendMessage(ChatColor.BLUE + "[BlueBook "+version+ "] " + ChatColor.GOLD
			//		+ Util.getName(itemInHand) + " durability is " + durability + " out of " + maxDurability);
			/*
			// If the max and current durability are zero then this is just a block or normal item
			// If the current durability is greater than the max, then it's a variant on the current item, e.g. potion, etc.
			if (durability > maxDurability) {
				// This is a special item
				
			} else {
				// Adjust price for damage to the item
				if (maxDurability > 0) {
					damageModifier = 1 - (double)durability/(double)maxDurability;
					p.sendMessage(ChatColor.BLUE + "[BlueBook] " + ChatColor.GOLD
							+  " damageModifier = " + damageModifier);
				}
			}
			*/
			// Tell player what the guide price is.
			//double price = damageModifier * Util.getPrice(itemInHand.getType());
			double price = Util.getPrice(itemInHand);
			//getLogger().info("Golden carrot" + Util.blockPrices.get(Material.GOLDEN_CARROT));
			// Add the % profit
			price = price + price * (profit/100);
			// If the price is negative, it is undefined, or parts of the
			// crafted item are unknown
			if (price < 0.0) {
				// We do not know what the price is! This is a catch all for
				// unknown prices - literally price-less
				// TODO: Allow the prefix of the message to be configured
				p.sendMessage(ChatColor.BLUE + "[BlueBook" + version + "] " + ChatColor.GOLD
						+ Util.getName(itemInHand) + " is priceless!");
			} else {
				if (maxDurability > 0 && durability > 0) {
					// Item is worn down somewhat
					p.sendMessage(ChatColor.BLUE + "[BlueBook" + version + "] " + ChatColor.GOLD
							+ "Worn " + Util.getName(itemInHand) + " ~"
							+ econ.format(price) + " each");
				} else if (durability == 0 && maxDurability > 0){
					p.sendMessage(ChatColor.BLUE + "[BlueBook" + version + "] " + ChatColor.GOLD
							+ "Mint " + Util.getName(itemInHand) + " ~"
							+ econ.format(price) + " each");
				} else {
					p.sendMessage(ChatColor.BLUE + "[BlueBook" + version + "] " + ChatColor.GOLD
							+ Util.getName(itemInHand) + " ~"
							+ econ.format(price) + " each");
					
				}
			}
		}
	}

	/**
	 * Returns the economy
	 * 
	 * @return The economy object
	 */
	public Economy getEcon() {
		return econ;
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
}
