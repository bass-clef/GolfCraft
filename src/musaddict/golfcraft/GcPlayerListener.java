package musaddict.golfcraft;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class GcPlayerListener implements Listener
{
  Golfcraft plugin;
  
  GcPlayerListener(Golfcraft plugin)
  {
    this.plugin = plugin;
  }
  
  public void onPlayerMove(PlayerMoveEvent event)
  {
    onPlayerMove_Func(event);
  }
  
  public void onPlayerInteract(PlayerInteractEvent event)
  {
    onPlayerInteract_Func(event);
  }
  

  @EventHandler(priority=EventPriority.HIGH)
  private void onPlayerInteract_Func(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    if ((this.plugin.getConfig().getBoolean("enable-clubs")) && 
      (GcCommands.Golfing.containsKey(player)) && 
      (((Boolean)GcCommands.Golfing.get(player)).booleanValue()) && 
      (event.getAction() == Action.LEFT_CLICK_AIR) && (player.getItemInHand().getType() == Material.BOW)) {
      if (GcClubs.Club.containsKey(player)) {
        if (((Integer)GcClubs.Club.get(player)).intValue() < 11) {
          GcClubs.Club.put(player, Integer.valueOf(((Integer)GcClubs.Club.get(player)).intValue() + 1));
        }
        else {
          GcClubs.Club.put(player, Integer.valueOf(0));
        }
      }
      else {
        GcClubs.Club.put(player, Integer.valueOf(0));
      }
      player.sendMessage(GcClubs.getClubMessage(player));
    }
    





    if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && (player.getGameMode() == GameMode.ADVENTURE)) {
      Block block = event.getClickedBlock();
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
              GcBlockListener.signLocation.put(player, block.getLocation());
              
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
  }
  
  @EventHandler(priority=EventPriority.HIGH)
  private void onPlayerMove_Func(PlayerMoveEvent event)
  {
    Player player = event.getPlayer();
    Location playerLocation = null;
    Location signLocation = null;
    
    if ((GcEntityListener.signExists.containsKey(player)) && 
      (!((Boolean)GcEntityListener.signExists.get(player)).booleanValue())) {
      playerLocation = player.getLocation();
    }
    

    if (playerLocation != null) {
      signLocation = (Location)GcBlockListener.signLocation.get(player);
      if ((signLocation != null) && 
        (playerLocation.distance(signLocation) > 2.0D)) {
        player.teleport(signLocation);
        player.sendMessage(ChatColor.RED + "You cannot move until your launched ball has landed.");
      }
    }
  }
}
