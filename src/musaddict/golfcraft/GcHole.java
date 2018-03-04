package musaddict.golfcraft;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


public class GcHole
{
  public World world;
  public String hole;
  public int par;
  public int x;
  public int y;
  public int z;
  public int hAxis;
  public int vAxis;
  
  public GcHole(String worldName, String hole, int par)
  {
    this(Bukkit.getWorld(worldName), hole, par, 0, 0, 0, 0, 0);
  }
  
  public GcHole(String worldName, String hole, int par, int x, int y, int z, int hAxis, int vAxis)
  {
    this(Bukkit.getWorld(worldName), hole, par, x, y, z, hAxis, vAxis);
  }
  
  public GcHole(String worldName, String hole, int par, Location location)
  {
    this(Bukkit.getWorld(worldName), hole, par,
    		location.getBlockX(), location.getBlockY(), location.getBlockZ(),
    		(int)location.getYaw(), (int)location.getPitch()
    		);
  }
  
  public GcHole(World world, String hole, int par, int x, int y, int z, int hAxis, int vAxis)
  {
    this.world = world;
    this.hole = hole;
    this.par = par;
    this.x = x;
    this.y = y;
    this.z = z;
    this.hAxis = hAxis;
    this.vAxis = vAxis;
  }
  
  public String toString()
  {
    return this.world.getName() + ";" + this.hole + ";" + this.par;
  }
  
  public Location getLocation()
  {
    return new Location(this.world, (double)this.x, (double)this.y, (double)this.z, (float)this.hAxis, (float)this.vAxis);
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
