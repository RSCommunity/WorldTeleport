package io.github.kunonx.WorldTeleport.command;

import io.github.kunonx.DesignFramework.command.CustomizeCommand;
import io.github.kunonx.WorldTeleport.WorldTeleport;

public class CmdConfig extends CustomizeCommand<CmdConfig>
{
	CmdConfigSetDefault cmdConfigSetDefault = new CmdConfigSetDefault();
	
	public CmdConfig()
	{
		this.addAliases("conf", "config");
		this.setDescription(WorldTeleport.getInstance().getLangConfiguration().getMessage("Command.WT_CONFIG_DESCRIPTION"));
		this.setPermission("config");
		
		this.addChild(this.cmdConfigSetDefault);
	}
}