package io.github.kunonx.WorldTeleport.command;

import java.util.List;

import io.github.kunonx.DesignFramework.command.CustomizeCommand;
import io.github.kunonx.WorldTeleport.WorldTeleport;
import org.bukkit.command.CommandSender;

public class CmdReload extends CustomizeCommand<CmdReload>
{
	public CmdReload()
	{
		this.addAliases("reload");
		this.setPermission("reload");
		this.setDescription("Reload SyncWorldTeleport Plugin");
	}
	
	@Override
	public boolean perform(CommandSender sender, List<String> args)
	{
		WorldTeleport.getInstance().reload();
		WorldTeleport.getInstance().getPluginMsg().send(sender, "reload complete.");
		return true;
	}
}
