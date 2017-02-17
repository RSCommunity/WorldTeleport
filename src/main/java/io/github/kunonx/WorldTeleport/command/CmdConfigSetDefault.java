package io.github.kunonx.WorldTeleport.command;

import java.util.List;

import io.github.kunonx.DesignFramework.command.CustomizeCommand;
import org.bukkit.command.CommandSender;


public class CmdConfigSetDefault extends CustomizeCommand<CmdConfigSetDefault>
{
	public CmdConfigSetDefault()
	{
		this.addAliases("setd", "setdefault");
		
	}
	
	@Override
	public boolean perform(CommandSender sender, List<String> args)
	{
		// TODO Auto-generated method stub
		return super.perform(sender, args);
	}
}
