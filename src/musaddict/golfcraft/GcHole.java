package musaddict.golfcraft;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;


public class GcHole
{
  public World world;
  public String hole;
  public int par;
  public int x;
  public int y;
  public int z;
  
  public GcHole(String worldName, String hole, int par)
  {
    this(Bukkit.getWorld(worldName), hole, par, 0, 0, 0);
  }
  
  public GcHole(String worldName, String hole, int par, int x, int y, int z)
  {
    this(Bukkit.getWorld(worldName), hole, par, x, y, z);
  }
  
  public GcHole(String worldName, String hole, int par, Block block)
  {
    this(Bukkit.getWorld(worldName), hole, par, block.getX(), block.getY(), block.getZ());
  }
  
  public GcHole(World world, String hole, int par, int x, int y, int z)
  {
    this.world = world;
    this.hole = hole;
    this.par = par;
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public String toString()
  {
    return this.world.getName() + ";" + this.hole + ";" + this.par;
  }
  
  public Block getBlock()
  {
    return this.world.getBlockAt(this.x, this.y, this.z);
  }
  

  public boolean equals(Object obj)
  {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    GcHole otherHole = (GcHole)obj;
    
    return new EqualsBuilder().append(this.world, otherHole.world).append(this.hole, otherHole.hole).isEquals();
  }
}
