package io.github.kunonx.WorldTeleport.core;

import java.io.File;
import java.io.IOException;

import io.github.kunonx.DesignFramework.core.Core;
import io.github.kunonx.WorldTeleport.WorldTeleport;
import io.github.kunonx.WorldTeleport.event.WorldTeleportClickEvent;
import io.github.kunonx.WorldTeleport.event.WorldTeleportCloseEvent;
import io.github.kunonx.WorldTeleport.event.WorldTeleportOpenEvent;
import io.github.kunonx.WorldTeleport.inventory.WorldInventory;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

public class WorldInventoryCore extends Core
{
	private static WorldInventoryCore instance = new WorldInventoryCore();
	public static WorldInventoryCore getInstance() { return instance; }
	
	@Override
	public void run()
	{
		for(WorldInventory i : WorldInventory.getRegisterInventories().values())
		{
			if(i.isEnabled())
			{
				if(i.isCustomizedInventory())
				{
					File file = new File(WorldTeleport.getInstance().getDataFolder(), "Custom_" + i.getCustomizationName() + ".yml");
					i.setConfig(YamlConfiguration.loadConfiguration(file));
				}
				else
				{
					i.setConfig(this.getActivePlugin().getSyncConfig().getYaml());
				}
				i.refreshInventoryData();
			}
		}
	}

	public static WorldInventory setChangeInventoryType(Player player, String customName)
	{
		if(WorldInventory.hasWorldInventory(player))
			WorldInventory.getRegisterInventories().remove(player);

		WorldInventory i = new WorldInventory(player, customName);
		return i;
	}
	
	@EventHandler
	public void onClickItem(InventoryClickEvent event)
	{
		Player player = (Player)event.getWhoClicked();

		if(! WorldInventory.hasWorldInventory(player)) return;
		
		if(!(WorldInventory.getPlayerInventory(player).equals(event.getInventory()))) return;
		
		if(event.getRawSlot() < 0) return;

		WorldInventory gui = WorldInventory.getPlayerInventory(player);
		
		WorldTeleportClickEvent event2 = new WorldTeleportClickEvent(player, gui, gui.getRegisterCategory().get(event.getRawSlot()));
		if(event2.isCancelled()) return;
		event2.run();
		event.setCancelled(true);

		WorldInventory targetInv = event2.getWorldInventory();

		if(targetInv.isScripting(event.getSlot()))
		{
			if(targetInv.isScriptingUndefined(event.getSlot()))
			{
				return;
			}
			event2.getPlayer().chat("/" + targetInv.getScript(event.getRawSlot()));
			return;
		}

		if(targetInv.getWorldFromCurrentItem(event2.getItemClicked()) == null) return;
		
		World w = targetInv.getWorldFromCurrentItem(event2.getItemClicked());
		event2.getWorldInventory().getSoundClick().run(event2.getPlayer());

		if(targetInv.isEnabledWorldPermission(w) && ! targetInv.viewerHasPermission((w)))
		{
			this.getActivePlugin().getPluginMsg().send(event2.getPlayer(), "&4" + this.getActivePlugin().getLangConfiguration().getMessage("System.DENY_PERMISSION"));
			return;
		}

		if(targetInv.getLocationFromCurrentItem(event2.getItemClicked()) == null) {
			event2.getPlayer().teleport(targetInv.getWorldFromCurrentItem(event2.getItemClicked()).getSpawnLocation());
		}
		else
		{
			event2.getPlayer().teleport(targetInv.getLocationFromCurrentItem(event2.getItemClicked()));
		}
	}
	
	@EventHandler
	public void inventoryOpen(InventoryOpenEvent event)
	{
		Player player = (Player) event.getPlayer();
		Inventory inv = event.getInventory();
		WorldInventory inventory = WorldInventory.getPlayerInventory(player);
		if(inventory.equals(inv))
		{
			WorldTeleportOpenEvent event2 = new WorldTeleportOpenEvent(player, inventory);
			event2.run();
			
			event2.getWorldInventory().getSoundOpen().run(player);
		}
	}
	
	@EventHandler
	public void inventoryClose(InventoryCloseEvent event)
	{
		try
		{
			Player player = (Player) event.getPlayer();
			Inventory inv = event.getInventory();
			WorldInventory inventory = WorldInventory.getPlayerInventory(player);
			if(inventory.equals(inv))
			{
				WorldTeleportCloseEvent event2 = new WorldTeleportCloseEvent(player, inventory);
				event2.run();
				
				event2.getWorldInventory().getSoundClose().run(player);
			}
		}
		catch(NullPointerException e)
		{
			return;
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void playerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		if(!WorldInventory.hasWorldInventory(player))
			new WorldInventory(player);
		
		File file = new File(this.getActivePlugin().getSyncConfig().getFolder(), "players/" + player.getUniqueId().toString() + ".yml");
		if(! file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				
			}
		}
	}
	
	@EventHandler
	public void playerLeave(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		if(WorldInventory.hasWorldInventory(player))
			WorldInventory.getRegisterInventories().remove(player);
	}
	
	@EventHandler
	public void pluginEnabling(PluginEnableEvent event)
	{
		if(event.getPlugin() == (Plugin)WorldTeleport.getInstance())
		{
			for(Player p : Bukkit.getOnlinePlayers())
			{
				if(! WorldInventory.hasWorldInventory(p))
				{
					new WorldInventory(p);
				}
			}
		}
	}

	@EventHandler
	public void playerTeleport(PlayerTeleportEvent e)
	{
		Player player = e.getPlayer();
		Location fromLocation = e.getFrom();
		Location toLocation = e.getTo();
		WorldInventory w = WorldInventory.getPlayerInventory(player);
		if(fromLocation.getWorld() != toLocation.getWorld())
		{
			if(w.isEnabledWorldPermission(toLocation.getWorld()))
			{
				String permission = w.getCustomPermission(toLocation.getWorld());
				if(! player.hasPermission(permission))
				{
					e.setCancelled(true);
					WorldTeleport.getInstance().getPluginMsg().send(player, "&cYou cannot teleport because you do not have that world permission.");
					WorldTeleport.getInstance().getPluginMsg().send(player, "&7Required: " + permission);
				}
			}
		}
		else
		{
			return;
		}


	}
}
