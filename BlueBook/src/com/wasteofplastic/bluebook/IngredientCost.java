package com.wasteofplastic.bluebook;

import org.bukkit.inventory.ItemStack;

public class IngredientCost {
    private ItemStack ingredient;
    private double cost = 0D;

    /**
     * @param ingredient
     * @param cost
     */
    public IngredientCost(ItemStack ingredient, double cost) {
	this.ingredient = ingredient;
	this.cost = cost;
    }
    /**
     * @return the ingredient
     */
    public ItemStack getIngredient() {
        return ingredient;
    }
    /**
     * @param ingredient the ingredient to set
     */
    public void setIngredient(ItemStack ingredient) {
        this.ingredient = ingredient;
    }
    /**
     * @return the cost
     */
    public double getCost() {
        return cost;
    }
    /**
     * @param cost the cost to set
     */
    public void setCost(double cost) {
        this.cost = cost;
    }
    
    
}
