package io.github.kunonx.WorldTeleport.command;

import java.util.List;

import io.github.kunonx.DesignFramework.Parameter;
import io.github.kunonx.DesignFramework.command.CustomizeCommand;
import io.github.kunonx.DesignFramework.message.Msg;
import org.bukkit.command.CommandSender;

public class CmdLocationSet extends CustomizeCommand<CmdLocationSet>
{
	public CmdLocationSet()
	{
		this.addAliases("set");
		this.addParameter(new Parameter("name|index", true));
		this.setDescription("set new Teleport location");
		this.setPermission("set");
	}

	@Override
	public boolean perform(CommandSender sender, List<String> args)
	{
		Msg.sendTxt(sender, "&cSorry! This command not supported yet.");
		Msg.sendTxt(sender, "&bIf you want to get support for this feature, contact your server administrator.");
		return false;
	}
}
