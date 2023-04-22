package com.wasteofplastic.bluebook;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import com.google.common.base.Enums;

/**
 * Calculates the price of an item stack
 * @author tastybento
 *
 */
public class PriceCalculator {

    private record IngrCost(ItemStack item, double cost) {}
    private Map<Material, Double> blockPrices = new EnumMap<>(Material.class);
    private BlueBook plugin;

    /**
     * @param plugin
     */
    public PriceCalculator(BlueBook plugin) {
        this.plugin = plugin;
        loadPrices();
    }


    public void loadPrices() {
        plugin.getLogger().info("Loading prices");
        // Initialize all block prices so that if any are missing nothing bad
        // happens
        // Check if the path exists, otherwise use the default
        Map<String, Object> defaultMap = plugin.getConfig().getConfigurationSection("block-prices").getValues(false);
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


    private Double getBlockPrice(Material mat) {
        return blockPrices.get(mat);
    }

    public double getPrice(ItemStack item) {
        List<IngrCost> ingredients = getIngredients(item, 0);
        double cost = 0D;
        for ( IngrCost ingredient : ingredients) {
            cost += ingredient.cost;
        }
        return cost;
    }

    /**
     * Calculate the recipe for this item in hand
     * @param i itemstack to do
     * @return List of recipe items and their factored cost
     */
    private List<IngrCost> getIngredients(ItemStack i, int iteration) {
        if (iteration > 3) {
            Bukkit.getLogger().info("Too many iterations");
            return null;
        }
        ItemStack item = new ItemStack(i.getType());
        // If we know the price of this item immediately (it's in config) then return that, not the recipe
        if (blockPrices.containsKey(item.getType())) {
            Bukkit.getLogger().info("Price of " + item.getType().name() + " is known and is $" + getBlockPrice(item.getType()));
            return List.of(new IngrCost(item, getBlockPrice(item.getType())));
        }
        Bukkit.getLogger().info("Calculating price of " + item.getType());

        // Look up recipe for ItemStack
        List<Recipe> recipe = Bukkit.getServer().getRecipesFor(item);
        if (recipe.isEmpty()) {
            // There is no recipe for this itemstack and we do not know the price!
            Bukkit.getLogger().warning("I do not know the price of " + item.getType().name() + ". Add it to config.yml!");
            return List.of(new IngrCost(item, 0D));
        } else {
            return getBestIngredients(item.getType(), recipe, iteration);
        }
    }

    private List<IngrCost> getBestIngredients(Material material, List<Recipe> recipe, int iteration) {
        List<IngrCost> bestIngredients = new ArrayList<IngrCost>();
        List<IngrCost> ingredients = new ArrayList<IngrCost>();
        double lowestCost = Double.MAX_VALUE;
        double highestCost = Double.MIN_VALUE;
        for (Recipe r : recipe) {
            Bukkit.getLogger().info("Debug: Recipe is a " + r.getClass().getName());
            Double cost = 0D;
            if (r instanceof ShapedRecipe sr) {
                cost = shapedRecipe(sr, r, ingredients, material, iteration);
            } else if (r instanceof ShapelessRecipe slr) {
                cost = shapeless(slr, r, ingredients, material, iteration);
            } else if (r instanceof FurnaceRecipe fr) {
                cost = furnace(fr, r, ingredients, material, iteration);
            } else if (r instanceof BlastingRecipe fr) {
                cost = blast(fr, r, ingredients, material, iteration);
            }
            // Cost obtained
            if (cost != null) {
                // Store best price
                if (lowestCost > cost) {
                    lowestCost = cost;
                    // Store this one
                    bestIngredients = new ArrayList<>(ingredients);
                }
                if (highestCost < cost) {
                    highestCost = cost;
                }
            }
        }
        if (bestIngredients.isEmpty()) {
            // There is no recipe for this itemstack and we do not know the price!
            Bukkit.getLogger().warning("No workable recipe found. I do not know the price of " + material.name() + ". Add it to config.yml!");
            return List.of(new IngrCost(new ItemStack(material), 0D));
        }
        Bukkit.getLogger().info("Lowest cost is $" + lowestCost);
        Bukkit.getLogger().info("Highest cost is $" + highestCost);
        Bukkit.getLogger().info("Final recipe is");
        //
        bestIngredients.forEach(ic -> {
            Bukkit.getLogger().info(ic.item().getType() + " = $" + ic.cost);
        });
        return bestIngredients;
    }


    private Double blast(BlastingRecipe fr, Recipe r, List<IngrCost> ingredients, Material type, int iteration) {
        Bukkit.getLogger().info("Blasting Recipe");
        double cost = 0D;
        ItemStack in = fr.getInput();
        if (blockPrices.containsKey(fr.getInput().getType())) {
            cost = getBlockPrice(in.getType()) / fr.getResult().getAmount();
        } else {
            if (in.getType().equals(type)) {
                // Infinite loop -
                return null;
            } else {
                // iteration!
                List<IngrCost> subIng = getIngredients(in, iteration + 1);
                if (subIng == null) {
                    return null;
                }
                ingredients.addAll(subIng);
            }
        }
        ingredients.add(new IngrCost(in, cost));
        // Coal must be in config
        double coalCost = this.getPrice(new ItemStack(Material.COAL));
        cost = coalCost / 8D;
        ingredients.add(new IngrCost(new ItemStack(Material.COAL), cost));
        return cost;

    }


    private Double furnace(FurnaceRecipe fr, Recipe r, List<IngrCost> ingredients, Material type, int iteration) {
        Bukkit.getLogger().info("Furnace Recipe");
        double cost = 0D;
        ItemStack in = fr.getInput();
        if (blockPrices.containsKey(fr.getInput().getType())) {
            cost = getBlockPrice(in.getType()) / fr.getResult().getAmount();
        } else {
            if (in.getType().equals(type)) {
                // Infinite loop -
                return null;
            } else {
                // iteration!
                List<IngrCost> subIng = getIngredients(in, iteration + 1);
                if (subIng == null) {
                    return null;
                }
                ingredients.addAll(subIng);
            }
        }
        ingredients.add(new IngrCost(in, cost));
        // Coal must be in config
        double coalCost = this.getPrice(new ItemStack(Material.COAL));
        cost = coalCost / 8D;
        ingredients.add(new IngrCost(new ItemStack(Material.COAL), cost));
        return cost;
    }


    private Double shapeless(ShapelessRecipe slr, Recipe r, List<IngrCost> ingredients, Material type, int iteration) {
        Bukkit.getLogger().info("Shapeless Recipe");
        double totalCost = 0D;
        for (ItemStack ingredient : slr.getIngredientList()) {
            if (ingredient != null) {
                Bukkit.getLogger().info("Ingredient is " + ingredient.toString());
                double cost = 0D;
                if (blockPrices.containsKey(ingredient.getType())) {
                    cost = getBlockPrice(ingredient.getType()) / slr.getResult().getAmount();
                    ingredients.add(new IngrCost(ingredient, cost));
                    totalCost += cost;
                } else {
                    if (ingredient.getType().equals(type)) {
                        // Infinite loop - try and see if there is another recipe
                        return null;
                    } else {
                        // iteration!
                        List<IngrCost> subIng = getIngredients(ingredient, iteration + 1);
                        if (subIng == null) {
                            return null;
                        }
                        ingredients.addAll(subIng);
                    }
                }
            }
        }
        Bukkit.getLogger().info("Result = " + slr.getResult().toString());
        return totalCost;
    }


    private Double shapedRecipe(ShapedRecipe sr, Recipe r, List<IngrCost> ingredients, Material material, int iteration) {
        Bukkit.getLogger().info("Shaped Recipe");
        double totalCost = 0D;
        for (ItemStack ingredient : sr.getIngredientMap().values()) {
            if (ingredient != null) {
                Bukkit.getLogger().info("Ingredient is " + ingredient.toString());
                double cost = 0D;
                if (blockPrices.containsKey(ingredient.getType())) {
                    cost = getBlockPrice(ingredient.getType()) / sr.getResult().getAmount(); // Divide by the number created
                    ingredients.add(new IngrCost(ingredient, cost));
                    totalCost += cost;
                } else {
                    if (ingredient.getType().equals(material)) {
                        // Infinite loop -
                        return null;
                    } else {
                        // iteration!
                        List<IngrCost> subIng = getIngredients(ingredient, iteration + 1);
                        if (subIng == null) {
                            return null;
                        }
                        ingredients.addAll(subIng);
                    }
                }
            }
        }
        return totalCost;
    }
}
