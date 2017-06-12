package musaddict.golfcraft;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class GcBlockListener
  implements Listener
{
  public static HashMap<Player, Location> signLocation = new HashMap();
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onBlockDamage(BlockDamageEvent event)
  {
    onBlockDamage_Func(event);
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  public void onBlockBreak(BlockBreakEvent event)
  {
    onBlockBreak_Func(event);
  }
  
  private void onBlockBreak_Func(BlockBreakEvent event)
  {
    Block block = event.getBlock();
    Player player = event.getPlayer();
    
    if ((block.getState() instanceof Sign)) {
      Sign sign = (Sign)block.getState();
      String[] lines = sign.getLines();
      
      if (lines[0].equals(ChatColor.WHITE + "   [" + ChatColor.DARK_GREEN + "Golf" + ChatColor.WHITE + "]")) {
        if (lines[1].equals(ChatColor.DARK_GRAY + player.getName())) {
          if ((GcCommands.Golfing.containsKey(player)) && 
            (((Boolean)GcCommands.Golfing.get(player)).booleanValue())) {
            while (player.getInventory().contains(Material.ARROW)) {
              player.getInventory().remove(Material.ARROW);
            }
            ItemStack arrow = new ItemStack(Material.ARROW);
            arrow.setAmount(1);
            player.getInventory().addItem(new ItemStack[] { arrow });
            while (player.getInventory().contains(Material.BOW)) {
              player.getInventory().remove(Material.BOW);
            }
            ItemStack bow = new ItemStack(Material.BOW);
            arrow.setAmount(1);
            player.getInventory().addItem(new ItemStack[] { bow });
            GcEntityListener.signExists.put(player, Boolean.valueOf(false));
            signLocation.put(player, block.getLocation());
            
            event.setCancelled(true);
            block.setTypeId(((Integer)GcEntityListener.blockType.get(player)).intValue());
            block.getState().update(false);
            
            player.sendMessage(ChatColor.GRAY + "You may now continue golfing.");
          }
          
        }
        else if (lines[1] != "Start:") {
          if ((player.hasPermission("golf.ref")) || (player.isOp())) {
            String signPlayer = ChatColor.stripColor(lines[1]);
            player.sendMessage(ChatColor.GRAY + "You have removed " + ChatColor.GREEN + signPlayer + ChatColor.GRAY + "'" + ChatColor.GREEN + "s " + ChatColor.GRAY + "sign.");
            
            for(Player players : org.bukkit.Bukkit.getOnlinePlayers()){
              if (players.getName().equals(signPlayer)) {
                GcEntityListener.signExists.put(players, Boolean.valueOf(false));
                
                event.setCancelled(true);
                block.setTypeId(((Integer)GcEntityListener.blockType.get(player)).intValue());
                block.getState().update(false);
              }
            }
          }
          else {
            event.setCancelled(true);
            block.getState().update(true);
          }
        }
      }
    }
  }
  
  private void onBlockDamage_Func(BlockDamageEvent event)
  {
    Block block = event.getBlock();
    Player player = event.getPlayer();
    
    if ((block.getState() instanceof Sign)) {
      Sign sign = (Sign)block.getState();
      String[] lines = sign.getLines();
      
      if (lines[0].equals(ChatColor.WHITE + "   [" + ChatColor.DARK_GREEN + "Golf" + ChatColor.WHITE + "]")) {
        if (lines[1].equals(ChatColor.DARK_GRAY + player.getName())) {
          if ((GcCommands.Golfing.containsKey(player)) && 
            (((Boolean)GcCommands.Golfing.get(player)).booleanValue())) {
            while (player.getInventory().contains(Material.ARROW)) {
              player.getInventory().remove(Material.ARROW);
            }
            ItemStack arrow = new ItemStack(Material.ARROW);
            arrow.setAmount(1);
            player.getInventory().addItem(new ItemStack[] { arrow });
            while (player.getInventory().contains(Material.BOW)) {
              player.getInventory().remove(Material.BOW);
            }
            ItemStack bow = new ItemStack(Material.BOW);
            arrow.setAmount(1);
            player.getInventory().addItem(new ItemStack[] { bow });
            GcEntityListener.signExists.put(player, Boolean.valueOf(false));
            signLocation.put(player, block.getLocation());
            
            block.setTypeId(((Integer)GcEntityListener.blockType.get(player)).intValue());
            block.getState().update(false);
            
            player.sendMessage(ChatColor.GRAY + "You may now continue golfing.");
          }
          
        }
        else if (lines[1] != "Start:") {
          if ((player.hasPermission("golf.ref")) || (player.isOp())) {
            String signPlayer = ChatColor.stripColor(lines[1]);
            player.sendMessage(ChatColor.GRAY + "You have removed " + ChatColor.GREEN + signPlayer + ChatColor.GRAY + "'" + ChatColor.GREEN + "s " + ChatColor.GRAY + "sign.");
            
            for(Player players : org.bukkit.Bukkit.getOnlinePlayers()){
              if (players.getName().equals(signPlayer)) {
                GcEntityListener.signExists.put(players, Boolean.valueOf(false));
                
                block.setTypeId(((Integer)GcEntityListener.blockType.get(player)).intValue());
                block.getState().update(false);
              }
            }
          }
          else {
            event.setCancelled(true);
            block.getState().update(true);
          }
        }
      }
    }
  }
}
