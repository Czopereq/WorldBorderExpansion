package org.example;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;

public class Main extends JavaPlugin implements Listener {

    NamespacedKey key = new NamespacedKey(this, "wbexpand");

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        if (!new File("plugins" + File.separator + "WorldBorderExpansion" + File.separator + "config.yml").exists()) {
            Bukkit.getConsoleSender().sendMessage("Nie wykryto configu! Generowanie...");
            saveDefaultConfig();
        }
        ItemStack itemGreenDye = new ItemStack(Material.GREEN_DYE);
        ItemMeta itemGreenDyeMeta = itemGreenDye.getItemMeta();
        itemGreenDyeMeta.setDisplayName(ChatColor.BLUE + "Czesc przedmiotu do powiekszenia bordera");
        itemGreenDyeMeta.setLore(Collections.singletonList(ChatColor.GOLD + "Zbierz pozostałe 9 części, aby stworzyć przedmiot do powiększenia granicy świata"));
        itemGreenDyeMeta.addEnchant(Enchantment.DURABILITY, 10, true);
        itemGreenDyeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemGreenDyeMeta.setCustomModelData(997);
        itemGreenDye.setItemMeta(itemGreenDyeMeta);

        ShapedRecipe wbeshard = new ShapedRecipe(new NamespacedKey(this, "itemgreendye"), itemGreenDye);
        wbeshard.shape("XXX", "XXX", "XXX");
        wbeshard.setIngredient('X', Material.BEDROCK);
        Bukkit.addRecipe(wbeshard);

        ItemStack wbexpand = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta wbexpandmeta = wbexpand.getItemMeta();
        wbexpandmeta.setDisplayName(ChatColor.AQUA + "Powiekszanie Bordera");
        wbexpandmeta.setLore(Collections.singletonList(ChatColor.GOLD + "Nacisnij PPM aby powiekszyc border!"));
        wbexpandmeta.addEnchant(Enchantment.DURABILITY, 1, true);
        wbexpandmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        wbexpand.setItemMeta(wbexpandmeta);

        ShapedRecipe wbe = new ShapedRecipe(key, wbexpand);
        wbe.shape("XXX", "XXX", "XXX");
        wbe.setIngredient('X', new RecipeChoice.ExactChoice(itemGreenDye));
        Bukkit.addRecipe(wbe);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("wbe")) {
            if (sender instanceof Player) {
                if (sender.isOp()) {
                    if (args.length >= 1) {
                        if (args[0].equals("help")) {
                            sender.sendMessage(ChatColor.GREEN + "----WorldBorderExpansion----");
                            sender.sendMessage(ChatColor.GREEN + "/wbe help - pomoc");
                            sender.sendMessage(ChatColor.GREEN + "/wbe expand <kratki> - o ile się poszerza granica świata");
                            sender.sendMessage(ChatColor.GREEN + "----WorldBorderExpansion----");
                            return true;
                        } else if (args[0].equals("expand")) {
                            if (args.length >= 1) {
                                int expansion = Integer.parseInt(args[1]);

                                // Wczytaj istniejący plik konfiguracyjny
                                reloadConfig();
                                FileConfiguration config = getConfig();

                                // Zmodyfikuj wartość klucza "expansion"
                                config.set("expansion", expansion);

                                // Zapisz zmieniony plik konfiguracyjny
                                saveConfig();

                                sender.sendMessage(ChatColor.GREEN + "Ustawiono rozszerzanie granicy świata o " + config.get("expansion") + " kratek!");
                                return true;
                            }
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "----WorldBorderExpansion----");
                            sender.sendMessage(ChatColor.GREEN + "/wbe help - pomoc");
                            sender.sendMessage(ChatColor.GREEN + "/wbe expand <kratki> - o ile się poszerza granica świata");
                            sender.sendMessage(ChatColor.GREEN + "----WorldBorderExpansion----");
                        }
                    }
                }
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction().toString().contains("RIGHT")){
            ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
            if (item.getType().equals(Material.HEART_OF_THE_SEA)&&item.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.AQUA + "Powiekszanie Bordera")){
                reloadConfig();
                FileConfiguration config = getConfig();
                int kratki = (int) Bukkit.getWorlds().get(0).getWorldBorder().getSize() + config.getInt("expansion");
                Bukkit.getServer().getWorld("world").getWorldBorder().setSize(kratki, config.getInt("expansion")/10);
                Bukkit.getServer().getWorld("world_nether").getWorldBorder().setSize(kratki, config.getInt("expansion")/10);
                Bukkit.getServer().getWorld("world_the_end").getWorldBorder().setSize(kratki, config.getInt("expansion")/10);
                Bukkit.broadcastMessage(ChatColor.GREEN+"Gracz "+e.getPlayer().getName()+" powiekszyl border!");
                item.setAmount(item.getAmount()-1);
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onMobKill(EntityDeathEvent e) {
        if (e.getEntity().getKiller() instanceof Player) {
            int randomNum = (int) (Math.random() * 101);
            if (randomNum >= 99) {
                ItemStack itemGreenDye = new ItemStack(Material.GREEN_DYE);
                ItemMeta itemGreenDyeMeta = itemGreenDye.getItemMeta();
                itemGreenDyeMeta.setDisplayName(ChatColor.BLUE + "Czesc przedmiotu do powiekszenia bordera");
                itemGreenDyeMeta.setLore(Collections.singletonList(ChatColor.GOLD + "Zbierz pozostałe 9 części, aby stworzyć przedmiot do powiększenia granicy świata"));
                itemGreenDyeMeta.addEnchant(Enchantment.DURABILITY, 10, true);
                itemGreenDyeMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                itemGreenDyeMeta.setCustomModelData(997);
                itemGreenDye.setItemMeta(itemGreenDyeMeta);
                e.getDrops().add(itemGreenDye);
            }
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e){
        if (e.getEntity().getType()== EntityType.PLAYER){
            ItemStack item =e.getItem().getItemStack();
            ItemMeta meta = item.getItemMeta();
            if (meta.getDisplayName().equals(ChatColor.BLUE + "Czesc przedmiotu do powiekszenia bordera")){
                Player p = (Player) e.getEntity();
                p.discoverRecipe(key);
            }
        }
    }
}