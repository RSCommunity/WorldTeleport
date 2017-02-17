package io.github.kunonx.WorldTeleport.event;

import io.github.kunonx.WorldTeleport.inventory.WorldInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class WorldTeleportCloseEvent extends WorldTeleportEvent
{
	public WorldTeleportCloseEvent(Player player, WorldInventory GUI)
	{
		super(player, GUI);
	}

	private static final HandlerList Handler = new HandlerList();

	@Override public HandlerList getHandlers() { return Handler; }

	public HandlerList getHandlerList() { return getHandlers(); }
}
