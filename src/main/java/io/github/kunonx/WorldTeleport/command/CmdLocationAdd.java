package io.github.kunonx.WorldTeleport.command;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import io.github.kunonx.DesignFramework.Parameter;
import io.github.kunonx.DesignFramework.command.CustomizeCommand;
import io.github.kunonx.DesignFramework.entity.world.WorldLocation;
import io.github.kunonx.DesignFramework.message.Msg;
import io.github.kunonx.DesignFramework.message.StringUtil;
import io.github.kunonx.DesignFramework.security.IntegrityChecker;

import io.github.kunonx.WorldTeleport.WorldTeleport;
import io.github.kunonx.WorldTeleport.entity.WorldPlayer;
import io.github.kunonx.WorldTeleport.event.WorldPlayerLocationAddEvent;

import io.github.kunonx.WorldTeleport.event.WorldPlayerLocationCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CmdLocationAdd extends CustomizeCommand<CmdLocationAdd>
{
	public CmdLocationAdd()
	{
		this.addParameter(new Parameter("name"));
		this.addParameter(new Parameter("public|playername"));
		this.addAliases("a", "add");
		this.setDescription("add location");
		this.setPermission("add");
	}

        @Override
	public boolean perform(CommandSender sender, List<String> args) 
	{
		Msg msg = WorldTeleport.getInstance().getPluginMsg();
		if(!(sender instanceof Player))
		{
			msg.send(sender, "&cSorry, This command doesn't support in console. :(");
			return false;
		}
		Player p = (Player)sender;
		if(this.hasPermission(p))
		{
			if(args.size() == 0)
			{
				WorldPlayer player = WorldPlayer.fromPlayer(p);
				msg.send(sender, "&bLoading location data");
				Location location = p.getLocation();
                msg.send(sender, "&bLoading location data..complete.");
                WorldTeleport.getInstance().getLangConfiguration().sendMessage(sender, "Command.NAME_UNDEFINED");
				String name = null;
				try
				{
					name = IntegrityChecker.sha1(location.toString());
				}
				catch (NoSuchAlgorithmException e)
				{
						msg.send(sender, "&cFailed to get hash data! Try again.");
					return false;
				}
				msg.send(sender, "&bHashcode name:&f {0}", name);
				WorldPlayerLocationAddEvent event = new WorldPlayerLocationAddEvent(player, location);
				if(event.isCancelled()) return false;
				event.run();
				event.getPlayer().registerLocation(name, event.getLocation());
				WorldTeleport.getInstance().getLangConfiguration().sendMessage(sender, "Command.LOCATION_REGISTERED", "&8(HashCode)");
				return true;
			}
			else
			{
				WorldPlayer wp = null;
				msg.send(sender, "&bLoading location data");
				Location location = p.getLocation();
				msg.send(sender, "&bLoading location data..complete.");
				String name = args.get(0);
				if(StringUtil.isNumber(name))
				{
					WorldTeleport.getInstance().getLangConfiguration().sendMessage(sender, "Command.NUMBER_FORMAT_NOT_SUPPORTED");
					return false;
				}
				msg.send(sender, "&bLocation name:&f {0}", name);
				if(args.size() == 2)
				{
					if(this.hasChildPermission((Player) sender, "others"))
					{
						if(args.get(1).equalsIgnoreCase("public"))
						{

							if(WorldLocation.isRegistered(name))
							{
								WorldTeleport.getInstance().getLangConfiguration().sendMessage(sender, "Command.NUMBER_FORMAT_NOT_SUPPORTED");
							}
							else
							{
								WorldLocation wtlocation =  new WorldLocation(name, location);
								wtlocation.save();
								WorldTeleport.getInstance().getLangConfiguration().sendMessage(sender, "Command.PUBLIC_LOCATION_REGISTERED", name);
							}
							return true;
						}
						else
						{
							wp = WorldPlayer.fromPlayer(Bukkit.getPlayer(args.get(1)));
						}
					}
					else
					{
						WorldTeleport.getInstance().getLangConfiguration().sendMessage(sender, "Command.PERMISSION_DENIED");
						return false;
					}
				}
				else
				{
					wp = WorldPlayer.fromPlayer(p);
				}
				WorldPlayerLocationAddEvent event = new WorldPlayerLocationAddEvent(wp, location);
				if(event.isCancelled()) return false;
				event.run();
				if(event.getPlayer().isRegisterLocation(name))
				{
					WorldTeleport.getInstance().getLangConfiguration().sendMessage(sender, "Command.PLAYER_LOCATION_ALREADY_DEFINED");
					return false;
				}
				event.getPlayer().registerLocation(name, event.getLocation());
				WorldTeleport.getInstance().getLangConfiguration().sendMessage(sender, "Command.OTHERS_LOCATION_REGISTERED", event.getPlayer().getPlayer().getName(), name);
				return true;
			}
		}
		else
		{
			WorldTeleport.getInstance().getLangConfiguration().sendMessage(sender, "Command.PERMISSION_DENIED");
			return false;
		}
	}
}
