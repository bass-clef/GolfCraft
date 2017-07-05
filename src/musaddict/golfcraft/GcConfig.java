package musaddict.golfcraft;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class GcConfig
{
	static Golfcraft plugin;

	public static int sandChance() {
		int Min = plugin.getConfig().getInt("sandtrap-min-percent-decrease");
		int Max = plugin.getConfig().getInt("sandtrap-max-percent-decrease");
		if (Max < 1)
			Max = 1;
		if (Min < 0)
			Min = 0;
		if (Min >= Max)
			Min = Max - 1;
		int Variation = Max - Min;
		return Variation;
	}

	public static int sandBase()
	{
		int Max = plugin.getConfig().getInt("sandtrap-max-percent-decrease");
		if (Max < 0)
			Max = 0;
		if (Max > 99)
			Max = 99;
		int Base = 100 - Max;
		return Base;
	}

	public static int roughChance()
	{
		int Min = plugin.getConfig().getInt("rough-min-percent-decrease");
		int Max = plugin.getConfig().getInt("rough-max-percent-decrease");
		if (Max < 1)
			Max = 1;
		if (Min < 0)
			Min = 0;
		if (Min >= Max)
			Min = Max - 1;
		int Variation = Max - Min;
		return Variation;
	}

	public static int roughBase()
	{
		int Min = plugin.getConfig().getInt("rough-min-percent-decrease");
		if (Min < 0)
			Min = 0;
		if (Min > 99)
			Min = 99;
		int Base = 100 - Min;
		return Base;
	}

	public static int teeingGroundChance()
	{
		int Min = plugin.getConfig().getInt("teeing-ground-min-percent-increase");
		int Max = plugin.getConfig().getInt("teeing-ground-max-percent-increase");
		if (Max < 1)
			Max = 1;
		if (Min < 0)
			Min = 0;
		if (Min >= Max)
			Min = Max - 1;
		int Variation = Max - Min;
		return Variation;
	}

	public static int teeingGroundBase()
	{
		int Min = plugin.getConfig().getInt("teeing-ground-min-percent-decrease");
		if (Min < 0)
			Min = 0;
		if (Min > 99)
			Min = 99;
		int Base = 100 - Min;
		return Base;
	}

	public static enum LandedBlock {
		SAND_TRAP("sand-trap"),
		ROUGH("rough"),
		FAIRWAY("fairway"),
		GREEN("green"),
		HOLE("hole"),
		TEE("tee"),
		OB("ob");

		private String blockName;
		LandedBlock(String blockName) {
			this.blockName = blockName;
		}
		public String getName() {
			return this.blockName;
		}
	}
	
	public static boolean setConfig(String path, String value)
	{
		try {
			if (StringUtils.isNumeric(value)) {
				plugin.getConfig().set(path, Integer.parseInt(value));
			} else if (NumberUtils.isNumber(value)) {
				plugin.getConfig().set(path, Double.parseDouble(value));
			} else if (0 == value.indexOf("\"") && value.length()-1 == value.lastIndexOf("\"")) {
				plugin.getConfig().set(path, value.substring(1, value.length()-1));
			} else {
				return false;
			}
		} catch(NumberFormatException e) {
			return false;
		}
		return true;
	}
	public static String getConfig(String path)
	{
		return plugin.getConfig().getString(path);
	}
	public static Set<String> getConfigKey(String path)
	{
		Set<String> result = plugin.getConfig().getConfigurationSection(path).getKeys(false);
		if (null == result) {
			result = new HashSet<String>();
		}
		return result;
	}
	public static void saveConfig()
	{
		plugin.saveConfig();
		plugin.reloadConfig();
	}

	public static LandedBlock whereLandedBlock(Material targetMaterial, int targetData)
	{
		for(LandedBlock landedBlock : LandedBlock.values()) {
			Set<String> landedBlockNameList =
					plugin.getConfig().getConfigurationSection(
							"landed-block." + landedBlock.getName()).getKeys(false);
			for(String landedBlockName : landedBlockNameList) {
				Material configMaterial = Material.getMaterial(landedBlockName);
				int confidData = plugin.getConfig().getInt(
						"landed-block." + landedBlock.getName() +"."+ landedBlockName);
				if (((-1 == confidData) || (targetData == confidData)) && (configMaterial == targetMaterial)) {
					return landedBlock;
				}
			}
		}

		return LandedBlock.FAIRWAY;
	}

	public static boolean sameLandedBlock(LandedBlock landedBlock, Material targetMaterial, int targetData)
	{
		Set<String> landedBlockNameList =
				plugin.getConfig().getConfigurationSection(
						"landed-block." + landedBlock.getName()).getKeys(false);
		for(String landedBlockName : landedBlockNameList) {
			Material configMaterial = Material.getMaterial(landedBlockName);
			int confidData = plugin.getConfig().getInt(
					"landed-block." + landedBlock.getName() +"."+ landedBlockName);
			if (((-1 == confidData) || (targetData == confidData)) && (configMaterial == targetMaterial)) {
				return true;
			}
		}

		return false;
	}
	
	public static String getLeapString(int club, LandedBlock landedBlock)
	{
		if (false == plugin.getConfig().isSet("max-leap." + landedBlock.getName() + "." + String.valueOf(club))) {
			return "unknown";
		}
		String maxLeap = plugin.getConfig().getString(
				"max-leap." + landedBlock.getName() + "." + String.valueOf(club)
			);
		return maxLeap;
	}

	public static Double getClubSpeed(int club, LandedBlock landedBlock)
	{
		Double speed = plugin.getConfig().getDouble(
			"club-speed." + landedBlock.getName() + "." + String.valueOf(club)
		);
		if (false == plugin.getConfig().isSet("club-speed." + landedBlock.getName() + "." + String.valueOf(club))) {
			speed = plugin.getConfig().getDouble(
					"club-speed." + LandedBlock.FAIRWAY.getName() + "." + String.valueOf(club)
			);
		}
		return speed;
	}
}
