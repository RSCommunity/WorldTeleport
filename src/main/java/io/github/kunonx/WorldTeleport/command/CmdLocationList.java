package io.github.kunonx.WorldTeleport.command;

import java.util.List;

import io.github.kunonx.DesignFramework.Parameter;
import io.github.kunonx.DesignFramework.command.CustomizeCommand;

import io.github.kunonx.DesignFramework.message.Msg;
import io.github.kunonx.WorldTeleport.WorldTeleport;
import io.github.kunonx.WorldTeleport.entity.WorldPlayer;
import io.github.kunonx.WorldTeleport.entity.WorldPlayerLocation;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CmdLocationList extends CustomizeCommand<CmdLocationList>
{	
	public CmdLocationList()
	{
		this.addAliases("list", "l");
		Parameter p = new Parameter("playername", false);
		this.addParameter(p);
		this.setDescription("playerlist");
		this.setPermission("list");
	}

	@Override
	public boolean perform(CommandSender sender, List<String> args)
	{
		Msg msg = WorldTeleport.getInstance().getPluginMsg();
		if(sender instanceof ConsoleCommandSender)
		{
			if(args.size() == 0)
			{
				Msg.sendTxt(sender, "&cNot Supported Console :(");
				return true;
			}
		}
		WorldPlayer player;
		if(args.size() >= 1)
		{
			player = WorldPlayer.fromPlayer(args.get(0));
		}
		else
		{
			player = WorldPlayer.fromPlayer((Player)sender);
		}
		if(player.getRegisterLocation().size() == 0)
		{
			msg.send(sender, "&cThe location database of {0} is empty", player.getPlayer().getName());
			return false;
		}
		else
		{
			msg.send(sender, "==== [ Location list (Username: {0}, {1} found(s) ] =====", player.getPlayer().getName(), player.getRegisterLocation().size());
			int index = 0;
			for(WorldPlayerLocation wpl : player.getRegisterLocation())
			{
				msg.send(sender, "&a({0})&b {1}, &eDetail: [world:{2}, x:{3}, y:{4}, z:{5} default: {6}]", index, wpl.getName(),
						wpl.getLocation().getWorld().getName(), wpl.getLocation().getBlockX(), wpl.getLocation().getBlockY(), wpl.getLocation().getBlockZ(),
						player.getDefaultLocation(wpl.getWorld()) == wpl);
				index++;
			}
			return true;
		}
	}
}
