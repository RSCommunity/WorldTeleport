package io.github.kunonx.WorldTeleport.command;

import java.util.List;

import io.github.kunonx.DesignFramework.Parameter;
import io.github.kunonx.DesignFramework.command.CustomizeCommand;
import io.github.kunonx.DesignFramework.message.Msg;
import io.github.kunonx.DesignFramework.message.StringUtil;
import io.github.kunonx.WorldTeleport.WorldTeleport;
import io.github.kunonx.WorldTeleport.entity.WorldPlayer;
import io.github.kunonx.WorldTeleport.entity.WorldPlayerLocation;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdLocationTeleport extends CustomizeCommand<CmdLocationTeleport>
{
	private static CmdLocationTeleport instance = new CmdLocationTeleport();
	public static CmdLocationTeleport getInstance() { return instance; }
	
	public CmdLocationTeleport()
	{
		this.addAliases("wtloc", "loc");
		this.addParameter(new Parameter("index|name", true));
		this.addParameter(new Parameter("playername"));
		this.setDescription("teleport to someone's location");
		this.setPermission("worldteleport.teleport");
	}
	
	@Override
	public boolean perform(CommandSender sender, List<String> args)
	{
		Msg msg = WorldTeleport.getInstance().getPluginMsg();
		if(!(sender instanceof Player))
		{
			Msg.sendTxt(sender, "&cSorry, This command doesn't support in console. :(");
			return false;
		}
		WorldPlayer player = WorldPlayer.fromPlayer((Player)sender);
		if(StringUtil.isNumber(args.get(0)))
		{
			if(player.getLocation(Integer.parseInt(args.get(0))) != null)
			{
				if(player.getRegisterLocation().isEmpty())
				{
					msg.send(sender, "&cNo have database. please create a new location.");
					return false;
				}
				if(Integer.parseInt(args.get(0)) >= player.getRegisterLocation().size())
				{
					msg.send(sender, "&cThe index number \"{0}\" exceeded the number of your database. &eMaximum index number: &f{1}", args.get(0), player.getRegisterLocation().size() - 1);
					return false;
				}

				WorldPlayerLocation l = player.getLocation(Integer.parseInt(args.get(0)));
				l.teleport((Player)sender);
				return true;
			}
			else
			{
				msg.send(sender, "&cThe index number \"{0}\" exceeded the number of your database. &eMaximum index number: &f{1}", args.get(0),player.getRegisterLocation().size() - 1);
				return false;
			}
		}
		for(WorldPlayerLocation l : player.getRegisterLocation())
		{
			if(l.getName().equalsIgnoreCase(args.get(0)))
			{
				l.teleport((Player)sender);
				return true;
			}
		}
		msg.send(sender, "&cNot found information for name: \"{0}\"", args.get(0));
		return false;
	}
}
