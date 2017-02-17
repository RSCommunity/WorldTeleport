package io.github.kunonx.WorldTeleport.entity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import io.github.kunonx.DesignFramework.ClassActivation;
import io.github.kunonx.DesignFramework.plugin.DesignFrameworkPlugin;
import io.github.kunonx.DesignFramework.plugin.config.SyncYamlConfiguration;

import io.github.kunonx.WorldTeleport.WorldTeleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class WorldPlayer implements ClassActivation
{
	private static transient final List<WorldPlayer> SYNC_PLAYER = new ArrayList<WorldPlayer>(new HashSet<WorldPlayer>());

	public static List<WorldPlayer> getRegisterPlayer() { return SYNC_PLAYER; }

	public static WorldPlayer fromPlayer(UUID uuid){ return fromPlayer(Bukkit.getPlayer(uuid)); }

	public static WorldPlayer fromPlayer(String s) { return fromPlayer(Bukkit.getPlayer(s)); }

	public static WorldPlayer fromPlayer(Player obj)
	{
		if(obj == null) throw new IllegalStateException("Player cannot be null. Please check the data.");
		for(WorldPlayer p : SYNC_PLAYER)
			if(p.getPlayer() == obj) return p;
		return new WorldPlayer(obj);
	}

	public static void initLoad(Player player)
	{
		if(player == null) throw new IllegalStateException("Player cannot be null. Please check the data.");
		new WorldPlayer(player);
	}

	private WorldPlayer(Player player)
	{
		if(player == null) throw new IllegalStateException("The player cannot be null");
		this.player = player;
		this.playerDataManager = SyncYamlConfiguration.initLoad(WorldTeleport.getInstance(), new File(WorldTeleport.getInstance().getDataFolder(),
				"players/" + this.getPlayer().getUniqueId().toString() + ".yml"));
		this.setEnabled(WorldTeleport.getInstance());
	}

	private SyncYamlConfiguration playerDataManager;
	public SyncYamlConfiguration getPlayerDataManager() { return this.playerDataManager; }
	
	private final Player player;
	public Player getPlayer() { return this.player; }
	
	private DesignFrameworkPlugin activePlugin;
	public DesignFrameworkPlugin getActivePlugin() { return this.activePlugin; }
	
	protected void initialized()
	{
		this.REGISTER_LOCATIONS.clear();
	}

	public void refreshLocationData()
	{
		YamlConfiguration y = this.getPlayerDataManager().getYaml();
		this.initialized();
		
		// reloading data from player settings
		for(String locName : y.getKeys(false))
		{
			try
			{
				Location location = new Location(Bukkit.getServer().getWorld(y.getString(locName + ".Location.World")),
						y.getDouble(locName + ".Location.X"),
						y.getDouble(locName + ".Location.Y"),
						y.getDouble(locName + ".Location.Z"),
						(float)y.getDouble(locName + ".Location.Yaw"),
						(float)y.getDouble(locName + ".Location.Pitch"));
				WorldPlayerLocation loc = new WorldPlayerLocation(this, locName, location);
				this.REGISTER_LOCATIONS.add(loc);
			}
			catch(NullPointerException e)
			{
				this.getActivePlugin().getPluginMsg().sendToConsole("&bSyntax Error found: {0}, &cDetected missing data! Have you modified someone's database or deleted?", locName);
				continue;
			}
		}
	}

	public boolean addLocation(WorldPlayerLocation loc)
	{
		if(loc == null) throw new IllegalStateException("location cannot be null");
		return this.registerLocation(loc);
	}

	public WorldPlayerLocation getDefaultLocation(World world)
	{
		YamlConfiguration y = this.getPlayerDataManager().getYaml();
		try
		{
			for(String name : y.getKeys(false))
			{
				if(Bukkit.getServer().getWorld(y.getString(name + ".Location.World")) == world)
				{
					if (y.getBoolean(name + ".Default"))
					{
						return this.getLocation(name);
					}
				}
			}
			return null;
		}
		catch(NullPointerException e)
		{
			return null;
		}
	}

	
	public WorldPlayerLocation getLocation(String name)
	{
		for(WorldPlayerLocation l : this.REGISTER_LOCATIONS)
		{
			if(l.getName().equalsIgnoreCase(name))
			{
				return l;
			}
		}
		return null;
	}
	
	private List<WorldPlayerLocation> REGISTER_LOCATIONS = new ArrayList<WorldPlayerLocation>(new HashSet<WorldPlayerLocation>());
	public List<WorldPlayerLocation> getRegisterLocation()
	{
		return REGISTER_LOCATIONS;
	}

	public WorldPlayerLocation getLocation(int index)
	{
		try
		{
			return this.REGISTER_LOCATIONS.get(index);
		}
		catch(IndexOutOfBoundsException e)
		{
			return null;
		}
	}

	public synchronized boolean registerLocation(String name, Location loc)
	{
		return this.registerLocation(new WorldPlayerLocation(this, name, loc));
	}

	public synchronized boolean registerLocation(WorldPlayerLocation wpl)
	{
		if(wpl == null) return false;
		if(! this.getPlayerDataManager().containsKey(wpl.getName()))
		{
			this.getPlayerDataManager().fixedDefault(wpl.getName() + ".Default", this.getDefaultLocation(wpl.getLocation().getWorld()) == null);
			this.getPlayerDataManager().fixedDefault(wpl.getName() + ".Id", wpl.getId());
			this.getPlayerDataManager().fixedDefault(wpl.getName() + ".Location.World", wpl.getLocation().getWorld().getName());
			this.getPlayerDataManager().fixedDefault(wpl.getName() + ".Location.X", wpl.getLocation().getX());
			this.getPlayerDataManager().fixedDefault(wpl.getName() + ".Location.Y", wpl.getLocation().getY());
			this.getPlayerDataManager().fixedDefault(wpl.getName() + ".Location.Z", wpl.getLocation().getZ());
			this.getPlayerDataManager().fixedDefault(wpl.getName() + ".Location.Pitch", wpl.getLocation().getPitch());
			this.getPlayerDataManager().fixedDefault(wpl.getName() + ".Location.Yaw", wpl.getLocation().getYaw());
			this.getPlayerDataManager().save();
			return true;
		}
		else
		{

		}
		return false;
	}

	public synchronized boolean addLocation(String name, Location loc)
	{
		WorldPlayerLocation wtl = new WorldPlayerLocation(this, name, loc);
		return this.addLocation(wtl);
	}

	public boolean isRegisterLocation(String locationName)
	{
		 for(WorldPlayerLocation wpl :  this.REGISTER_LOCATIONS)
		 {
			 if(wpl.getName().equalsIgnoreCase(locationName))
			 {
				 return true;
			 }
		 }
		 return false;
	}

	public synchronized boolean removeLocation(int index)
	{
		try
		{
			return this.removeLocation(this.REGISTER_LOCATIONS.get(index).getName());
		}
		catch(IndexOutOfBoundsException e)
		{
			return false;
		}
	}

	public synchronized boolean removeLocation(String name)
	{
		if(name == null)
			throw new IllegalStateException("name cannot be null");
	
		 for(WorldPlayerLocation wtl :  this.REGISTER_LOCATIONS)
		 {
			 if(wtl.getName().equalsIgnoreCase(name))
			 {
				 if(this.getPlayerDataManager().containsKey((wtl.getName())))
				 {
					 this.getPlayerDataManager().removeKey(wtl.getName());
					 this.getPlayerDataManager().save();
					 return true;
				 }
			 }
		 }
		 return true;
	}
	
	@Override
	public void setEnabled(DesignFrameworkPlugin plugin)
	{
		this.activePlugin = plugin;
		this.setEnabled(this.activePlugin != null);
	}
	
	@Override
	public void setEnabled(boolean active)
	{
		if(active)
		{
			SYNC_PLAYER.add(this);
		}
		else
		{
			if(this.isEnabled())
			{
				SYNC_PLAYER.remove(this);
			}
		}
	}
	
	@Override
	public boolean isEnabled()
	{
		for(WorldPlayer player : WorldPlayer.getRegisterPlayer())
		{
			if(player.equals(this)) return true;
		}
		return false;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof WorldPlayer) return this.getPlayerDataManager().equals(((WorldPlayer) obj).getPlayerDataManager());
		else return false;
	}
}
