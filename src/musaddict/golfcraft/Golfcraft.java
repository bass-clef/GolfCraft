package musaddict.golfcraft;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class Golfcraft extends org.bukkit.plugin.java.JavaPlugin
{
  public static PluginDescriptionFile info;
  static Logger logger = Logger.getLogger("Minecraft");
  
  public static final String mainDirectory = "plugins/Golfcraft";
  
  public void onEnable()
  {
    info = getDescription();
    logger = Logger.getLogger("Minecraft");
    
    Log(Level.INFO, "is enabled, version: " + info.getVersion());
    Log(Level.INFO, "written by [Musaddict]");
    
    new File("plugins/Golfcraft").mkdir();
    

    getConfig().options().copyDefaults(true);
    saveConfig();
    
    GcFiles.load();
    
    GcConfig.plugin = this;
    getCommand("golf").setExecutor(new GcCommands());
    getServer().getPluginManager().registerEvents(new GcSigns(this), this);
    getServer().getPluginManager().registerEvents(new GcPlayerListener(this), this);
    getServer().getPluginManager().registerEvents(new GcBlockListener(), this);
    getServer().getPluginManager().registerEvents(new GcEntityListener(this), this);
  }
  

  public void onDisable()
  {
    GcConfig.plugin = null;
    GcFiles.save();
    for(Player players : org.bukkit.Bukkit.getOnlinePlayers()){
    	if (GcEntityListener.signLocation.containsKey(players)) {
    		Block block = ((Location)GcEntityListener.signLocation.get(players)).getBlock();
    		block.setType(Material.AIR);
    		GcEntityListener.signLocation.remove(players);
    	}
    	if (GcEntityListener.signExists.containsKey(players)) {
    		GcEntityListener.signExists.remove(players);
    	}
    	if (GcBlockListener.signLocation.containsKey(players)) {
    		Block block = ((Location)GcBlockListener.signLocation.get(players)).getBlock();
    		block.setType(Material.AIR);
    		GcBlockListener.signLocation.remove(players);
    	}
    	if (GcEntityListener.score.containsKey(players)) {
    		GcEntityListener.score.remove(players);
    	}
    }
    Log(Level.INFO, "was successfully disabled.");
  }
  
  public static void Log(String message)
  {
    Log(Level.INFO, message);
  }
  
  public static void Log(Level logLevel, String message)
  {
    logger.log(logLevel, "[" + info.getName() + "] " + message);
  }
}
