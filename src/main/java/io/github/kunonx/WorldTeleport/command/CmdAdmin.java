package io.github.kunonx.WorldTeleport.command;

import io.github.kunonx.DesignFramework.command.CustomizeCommand;

public class CmdAdmin extends CustomizeCommand<CmdAdmin>
{
	public CmdAdmin()
	{
		this.addAliases("admin");
		this.setPermission("admin");
	}
}
