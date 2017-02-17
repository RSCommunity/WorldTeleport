package io.github.kunonx.WorldTeleport.entity;

import io.github.kunonx.DesignFramework.entity.AbstractLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldPlayerLocation extends AbstractLocation
{
	public WorldPlayerLocation(WorldPlayer player, String name, Location location)
	{
		this(player, name, null, location);
	}

	public WorldPlayerLocation(WorldPlayer player, String name, String id, Location location)
	{
		super(name, id, location);
		this.owner = player;
	}

	private WorldPlayer owner;
	public WorldPlayer getOwner() { return this.owner; }
	public boolean isPublicLocation() { return this.owner == null; }
	public void setOwner(WorldPlayer player)
	{
		if(player == null) throw new IllegalStateException("player cannot be null");

		//New processing register
		owner.removeLocation(this.getName());
		this.owner = player;
		player.addLocation(this);
	}

	@Override
	public void setName(String name)
	{
		owner.removeLocation(this.getName());
		this.setName(name);
		owner.registerLocation(this);
	}

	public void teleport(Player player)
	{
		player.teleport(this.getLocation());
	}
}
