package musaddict.golfcraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginDescriptionFile;

public class GcCommands
implements CommandExecutor
{
	public static HashMap<Player, Boolean> Golfing = new HashMap();
	public static HashMap<Player, String> playingHole = new HashMap();

	public boolean onCommand(CommandSender sender, Command command, String label, String[] arg)
	{
		Player player = (Player)sender;

		if (arg.length == 0) {
			boolean golfing = false;

			if ((player.hasPermission("golf.spectate")) || (player.isOp()) || (player.hasPermission("golf.play"))) {
				if (Golfing.containsKey(player)) {
					if (((Boolean)Golfing.get(player)).booleanValue()) {
						Golfing.put(player, Boolean.valueOf(false));
						GcEntityListener.finishedHole.put(player, Boolean.valueOf(true));
						player.sendMessage(ChatColor.GRAY + "You have been removed from all golfing activity.");
						if (GcEntityListener.signLocation.containsKey(player)) {
							Block block = ((Location)GcEntityListener.signLocation.get(player)).getBlock();
							block.setType(Material.AIR);
							GcEntityListener.signLocation.remove(player);
						}
						if (GcEntityListener.signExists.containsKey(player)) {
							GcEntityListener.signExists.remove(player);
						}
						if (GcBlockListener.signLocation.containsKey(player)) {
							Block block = ((Location)GcBlockListener.signLocation.get(player)).getBlock();
							block.setType(Material.AIR);
							GcBlockListener.signLocation.remove(player);
						}
						if (GcEntityListener.score.containsKey(player)) {
							GcEntityListener.score.remove(player);
						}
					}
					else {
						Golfing.put(player, Boolean.valueOf(true));
						player.sendMessage(ChatColor.GOLD + "You are now golfing!");
						player.sendMessage(ChatColor.GRAY + "You have not chosen a hole. You can do so with:");
						player.sendMessage(ChatColor.GRAY + "/golf play [hole]");
						golfing = true;
					}
				}
				else {
					Golfing.put(player, Boolean.valueOf(true));
					player.sendMessage(ChatColor.GOLD + "You are now golfing!");
					player.sendMessage(ChatColor.GRAY + "You have not chosen a hole. You can do so with:");
					player.sendMessage(ChatColor.GRAY + "/golf play [hole]");
					golfing = true;
				}

				label720:{
					if (golfing) if (!player.hasPermission("golf.play")) { if (!player.isOp()) {}
					} else { while (player.getInventory().contains(Material.ARROW)) {
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
					break label720;
					}
					while (player.getInventory().contains(Material.ARROW)) { ItemStack arrow;
					ItemStack bow; player.getInventory().remove(Material.ARROW);
					}
					while (player.getInventory().contains(Material.BOW)) {
						player.getInventory().remove(Material.BOW);
					}
				}
				return true;
			}

			player.sendMessage(ChatColor.DARK_RED + "You don't have permission to spectate golf!");
			return true;
		}


		if (arg.length > 0) {
			String Command = arg[0];

			if (Command.equalsIgnoreCase("help")) {
				if ((arg.length == 1) || ((arg.length == 2) && (arg[1].equals("1")))) {
					player.sendMessage(ChatColor.GRAY + "Golfcraft " + ChatColor.GREEN + "v" + Golfcraft.info.getVersion() + ChatColor.GRAY + " Page 1/4");
					player.sendMessage(ChatColor.GRAY + "____________________________________");
					player.sendMessage(ChatColor.GREEN + "/golf" + ChatColor.GRAY + " - enters/exits golf mode");
					player.sendMessage(ChatColor.GREEN + "/golf help (page)" + ChatColor.GRAY + " - what you just typed");
					player.sendMessage(ChatColor.GREEN + "/golf version" + ChatColor.GRAY + " - checks for updates");
					player.sendMessage(ChatColor.GREEN + "/golf tp (player)" + ChatColor.GRAY + " - TP's you or others to sign");
					player.sendMessage(ChatColor.GREEN + "/golf list" + ChatColor.GRAY + " - show holes");
					player.sendMessage(ChatColor.GREEN + "/golf create [name] [par]" + ChatColor.GRAY + " - creates a new hole and sets par");
					player.sendMessage(ChatColor.GREEN + "/golf delete [name]" + ChatColor.GRAY + " - delete hole");
					player.sendMessage(ChatColor.GREEN + "/golf play [name]" + ChatColor.GRAY + " - TP's you to that hole to begin new round");
					player.sendMessage("");
					player.sendMessage("");
					return true;
				}
				if (arg[1].equals("2")) {
					player.sendMessage(ChatColor.GRAY + "Golfcraft " + ChatColor.GREEN + "v" + Golfcraft.info.getVersion() + ChatColor.GRAY + " Page 2/4");
					player.sendMessage(ChatColor.GRAY + "____________________________________");
					player.sendMessage(ChatColor.GRAY + "Green Wool - " + ChatColor.DARK_GREEN + "Fairway" + ChatColor.GRAY + " (main material)");
					player.sendMessage(ChatColor.GRAY + "Lime Wool - " + ChatColor.GREEN + "The Green" + ChatColor.GRAY + " (putting ground)");
					player.sendMessage(ChatColor.GRAY + "Glowstone - " + ChatColor.GREEN + "The " + ChatColor.GOLD + "Cup");
					player.sendMessage(ChatColor.GRAY + "Sand - " + ChatColor.YELLOW + "Sandtrap");
					player.sendMessage(ChatColor.GRAY + "Grass - Rough  (border of course)");
					player.sendMessage(ChatColor.GRAY + "Wood - Teeing Ground (Start of course)");
					player.sendMessage("");
					player.sendMessage("");
					return true;
				}
				if (arg[1].equals("3")) {
					player.sendMessage(ChatColor.GRAY + "Golfcraft " + ChatColor.GREEN + "v" + Golfcraft.info.getVersion() + ChatColor.GRAY + " Page 3/4");
					player.sendMessage(ChatColor.GRAY + "____________________________________");
					player.sendMessage(ChatColor.RED + "Hazards:");
					player.sendMessage(ChatColor.GRAY + " - " + ChatColor.RED + "Water");
					player.sendMessage(ChatColor.GRAY + " - " + ChatColor.RED + "Trees");
					player.sendMessage(ChatColor.GRAY + " - " + ChatColor.RED + "Lava");
					player.sendMessage("");
					player.sendMessage("");
					player.sendMessage("");
					player.sendMessage("");
					return true;
				}

				if (arg[1].equals("4")) {
					player.sendMessage(ChatColor.GRAY + "Golfcraft " + ChatColor.GREEN + "v" + Golfcraft.info.getVersion() + ChatColor.GRAY + " Page 4/4");
					player.sendMessage(ChatColor.GRAY + "____________________________________");
					player.sendMessage(ChatColor.GRAY + "You can create new holes where you're standing.");
					player.sendMessage(ChatColor.GRAY + "Best to create holes while on your teeing ground (wood).");
					player.sendMessage(ChatColor.GRAY + "Entering golf mode will force your inventory to have 1 arrow.");
					player.sendMessage(ChatColor.GRAY + "Par for each hole is set on creation. You scores will be");
					player.sendMessage(ChatColor.GRAY + "based on that number. Scores dont carry over to new holes.");
					player.sendMessage("");
					player.sendMessage(ChatColor.GRAY + "You will get your arrow back once you destroy your sign, or");
					player.sendMessage(ChatColor.GRAY + "if you land in a hazard.");
					return true;
				}
			} else {
				String inputLine;
				if (Command.equalsIgnoreCase("version")) {
					sender.sendMessage(ChatColor.GRAY + "You are running " + ChatColor.GREEN + "Golfcraft " + ChatColor.GRAY + Golfcraft.info.getVersion());
					try
					{
						URL version = new URL("http://dl.dropbox.com/u/25125155/GolfCraft/Version.txt");
						URLConnection dc = version.openConnection();
						BufferedReader in = new BufferedReader(new InputStreamReader(dc.getInputStream()));


						while ((inputLine = in.readLine()) != null) {
							//              String inputLine;
							if (inputLine.equals(Golfcraft.info.getVersion())) {
								sender.sendMessage(ChatColor.GRAY + "Golfcraft is up to date!");
							}
							else {
								sender.sendMessage(ChatColor.GRAY + "Golfcraft is Out of date! The latest version is:");
								sender.sendMessage(ChatColor.GREEN + inputLine);
								sender.sendMessage(ChatColor.GRAY + "You can download the latest version here:");
								sender.sendMessage("http://dev.bukkit.org/server-mods/golfcraft/files/");
							}
						}
						in.close();
					}
					catch (IOException e) {
						e.printStackTrace();
					}

					return true;
				}

				if (Command.equalsIgnoreCase("tp")) {
					if (arg.length == 1) {
						if ((player.hasPermission("golf.tp.sign")) || (player.isOp())) {
							if (GcEntityListener.signExists.containsKey(player)) {
								if (((Boolean)GcEntityListener.signExists.get(player)).booleanValue()) {
									if (GcEntityListener.signLocation.containsKey(player)) {
										player.teleport((Location)GcEntityListener.signLocation.get(player));
										player.sendMessage(ChatColor.GREEN + "You have been teleported to your sign.");
										return true;
									}
									player.sendMessage(ChatColor.GRAY + "You do not have a sign.");
									return true;
								}
								player.sendMessage(ChatColor.GRAY + "You do not have a sign.");
								return true;
							}
							player.sendMessage(ChatColor.GRAY + "You do not have a sign.");
							return true;
						}
						player.sendMessage(ChatColor.DARK_RED + "You don't have permission to teleport!");
						return true;
					}
					
					if ((player.hasPermission("golf.tp.sign.others")) || (player.isOp())) {
						String tpPlayer = arg[1];
						for(Player players : org.bukkit.Bukkit.getOnlinePlayers()){
							if ((players.getName().equals(tpPlayer)) && 
									(GcEntityListener.signExists.containsKey(players)) && 
									(((Boolean)GcEntityListener.signExists.get(players)).booleanValue()) && 
									(GcEntityListener.signLocation.containsKey(players))) {
								players.teleport((Location)GcEntityListener.signLocation.get(players));
								player.sendMessage(ChatColor.GRAY + "The player " + ChatColor.DARK_GREEN + arg[1] + ChatColor.GRAY + " has been teleported.");
								players.sendMessage(ChatColor.GREEN + "You have been teleported to your sign.");
								return true;
							}
						}

						player.sendMessage(ChatColor.RED + "The player " + ChatColor.DARK_GREEN + arg[1] + ChatColor.RED + " is not found.");
						return true;
					}

					player.sendMessage(ChatColor.DARK_RED + "You don't have permission to teleport others!");
					return true;
				}

				if (Command.equalsIgnoreCase("list")) {
					player.sendMessage(ChatColor.GREEN + "-- HoleList --");
					for(GcHole hole : GcFiles.getHoles()) {
						player.sendMessage(ChatColor.GREEN + hole.toString());
					}
					return true;
				}
				
				if (Command.equalsIgnoreCase("config")) {
					String configType = null, configPath = null, configValue = null;
					try {
						configType = arg[1];
					}
					catch (IndexOutOfBoundsException e) {
						player.sendMessage(ChatColor.GRAY + "Need to config path.");
						player.sendMessage(ChatColor.GRAY + "/golf config show [path]");
						player.sendMessage(ChatColor.GRAY + "/golf config change [path] [value]");
						player.sendMessage(ChatColor.GRAY + "/golf config save");
						player.sendMessage(ChatColor.GRAY + "/golf config list [path]");
						return true;
					}
					
					if (!configType.equals("save")) {
						try
						{
							configPath	= arg[2];
						}
						catch (IndexOutOfBoundsException e) {
							player.sendMessage("need config path. example for [hoge.foo.bar]");
							return true;
						}
						
						if (configType.equals("change")) {
							try
							{
								configValue	= arg[3];
							}
							catch (IndexOutOfBoundsException e) {
								player.sendMessage("need config value. if string value then add \" to both ends");
								return true;
							}
						}
					}
					
					if (configType.equals("save")) {
						if ((player.isOp()) || (player.hasPermission("golf.save"))) {
							GcConfig.saveConfig();
							player.sendMessage(ChatColor.GREEN + "saved.");
						} else {
							player.sendMessage(ChatColor.DARK_RED + "You do not have permission to use that command.");
						}
					} else if (configType.equals("list")) {
						player.sendMessage("-- config list [" + configPath + "] --");
						for(String key : GcConfig.getConfigKey(configPath)) {
							player.sendMessage(key);
						}
					} else if (configType.equals("show")) {
						player.sendMessage("" + ChatColor.GRAY + configPath + " = [" + ChatColor.AQUA + GcConfig.getConfig(configPath) + ChatColor.GRAY + "]");
					} else if (configType.equals("change")) {
						String prev = GcConfig.getConfig(configPath);
						if ((player.isOp()) || (player.hasPermission("golf.change"))) {
							if (GcConfig.setConfig(configPath, configValue)) {
								player.sendMessage("" + ChatColor.GREEN + "change success. "
									+ ChatColor.GRAY + "[" + ChatColor.RED + prev + ChatColor.GRAY + "] => "
									+ "[" + ChatColor.GREEN + configValue + "]");
							} else {
								player.sendMessage("" + ChatColor.RED + "don't changed.");
							}
						} else {
							player.sendMessage(ChatColor.DARK_RED + "You do not have permission to use that command.");
						}
					}
					
					return true;
				}
				
				if (Command.equalsIgnoreCase("delete")) {
					String holeName = null;
					try {
						holeName = arg[1];
					}
					catch (IndexOutOfBoundsException e) {
						player.sendMessage(ChatColor.GRAY + "Need to specify hole.");
						player.sendMessage(ChatColor.GRAY + "/golf delete [hole]");
						return true;
					}
					if (!holeName.equals(null)) {
						if ((player.isOp()) || (player.hasPermission("golf.delete"))) {
							if (GcFiles.holeExists(player, holeName))
							{
								GcFiles.delHole(player, holeName);
								GcFiles.save();
								player.sendMessage(ChatColor.GREEN + "You have deleted " + ChatColor.AQUA + holeName);
								return true;
							}
							player.sendMessage(ChatColor.RED + "You do not have deleted. not exist hole. ["+ holeName +"]");
							return true;
						}
						player.sendMessage(ChatColor.DARK_RED + "You do not have permission to use that command.");
						return true;
					}
					return true;
				}

				if (Command.equalsIgnoreCase("create")) {
					String holeName = null;
					String testString = null;
					try
					{
						holeName = arg[1];
						testString = arg[2];
					}
					catch (IndexOutOfBoundsException e) {
						player.sendMessage(ChatColor.GRAY + "Need to specify hole and par(#).");
						player.sendMessage(ChatColor.GRAY + "/golf create [hole] [par]");
						return true;
					}
					try
					{
						Integer.parseInt(testString);
					}
					catch (NumberFormatException e) {
						player.sendMessage("Par needs to be an integer");
						return true;
					}

					int parAmount = Integer.parseInt(testString);

					if (!holeName.equals(null)) {
						Block block = player.getLocation().getBlock();
						if ((player.isOp()) || (player.hasPermission("golf.create"))) {
							if (!GcFiles.holeExists(player, holeName, parAmount))
							{
								GcFiles.addHole(new GcHole(player.getWorld().getName(), holeName, parAmount, block));
								GcFiles.save();
								player.sendMessage(ChatColor.GREEN + "You have created " + ChatColor.AQUA + holeName + ": par " + parAmount);
								return true;
							}


							GcHole existingHole = GcFiles.getHole(player.getWorld().getName(), holeName);

							player.sendMessage(ChatColor.RED + "You have already created " + ChatColor.AQUA + holeName + ": par " + existingHole.par);

							return true;
						}


						player.sendMessage(ChatColor.DARK_RED + "You do not have permission to use that command.");
						return true;
					}


				}
				else if (Command.equals("play")) {
					String holeName = null;
					try
					{
						holeName = arg[1];
					}
					catch (IndexOutOfBoundsException e) {
						player.sendMessage(ChatColor.GRAY + "Need to specify hole.");
						player.sendMessage(ChatColor.GRAY + "/golf play [hole]");
						return true;
					}
					if (!holeName.equals(null)) {
						if ((player.isOp()) || (player.hasPermission("golf.tp.play"))) {
							GcHole existingHole = GcFiles.getHole(player.getWorld().getName(), holeName);
							try {
								GcFiles.holeExists(player, holeName);
							}
							catch (NullPointerException e) {
								player.sendMessage(ChatColor.GRAY + "That hole does not exist!");
								return true;
							}
							if (GcFiles.holeExists(player, holeName)) {
								if (GcEntityListener.signExists.containsKey(player)) {
									if ((((Boolean)GcEntityListener.signExists.get(player)).booleanValue()) && 
											(GcEntityListener.signLocation.containsKey(player))) {
										((Location)GcEntityListener.signLocation.get(player)).getBlock().setType(Material.AIR);
										GcEntityListener.signLocation.remove(player);
									}

									GcEntityListener.signExists.remove(player);
								}
								Location tpHoleLoc = GcFiles.getHole(player.getWorld().getName(), holeName).getBlock().getLocation();
								player.teleport(tpHoleLoc);

								if (Golfing.containsKey(player))
								{
									if (!((Boolean)Golfing.get(player)).booleanValue()) {
										Golfing.put(player, Boolean.valueOf(true));
										player.sendMessage(ChatColor.GOLD + "You are now golfing!");
									}
								}
								else {
									Golfing.put(player, Boolean.valueOf(true));
									player.sendMessage(ChatColor.GOLD + "You are now golfing!");
								}

								if (GcEntityListener.finishedHole.containsKey(player)) {
									if (((Boolean)GcEntityListener.finishedHole.get(player)).booleanValue()) {
										GcEntityListener.finishedHole.put(player, Boolean.valueOf(false));
									}
								}
								else {
									GcEntityListener.finishedHole.put(player, Boolean.valueOf(false));
								}



								for(Player players : org.bukkit.Bukkit.getOnlinePlayers()){
									if ((Golfing.containsKey(players)) && 
											(((Boolean)Golfing.get(players)).booleanValue()))
									{
										players.sendMessage(ChatColor.DARK_GREEN + player.getName() + ChatColor.GRAY + " has begun " + ChatColor.GOLD + holeName + " par: " + existingHole.par);
									}
								}



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
								GcEntityListener.score.put(player, Integer.valueOf(0));
								playingHole.put(player, holeName);
								return true;
							}

							player.sendMessage(ChatColor.AQUA + holeName + ChatColor.RED + " doesn't exist in world: " + player.getWorld().getName());
							return true;
						}


						player.sendMessage(ChatColor.DARK_RED + "You do not have permission to use that command.");
						return true;
					}
				}
			}
		}
		player.sendMessage(ChatColor.GRAY + "/golf help");
		return true;
	}
}
