package io.github.kunonx.WorldTeleport.command;

import java.util.List;

import io.github.kunonx.DesignFramework.Parameter;
import io.github.kunonx.DesignFramework.command.CustomizeCommand;
import io.github.kunonx.DesignFramework.message.Msg;

import io.github.kunonx.WorldTeleport.WorldTeleport;
import io.github.kunonx.WorldTeleport.core.WorldInventoryCore;
import io.github.kunonx.WorldTeleport.inventory.WorldInventory;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CmdOpenWorld extends CustomizeCommand<CmdOpenWorld>
{
	private static CmdOpenWorld instance = new CmdOpenWorld();
	public static CmdOpenWorld getInstance() { return instance; }

	private Runnable runnable = new Runnable() {
        public void run()
        {
            CmdOpenWorld.getInstance().inventory.open();
        }
    };

	private WorldInventory inventory;

	public CmdOpenWorld()
	{
		this.addAliases("openworld");
		this.addParameter(new Parameter("name"));
		this.setDescription(WorldTeleport.getInstance().getLangConfiguration().getMessage("Command.WT_OPENWORLD_DESCRIPTION"));
		this.setPermission("worldteleport.open");
	}
	
	@Override
	public boolean perform(CommandSender sender, List<String> args)
	{
		Msg msg = WorldTeleport.getInstance().getPluginMsg();
		if(sender instanceof ConsoleCommandSender)
		{
			msg.send(sender, "&cSorry, This command doesn't support in console. :(");
			return false;
		}
		Player player = (Player)sender;
		if(args.size() == 0)
        {
			WorldInventory inventory = WorldInventoryCore.setChangeInventoryType(player, null);
            inventory.open();
		}
		else
		{
			if(args.get(0).equalsIgnoreCase("DEFAULT"))
			{
				WorldInventory inventory = WorldInventoryCore.setChangeInventoryType(player, null);
				inventory.open();
			}
			else
			{
				if(! WorldInventory.isExistedCustomFile(args.get(0)))
				{
                    msg.send(sender, "&c해당 이름의 인벤토리는 구현되지 않았습니다. 서버 관리자 또는 관계자에게 문의하십시오.");
					return true;
				}
				WorldInventory inventory = WorldInventoryCore.setChangeInventoryType(player, args.get(0));
				inventory.open();
			}
		}
		return true;
	}
}
