package io.github.kunonx.WorldTeleport.event;

import io.github.kunonx.DesignFramework.event.DesignFrameworkEvent;
import io.github.kunonx.WorldTeleport.entity.WorldPlayer;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;

public class WorldPlayerLocationAddEvent extends DesignFrameworkEvent
{
	private WorldPlayer player;
	public void setPlayer(WorldPlayer player) { this.player = player; }
	public WorldPlayer getPlayer() { return this.player; }
	
	private Location location;
	public void setLocation(Location location) { this.location = location; }
	public Location getLocation() { return this.location; }
	
	public WorldPlayerLocationAddEvent(WorldPlayer player, Location location)
	{
		if(player == null) throw new IllegalStateException("player cannot be null");
		if(location == null) throw new IllegalStateException("location cannot be null");
		this.player = player;
		this.location = location;
	}

	private static final HandlerList Handler = new HandlerList();

	@Override public HandlerList getHandlers() { return Handler; }

	public HandlerList getHandlerList() { return getHandlers(); }
}
