package io.github.kunonx.WorldTeleport.event;

import io.github.kunonx.WorldTeleport.inventory.WorldInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class WorldTeleportOpenEvent extends WorldTeleportEvent
{
	public WorldTeleportOpenEvent(Player player, WorldInventory inventory)
	{
		super(player, inventory);
	}

	private static final HandlerList Handler = new HandlerList();

	@Override public HandlerList getHandlers() { return Handler; }

	public HandlerList getHandlerList() { return getHandlers(); }
}
