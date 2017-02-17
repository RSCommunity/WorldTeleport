package io.github.kunonx.WorldTeleport.event;

import io.github.kunonx.WorldTeleport.inventory.WorldInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class WorldTeleportClickEvent extends WorldTeleportEvent
{
	private ItemStack clicked;

	public WorldTeleportClickEvent(Player player, WorldInventory GUI, ItemStack clicked)
	{
		super(player, GUI);
		this.clicked = clicked;
	}

	public ItemStack getItemClicked()
	{
		return this.clicked;
	}

	private static final HandlerList Handler = new HandlerList();

	@Override public HandlerList getHandlers() { return Handler; }

	public HandlerList getHandlerList() { return getHandlers(); }
}
