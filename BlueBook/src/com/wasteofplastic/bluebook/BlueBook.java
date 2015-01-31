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
import java.util.ArrayList;
import java.util.List;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.wasteofplastic.bluebook.util.Util;

public class BlueBook extends JavaPlugin implements Listener {
    /** The active instance of BlueBook */
    public static BlueBook instance;

    public static Economy econ = null;

    public double profit = 0.0;
    public double enchantValue = 1.0;
    private static Util worldPrice = null;
    // What get's displayed when there is a quote in chat
    private String configText = "";

    @Override
    public void onDisable() {
	//this.reloadConfig();
    }

    public void onEnable() {
	instance = this;
	worldPrice = new Util();		

	loadConfig();
	worldPrice.loadPrices(null);
	worldPrice.calculatePrices(null);
	// Register events
	PluginManager pm = getServer().getPluginManager();
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
	// This plugin triggers if you hit a wall sign, sign, chest or trapped chest
	// TODO: Enable these items to be set in the config.
	if (!b.getType().equals(Material.WALL_SIGN)
		&& !b.getType().equals(Material.SIGN_POST)
		&& !b.getType().equals(Material.CHEST)
		&& !b.getType().equals(Material.TRAPPED_CHEST))
	    return;
	// Find out who is doing the clicking
	Player p = e.getPlayer();
	// Load the prices for this world
	worldPrice.loadPrices(p.getWorld().toString());
	worldPrice.calculatePrices(p.getWorld().toString());

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
	    //getLogger().info(worldPrice.getName(itemInHand) + " durability is " + durability + " out of " + maxDurability);
	    // Tell player what the guide price is.
	    double price = worldPrice.getPrice(itemInHand,p.getWorld().getName());
	    //double price = 1D;
	    // Find out the value of enchantments
	    // Find out if this item is enchanted or not
	    if (!item.getEnchantments().isEmpty()) {
		getLogger().info("Item is enchanted - Base price = " + Util.format(price));
		price = price + price * worldPrice.getEnchantmentValue(item) * enchantValue;
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
		enchanted = " Enchanted";
	    }

	    if (price < 0.0) {
		// We do not know what the price is! This is a catch all for
		// unknown prices - literally price-less
		// TODO: Allow the prefix of the message to be configured
		lineOne = enchanted;
		lineTwo = worldPrice.getName(itemInHand);
		lineThree = "is priceless!";
	    } else {
		if (maxDurability > 0 && durability > 0) {
		    // Item is worn down somewhat
		    lineOne = "Worn" + enchanted;
		    lineTwo = worldPrice.getName(itemInHand);
		    lineThree = Util.format(price) + " each";
		} else if (durability == 0 && maxDurability > 0){
		    lineOne = "Mint" + enchanted;
		    lineTwo = worldPrice.getName(itemInHand);
		    lineThree = Util.format(price) + " each";
		} else {
		    if (lineOne == "") {
			lineOne = worldPrice.getName(itemInHand);
			lineTwo = Util.format(price) + " each";	
		    } else {
			lineOne = enchanted;
			lineTwo = worldPrice.getName(itemInHand);
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
		if (lineOne == "") {
		    p.sendMessage(configText + " " + ChatColor.GOLD
			    + lineTwo + " " + lineThree);
		} else {
		    p.sendMessage(configText + " " + ChatColor.GOLD
			    + lineOne + " " + lineTwo + " " + lineThree);
		}
		/*
				ItemMeta i = p.getItemInHand().getItemMeta();
				List<String> lore = new ArrayList<String>();
				lore.add("For Sale");
				i.setLore(lore);
				p.getItemInHand().setItemMeta(i);
				p.sendMessage("Item is for sale!");
		 */
	    } 
	}
    }



    /**
     * Loads the configuration and checks settings in config.yml
     */
    public void loadConfig() {
	saveDefaultConfig(); // Creates the config folder and copies config.yml
	// (If one doesn't exist) as required.
	reloadConfig(); // Reloads config.yml
	getConfig().options().copyDefaults(true); // Load defaults.
	// Pull out a few variables and put some limit checking in just in case
	// someone goes crazy
	Double configProfit = this.getConfig().getDouble("profit",5D);
	if (configProfit > -1000D && configProfit < 1000D) {
	    profit = configProfit;
	} else {
	    getLogger().warning("Profit multiplier out of range (-1000 to 1000). Setting to 5");
	    profit = 5D;		    
	}
	Double configEnchantValue = this.getConfig().getDouble("enchantments",1D);
	if (configEnchantValue > -1000.0
		&& configEnchantValue < 1000.0) {
	    enchantValue = configEnchantValue;
	} else {
	    getLogger().warning("Enchantment multiplier out of range (-1000 to 1000). Setting to 1");
	    enchantValue = 1D;
	}
	String configCurrency = getConfig().getString("currency","$");
	if (configCurrency.length() < 10) {
	    worldPrice.setCurrency(configCurrency);
	} else {
	    getLogger().warning("Currency length must be less than 10 characters long. Truncating.");
	    worldPrice.setCurrency(configCurrency.substring(0, 9));
	}
	configText = getConfig().getString("quote-text","");
	if (configText.isEmpty()) {
	    PluginDescriptionFile pdf = this.getDescription();
	    configText = ChatColor.BLUE + "[BlueBook " + pdf.getVersion() + "]";
	} 

	// Load the rest of the prices
	//worldPrice.loadPrices();
	//worldPrice.calculatePrices();
    }

    /*
     * (non-Javadoc)
     * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
	if(cmd.getName().equalsIgnoreCase("bbreload")) {
	    // Reload the config file
	    loadConfig();
	    worldPrice.thisWorld = "";
	    sender.sendMessage(configText + " " + ChatColor.GOLD
		    + "Reloaded");
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * @return the worldPrice
     */
    public static Util getWorldPrice() {
	return worldPrice;
    }
}
