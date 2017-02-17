package io.github.kunonx.WorldTeleport.command;

import io.github.kunonx.DesignFramework.command.CustomizeCommand;
import io.github.kunonx.WorldTeleport.WorldTeleport;

public class CmdLocation extends CustomizeCommand<CmdLocation>
{
	CmdLocationAdd cmdLocationAdd = new CmdLocationAdd();
	CmdLocationDelete cmdLocationDelete = new CmdLocationDelete();
	CmdLocationList cmdLocationList = new CmdLocationList();
	CmdLocationSet cmdLocationSet = new CmdLocationSet();

	public CmdLocation()
	{
		// SET DEFAULT SETTINGS
		this.addAliases("loc","location");
		this.setPermission("location");
		this.setDescription(WorldTeleport.getInstance().getLangConfiguration().getMessage("Command.WT_LOCATION_DESCRIPTION"));
		
		// ADD CHILD
		this.addChild(this.cmdLocationAdd);
		this.addChild(this.cmdLocationList);
		this.addChild(this.cmdLocationDelete);
		this.addChild(this.cmdLocationSet);
	}
}
