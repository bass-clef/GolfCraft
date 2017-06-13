package musaddict.golfcraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GcEntityListener
implements Listener
{
	Golfcraft plugin;

	GcEntityListener(Golfcraft plugin)
	{
		this.plugin = plugin;
	}

	ArrayList<Snowball> bounce1 = new ArrayList<Snowball>();

	public static HashMap<Player, Integer> bounce = new HashMap<Player, Integer>();
	public static HashMap<Snowball, Player> snowballShooters = new HashMap<Snowball, Player>();
	public static HashMap<Player, Boolean> signExists = new HashMap<Player, Boolean>();
	public static HashMap<Player, Location> signLocation = new HashMap<Player, Location>();
	public static HashMap<Player, Boolean> finishedHole = new HashMap<Player, Boolean>();
	public static HashMap<Player, Integer> score = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> blockType = new HashMap<Player, Integer>();

	@EventHandler(priority=EventPriority.HIGH)
	public void onProjectileHit(ProjectileHitEvent event)
	{
		onProjectileHit_Func(event);
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onEntityTarget(EntityTargetEvent event)
	{
		onEntityTarget_Func(event);
	}

	@EventHandler(priority=EventPriority.HIGH)
	public void onEntityShootBow(EntityShootBowEvent event)
	{
		onEntityShootBow_Func(event);
	}

	private void onEntityShootBow_Func(EntityShootBowEvent event)
	{
		if ((event.getEntity() instanceof Player)) {
			Player shooter = (Player)event.getEntity();

			if (snowballShooters.containsValue(shooter))
			{
				return;
			}
			String shooterName = shooter.getName();
			boolean isGolfer = true;

			if (!GcCommands.Golfing.containsKey(shooter)) {
				isGolfer = false;
			}
			else if (!((Boolean)GcCommands.Golfing.get(shooter)).booleanValue()) {
				isGolfer = false;
			}

			if (!signExists.containsKey(shooter)) {
				signExists.put(shooter, Boolean.valueOf(false));
			}
			if ((shooter.hasPermission("golf.play")) || (shooter.isOp()))
			{
				if (isGolfer) {
					if (!((Boolean)signExists.get(shooter)).booleanValue())
					{
						for(Player players : org.bukkit.Bukkit.getOnlinePlayers()){

							if ((GcCommands.playingHole.containsKey(players)) && 
									(((String)GcCommands.playingHole.get(players)).equals(GcCommands.playingHole.get(shooter))))
							{
								boolean Continue = false;

								if (finishedHole.containsKey(shooter)) {
									if (!((Boolean)finishedHole.get(shooter)).booleanValue()) {
										Continue = true;
									}
								}
								else {
									Continue = true;
									finishedHole.put(shooter, Boolean.valueOf(false));
								}

								if (Continue) {
									if (!GcClubs.Club.containsKey(shooter)) {
										GcClubs.Club.put(shooter, Integer.valueOf(0));
									}
									if (this.plugin.getConfig().getBoolean("enable-clubs")) {
										players.sendMessage(ChatColor.DARK_GREEN + shooterName + ChatColor.GRAY + " swung their " + GcClubs.getClubMessage2(shooter));
										if (!GcClubs.getEfficiencyMessage(shooter).isEmpty()) {
											shooter.sendMessage(GcClubs.getEfficiencyMessage(shooter));
										}
									} else {
										players.sendMessage(ChatColor.DARK_GREEN + shooterName + ChatColor.GRAY + " has launched the ball!");
									}
								}
							}
						}

						if (score.containsKey(shooter)) {
							score.put(shooter, Integer.valueOf(((Integer)score.get(shooter)).intValue() + 1));
						}
						else {
							score.put(shooter, Integer.valueOf(1));
						}

						GcClubs.Force.put(shooter, Float.valueOf(event.getForce()));

						Entity proj = event.getProjectile();
						World world = proj.getWorld();

						Snowball newProj = (Snowball)world.spawn(proj.getLocation(), Snowball.class);

						newProj.setShooter(shooter);
						newProj.setVelocity(proj.getVelocity());
						newProj.setBounce(true);

						if ((!this.plugin.getConfig().getBoolean("enable-clubs")) && (this.plugin.getConfig().getBoolean("enable-speed-variances"))) {
							Block block = shooter.getLocation().getBlock().getRelative(BlockFace.DOWN);
//							if (block.getType() == Material.SAND) {
							if (GcConfig.sameLandedBlock(GcConfig.LandedBlock.SAND_TRAP, block.getType(), block.getData())) {
								int chance = new Random().nextInt(GcConfig.sandChance());
								chance = GcConfig.sandBase() + chance;
								double calcChance = chance / 100.0D;
								chance = 100 - chance;
								newProj.setVelocity(newProj.getVelocity().multiply(calcChance));
								shooter.sendMessage(ChatColor.YELLOW + "The sand trap reduced your speed by " + chance + "%");
							}
//							if (shooter.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.GRASS) {
							if (GcConfig.sameLandedBlock(GcConfig.LandedBlock.ROUGH, block.getType(), block.getData())) {
								int chance = new Random().nextInt(GcConfig.roughChance());
								chance = GcConfig.roughBase() - chance;
								double calcChance = chance / 100.0D;
								chance = 100 - chance;
								newProj.setVelocity(newProj.getVelocity().multiply(calcChance));
								shooter.sendMessage(ChatColor.GRAY + "The rough reduced your speed by " + ChatColor.YELLOW + chance + "%");
							}
//							if (shooter.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.WOOD) {
							if (GcConfig.sameLandedBlock(GcConfig.LandedBlock.DRIVER, block.getType(), block.getData())) {
								int chance = new Random().nextInt(GcConfig.teeingGroundChance());
								chance = GcConfig.teeingGroundBase() + chance;
								double calcChance = chance / 100.0D;
								chance -= 100;
								newProj.setVelocity(newProj.getVelocity().multiply(calcChance));
								shooter.sendMessage(ChatColor.GRAY + "Your driver increased your speed by " + ChatColor.YELLOW + chance + "%");
							}
						}
						else {
							if (!GcClubs.Club.containsKey(shooter)) {
								GcClubs.Club.put(shooter, Integer.valueOf(0));
							}
							Float force = Float.valueOf(event.getForce());
							Float dirX = Float.valueOf((float)((0.0D - Math.sin(shooter.getLocation().getYaw() / 180.0F * 3.141592653589793D) * 3.0D) * force.floatValue()));
							Float dirZ = Float.valueOf((float)(Math.cos(shooter.getLocation().getYaw() / 180.0F * 3.141592653589793D) * 3.0D * force.floatValue()));
							newProj.setVelocity(newProj.getVelocity().setX(dirX.floatValue()));
							newProj.setVelocity(newProj.getVelocity().setZ(dirZ.floatValue()));
							if (!((Integer)GcClubs.Club.get(shooter)).equals(Integer.valueOf(11))) {
								newProj.setVelocity(newProj.getVelocity().setY(GcClubs.getClubPitch(shooter).floatValue()));
							}
							newProj.setVelocity(newProj.getVelocity().multiply(GcClubs.getClubSpeed(shooter).doubleValue() * 0.475D * (this.plugin.getConfig().getInt("ball-power-percentage") / 100.0D)));
						}

						snowballShooters.put(newProj, shooter);
						this.bounce1.add(newProj);

						event.setProjectile(newProj);
					}
					else
					{
						shooter.sendMessage(ChatColor.RED + "You must destroy your sign to register a hit.");
					}
				}
			}
		}
	}

	private Vector getReflectedVector(Projectile oldProjectile, Vector oldVelocity)
	{
		Vector newVector = oldVelocity.clone();

		newVector.setY(-newVector.getY());

		Vector normalized = oldVelocity.normalize();

		double x = normalized.getX();double z = normalized.getZ();

		Block loc = oldProjectile.getLocation().getBlock();

		if ((x > 0.0D) && (loc.getRelative(BlockFace.SOUTH).getType() != Material.AIR)) {
			newVector.setX(-newVector.getX());
		} else if ((x < 0.0D) && (loc.getRelative(BlockFace.NORTH).getType() != Material.AIR)) {
			newVector.setX(-newVector.getX());
		}
		if ((z > 0.0D) && (loc.getRelative(BlockFace.WEST).getType() != Material.AIR)) {
			newVector.setZ(-newVector.getZ());
		} else if ((z < 0.0D) && (loc.getRelative(BlockFace.EAST).getType() != Material.AIR)) {
			newVector.setZ(-newVector.getZ());
		}
		return newVector;
	}

	private Snowball makeNewBall(Projectile oldProjectile, Vector newVector)
	{
		World world = oldProjectile.getWorld();

		Snowball ball = (Snowball)world.spawn(oldProjectile.getLocation(), Snowball.class);

		ball.setVelocity(newVector);
		ball.setBounce(true);

		return ball;
	}






	private void onProjectileHit_Func(ProjectileHitEvent event)
	{
		Player shooter = null;

		if ((event.getEntity() instanceof Snowball)) {
			Snowball proj = (Snowball)event.getEntity();
			Snowball newBall = null;
			Integer bounceInt = Integer.valueOf(this.plugin.getConfig().getInt("number-of-bounces"));

			if ((this.bounce1.size() > 0) && (this.bounce1.contains(proj))) {
				this.bounce1.remove(proj);
				if (!bounce.containsKey(shooter)) {
					bounce.put(shooter, Integer.valueOf(0));
				}
				Integer bounceMap = (Integer)bounce.get(shooter);
				if (bounceMap.intValue() <= bounceInt.intValue()) {
					newBall = makeNewBall(proj, getReflectedVector(proj, proj.getVelocity().multiply(0.5D)));
					this.bounce1.add(newBall);
					bounce.put(shooter, Integer.valueOf(bounceMap.intValue() + 1));
				}
				else {
					bounce.remove(shooter);
				}
			}

			if (snowballShooters.containsKey(proj)) {
				shooter = (Player)snowballShooters.get(proj);
				snowballShooters.remove(proj);

				if (newBall != null) {
					Block block = newBall.getLocation().getBlock().getRelative(BlockFace.DOWN); 
//					if (block.getType() == Material.SAND) {
					if (GcConfig.sameLandedBlock(GcConfig.LandedBlock.SAND_TRAP, block.getType(), block.getData())) {
						newBall.setVelocity(newBall.getVelocity().multiply(0.05D));
					}
					if (newBall.getLocation().getBlock().getType() == Material.WATER) {
						newBall.setVelocity(newBall.getVelocity().multiply(0.2D));
					}
//					if (newBall.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.LEAVES) {
					if (GcConfig.sameLandedBlock(GcConfig.LandedBlock.OB, block.getType(), block.getData())) {
						newBall.setVelocity(newBall.getVelocity().multiply(0.1D));
					}
					if (newBall.getLocation().getBlock().getType() == Material.LAVA) {
						newBall.setVelocity(newBall.getVelocity().multiply(0.01D));
					}
//					if (newBall.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.GRASS) {
					if (GcConfig.sameLandedBlock(GcConfig.LandedBlock.FAIRWAY,  block.getType(), block.getData())) {
						newBall.setVelocity(newBall.getVelocity().multiply(0.7D));
					}
					snowballShooters.put(newBall, shooter);

					return;
				}
			}
		}


		if (shooter == null) {
			return;
		}
		boolean hazard = false;
		boolean createSign = false;
		boolean inHole = false;
		Block block = event.getEntity().getLocation().getBlock().getRelative(BlockFace.DOWN);
		String playersMessage = null;
		String scoreString = null;
		String shooterName = shooter.getName();






		while ((block.getType() == Material.AIR) || (block.getType() == Material.FENCE)) {
			block = block.getRelative(BlockFace.DOWN);
		}


//		if (block.getType() != Material.GLOWSTONE) {
		if (false == GcConfig.sameLandedBlock(GcConfig.LandedBlock.HOLE, block.getType(), block.getData())) {
			while ((block.getType() == Material.FENCE) || (block.getType() == Material.FENCE)) {
				block = block.getRelative(BlockFace.UP);
			}
		}



		if (!GcCommands.Golfing.containsKey(shooter))
		{
			return;
		}
		if (!((Boolean)GcCommands.Golfing.get(shooter)).booleanValue())
		{
			return;
		}

		boolean Continue = false;

		if (finishedHole.containsKey(shooter)) {
			if (!((Boolean)finishedHole.get(shooter)).booleanValue()) {
				Continue = true;
			}
		}
		else {
			Continue = true;
			finishedHole.put(shooter, Boolean.valueOf(false)); }
		int parA;
		int scoreFinal;
		if (Continue) {
			int dist = (int)event.getEntity().getLocation().distance(shooter.getLocation());

			if ((block.getRelative(BlockFace.UP).getType() == Material.WATER) || (block.getRelative(BlockFace.UP).getType() == Material.STATIONARY_WATER) || (block.getType() == Material.WATER) || (block.getType() == Material.STATIONARY_WATER)) {
				playersMessage = ChatColor.DARK_GREEN + shooterName + ChatColor.RED + " just landed in a water hazard!";
				hazard = true;
			}
			if ((block.getRelative(BlockFace.UP).getType() == Material.LAVA) || (block.getRelative(BlockFace.UP).getType() == Material.STATIONARY_LAVA) || (block.getType() == Material.LAVA) || (block.getType() == Material.STATIONARY_LAVA)) {
				playersMessage = ChatColor.DARK_GREEN + shooterName + ChatColor.DARK_RED + " just landed in a lava hazard!";
				hazard = true;
			}
			if (!hazard) {
				//        if (block.getType() == Material.SAND) {
				if (GcConfig.sameLandedBlock(GcConfig.LandedBlock.SAND_TRAP, block.getType(), block.getData())) {
					playersMessage = ChatColor.DARK_GREEN + shooterName + ChatColor.YELLOW + " just landed in a sand trap... " + ChatColor.GRAY + "(" + ChatColor.AQUA + dist + "m" + ChatColor.GRAY + ")";
				}
				//    	  if (block.getType() == Material.GRASS) {
				if (GcConfig.sameLandedBlock(GcConfig.LandedBlock.ROUGH, block.getType(), block.getData())) {
					playersMessage = ChatColor.DARK_GREEN + shooterName + ChatColor.GRAY + " just landed in the rough... " + ChatColor.GRAY + "(" + ChatColor.AQUA + dist + "m" + ChatColor.GRAY + ")";
				}
				//    	  if (block.getType() == Material.WOOL) && (block.getData() == 13) {
				if (GcConfig.sameLandedBlock(GcConfig.LandedBlock.FAIRWAY,  block.getType(), block.getData())) {
					playersMessage = ChatColor.DARK_GREEN + shooterName + " just landed in the fairway! " + ChatColor.GRAY + "(" + ChatColor.AQUA + dist + "m" + ChatColor.GRAY + ")";
				}
				//    	  if (block.getType() == Material.WOOL) && (block.getData() == 5) {
				if (GcConfig.sameLandedBlock(GcConfig.LandedBlock.GREEN, block.getType(), block.getData())) {
					playersMessage = ChatColor.DARK_GREEN + shooterName + ChatColor.GREEN + " just landed on the green. " + ChatColor.GRAY + "(" + ChatColor.AQUA + dist + "m" + ChatColor.GRAY + ")";
				}
			}
			//      if ((block.getType() == Material.GLOWSTONE) && (!hazard))
			if (GcConfig.sameLandedBlock(GcConfig.LandedBlock.HOLE, block.getType(), block.getData())
					&& (!hazard))
			{
				if (GcCommands.playingHole.containsKey(shooter)) {
					GcHole existingHole = GcFiles.getHole(shooter.getWorld().getName(), (String)GcCommands.playingHole.get(shooter));
					int scoreA = ((Integer)score.get(shooter)).intValue();
					parA = existingHole.par;
					scoreFinal = scoreA - parA;

					if (scoreA == 1) {
						scoreString = ChatColor.GOLD + "----" + shooter.getName() + " GOT A HOLE IN ONE----";
					}
					else {
						if (scoreFinal <= -3) {
							scoreString = ChatColor.GRAY + "Score: " + scoreA + " (" + ChatColor.GOLD + "Double Eagle" + ChatColor.GRAY + ")";
						}
						if (scoreFinal == -2) {
							scoreString = ChatColor.GRAY + "Score: " + scoreA + " (" + ChatColor.YELLOW + "Eagle" + ChatColor.GRAY + ")";
						}
						if (scoreFinal == -1) {
							scoreString = ChatColor.GRAY + "Score: " + scoreA + " (" + ChatColor.GREEN + "Birdie" + ChatColor.GRAY + ")";
						}
						if (scoreFinal == 0) {
							scoreString = ChatColor.GRAY + "Score: " + scoreA + " (" + ChatColor.AQUA + "Par" + ChatColor.GRAY + ")";
						}
						if (scoreFinal == 1) {
							scoreString = ChatColor.GRAY + "Score: " + scoreA + " (" + ChatColor.YELLOW + "Bogey" + ChatColor.GRAY + ")";
						}
						if (scoreFinal == 2) {
							scoreString = ChatColor.GRAY + "Score: " + scoreA + " (" + ChatColor.RED + "Double Bogey" + ChatColor.GRAY + ")";
						}
						if (scoreFinal >= 3) {
							scoreString = ChatColor.GRAY + "Score: " + scoreA + " (" + ChatColor.DARK_RED + "Triple Bogey" + ChatColor.GRAY + ")";
						}
					}

					playersMessage = ChatColor.DARK_GREEN + shooterName + ChatColor.GREEN + " just landed in the" + ChatColor.GOLD + " cup!";
					shooter.sendMessage(ChatColor.GREEN + "You have finished this hole! Start a new hole?");
					shooter.sendMessage(ChatColor.GRAY + "/golf play [hole]");
					finishedHole.put(shooter, Boolean.valueOf(true));
					inHole = true;
				}
			}

//			if ((block.getType() == Material.LEAVES) && (!hazard)) {
			if ((!hazard) && GcConfig.sameLandedBlock(GcConfig.LandedBlock.OB, block.getType(), block.getData())) {
				playersMessage = ChatColor.DARK_GREEN + shooterName + ChatColor.RED + " just landed in a birds nest";
				hazard = true;
			}

			for(Player players : org.bukkit.Bukkit.getOnlinePlayers()){
				if ((GcCommands.playingHole.containsKey(players)) && 
						(((String)GcCommands.playingHole.get(players)).equals(GcCommands.playingHole.get(shooter)))) {
					if ((playersMessage == null) &&
							(false == GcConfig.sameLandedBlock(GcConfig.LandedBlock.HOLE, block.getType(), block.getData()))) {
						playersMessage = ChatColor.GRAY + "The ball has landed on " + block.getType().toString().toLowerCase().replace("_", " ") + ". " + ChatColor.GRAY + "(" + ChatColor.AQUA + dist + "m" + ChatColor.GRAY + ")";
						if (this.plugin.getConfig().getBoolean("unknow-blocks-are-hazards"))
							hazard = true;
					}
					players.sendMessage(playersMessage);
					if ((inHole) && (scoreString != null)) {
						players.sendMessage(scoreString);
					}
					createSign = true;
				}
			}
		}



		if (inHole) {
			signExists.remove(shooter);
			signLocation.remove(shooter);
			GcBlockListener.signLocation.remove(shooter);
		}


		if (hazard) {
			while (shooter.getInventory().contains(Material.ARROW)) {
				shooter.getInventory().remove(Material.ARROW);
			}
			ItemStack arrow = new ItemStack(Material.ARROW);
			arrow.setAmount(1);
			shooter.getInventory().addItem(new ItemStack[] { arrow });
			while (shooter.getInventory().contains(Material.BOW)) {
				shooter.getInventory().remove(Material.BOW);
			}
			ItemStack bow = new ItemStack(Material.BOW);
			arrow.setAmount(1);
			shooter.getInventory().addItem(new ItemStack[] { bow });
			score.put(shooter, Integer.valueOf(((Integer)score.get(shooter)).intValue() + 1));
		}

		if ((createSign) && (!hazard) && (!inHole)) {
			Block up = block.getRelative(BlockFace.UP);
			blockType.put(shooter, Integer.valueOf(up.getType().getId()));
			up.setType(Material.SIGN_POST);
			up.getState().update(true);
			signExists.put(shooter, Boolean.valueOf(true));

			if ((up.getState() instanceof Sign)) {
				Sign sign = (Sign)up.getState();
				sign.setLine(0, ChatColor.WHITE + "   [" + ChatColor.DARK_GREEN + "Golf" + ChatColor.WHITE + "]");
				sign.setLine(1, ChatColor.DARK_GRAY + shooterName);
				sign.update(true);
				up.getState().update(true);
				signLocation.put(shooter, sign.getLocation());

			}
			else
			{
				for(Player players : org.bukkit.Bukkit.getOnlinePlayers()){
					players.sendMessage(ChatColor.RED + "Sign failed to generate!");
				}
			}
		}
	}





	private void onEntityTarget_Func(EntityTargetEvent event)
	{
		if (this.plugin.getConfig().getBoolean("kill-mobs-on-course")) {
			Entity mob = event.getEntity();
			int targetUUID = 0;
			try {
				targetUUID = event.getTarget().getEntityId();
			}
			catch (NullPointerException e) {
				return;
			}
			for(Player players : org.bukkit.Bukkit.getOnlinePlayers()){
				int player = players.getEntityId();
				if ((targetUUID == player) && 
						(GcCommands.Golfing.containsKey(players)) && 
						(((Boolean)GcCommands.Golfing.get(players)).booleanValue())) {
					Block block = players.getLocation().getBlock().getRelative(BlockFace.DOWN);
					Block block2 = block.getRelative(BlockFace.DOWN);
					Block block3 = block2.getRelative(BlockFace.DOWN);

					if ((!mob.isDead()) && (
							(GcConfig.sameLandedBlock(GcConfig.LandedBlock.FAIRWAY,  block.getType(), block.getData()) || GcConfig.sameLandedBlock(GcConfig.LandedBlock.GREEN,  block.getType(), block.getData()))
							|| (GcConfig.sameLandedBlock(GcConfig.LandedBlock.FAIRWAY,  block2.getType(), block2.getData()) || GcConfig.sameLandedBlock(GcConfig.LandedBlock.GREEN,  block2.getType(), block2.getData()))
							|| (GcConfig.sameLandedBlock(GcConfig.LandedBlock.FAIRWAY,  block3.getType(), block3.getData()) || GcConfig.sameLandedBlock(GcConfig.LandedBlock.GREEN,  block3.getType(), block3.getData()))
							)) {
						//				  if (((block.getType() == Material.WOOL) && ((block.getData() == 13) || (block.getData() == 5)) && (!mob.isDead())) 
						//						  || ((block2.getType() == Material.WOOL) && ((block2.getData() == 13) || (block2.getData() == 5)) && (!mob.isDead()))
						//						  || ((block3.getType() == Material.WOOL) && ((block3.getData() == 13) || (block3.getData() == 5)) && (!mob.isDead()))) {

						mob.remove();
					}
				}
			}
		}
	}
}
