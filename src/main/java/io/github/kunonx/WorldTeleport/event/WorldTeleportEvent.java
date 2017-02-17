package io.github.kunonx.WorldTeleport.event;

import io.github.kunonx.DesignFramework.event.DesignFrameworkEvent;
import io.github.kunonx.WorldTeleport.inventory.WorldInventory;
import org.bukkit.entity.Player;

public abstract class WorldTeleportEvent extends DesignFrameworkEvent
{
	private Player player;
	private WorldInventory GUI;

	public WorldTeleportEvent(Player player, WorldInventory GUI)
	{
		this.player = player;
		this.GUI = GUI;
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public Player getPlayer()
	{
		return this.player;
	}
	
	public void setWorldInventory(WorldInventory inventory)
	{
		this.GUI = inventory;
	}
	
	public WorldInventory getWorldInventory()
	{
		return this.GUI;
	}
}
