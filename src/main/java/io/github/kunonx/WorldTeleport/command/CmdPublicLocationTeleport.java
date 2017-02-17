package io.github.kunonx.WorldTeleport.command;

import java.util.List;

import io.github.kunonx.DesignFramework.Parameter;
import io.github.kunonx.DesignFramework.command.CustomizeCommand;
import org.bukkit.command.CommandSender;

public class CmdPublicLocationTeleport extends CustomizeCommand<CmdPublicLocationTeleport>
{
	private static CmdPublicLocationTeleport instance = new CmdPublicLocationTeleport();
	public static CmdPublicLocationTeleport getInstance() { return instance; }
	
	public CmdPublicLocationTeleport()
	{
		this.addAliases("wtploc", "ploc");
		this.addParameter(new Parameter("name", true));
		this.setDescription("Teleport to public location");
		this.setPermission("worldteleport.publicteleport");
	}
	
	@Override
	public boolean perform(CommandSender sender, List<String> args)
	{
		return false;
	}
}
