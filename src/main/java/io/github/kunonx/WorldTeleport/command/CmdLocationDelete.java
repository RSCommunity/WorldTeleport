package io.github.kunonx.WorldTeleport.command;

import io.github.kunonx.DesignFramework.Parameter;
import io.github.kunonx.DesignFramework.command.CustomizeCommand;
import io.github.kunonx.DesignFramework.message.Msg;
import io.github.kunonx.DesignFramework.message.StringUtil;
import io.github.kunonx.WorldTeleport.WorldTeleport;
import io.github.kunonx.WorldTeleport.entity.WorldPlayer;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CmdLocationDelete extends CustomizeCommand<CmdLocationDelete>
{
	public CmdLocationDelete()
	{
		this.addAliases("d", "del");
		this.addParameter(new Parameter("index|name", true));
		this.addParameter(new Parameter("public|playername"));
		this.setDescription(WorldTeleport.getInstance().getLangConfiguration().getMessage("Command.WT_LOCATION_DELETE_DESCRIPTION"));
		this.setPermission("delete");
	}
	
	@Override
	public boolean perform(CommandSender sender, List<String> args)
	{
		Msg msg = WorldTeleport.getInstance().getPluginMsg();
		if(sender instanceof Player)
		{
			Player p = (Player)sender;
			if(! this.hasPermission(p))
			{
				WorldTeleport.getInstance().getLangConfiguration().sendMessage(sender, "Command.PERMISSION_DENIED");
				return false;
			}
		}
		
		if(args.size() >= 1)
		{
			if(args.size() >=2)
			{
				if(sender instanceof Player)
				{
					if(! this.hasChildPermission((Player)sender, "others"))
					{
						WorldTeleport.getInstance().getLangConfiguration().sendMessage(sender, "Command.PERMISSION_DENIED");
						return false;
					}
				}
				WorldPlayer player = WorldPlayer.fromPlayer(Bukkit.getServer().getPlayer(args.get(1)));
				if(StringUtil.isNumber(args.get(0)))
				{
					msg.send(sender, "&aIndex number: {0}", args.get(0));
					msg.send(sender, "&eGetting data using index number...");
					msg.send(sender, "&aLocation Name: {0}", player.getLocation(Integer.parseInt(args.get(0))).getName());
					player.removeLocation(Integer.parseInt(args.get(0)));
				}
				else
				{
					player.removeLocation(args.get(0));
				}
				msg.send(sender, "&eLocation Name: {0}, The location was removed successfully.", player.getLocation(Integer.parseInt(args.get(0))).getName()
						, player.getPlayer().getName());
				return true;
			}
			
			if(sender instanceof ConsoleCommandSender)
			{
				msg.send(sender, "&cPlease provide additional arguments in the command. Arguments are required for use in the console.");
				return false;
			}
			WorldPlayer player = WorldPlayer.fromPlayer((Player)sender);
			if(StringUtil.isNumber(args.get(0)))
			{
				msg.send(sender, "&aIndex number: {0}", args.get(0));
				msg.send(sender, "&eGetting data using index number...");
				msg.send(sender, "&aLocation Name: {0}", player.getLocation(Integer.parseInt(args.get(0))).getName());
				player.removeLocation(Integer.parseInt(args.get(0)));
			}
			else
			{
				player.removeLocation(args.get(0));
			}
			msg.send(sender, "&eLocation(Index) Name: {0}, The location was removed successfully.", args.get(0));
			return true;
				
		}
		return false;
	}
}
