package io.github.kunonx.WorldTeleport.event;

import io.github.kunonx.DesignFramework.event.DesignFrameworkEvent;
import io.github.kunonx.WorldTeleport.entity.WorldPlayerLocation;
import org.bukkit.event.HandlerList;

public class WorldPlayerLocationEvent extends DesignFrameworkEvent
{
	private WorldPlayerLocation location;
	public void setLocation(WorldPlayerLocation location) { this.location = location; }
	public WorldPlayerLocation getLocation() { return this.location; }
	
	public WorldPlayerLocationEvent(WorldPlayerLocation location)
	{
		this.location = location;
	}

	private static final HandlerList Handler = new HandlerList();

	@Override public HandlerList getHandlers() { return Handler; }

	public HandlerList getHandlerList() { return getHandlers(); }
}
