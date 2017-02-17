package io.github.kunonx.WorldTeleport.command;

import io.github.kunonx.DesignFramework.command.CustomizeCommand;

public class CmdWorldTeleport extends CustomizeCommand<CmdWorldTeleport>
{
	private static CmdWorldTeleport instance = new CmdWorldTeleport();
	public static CmdWorldTeleport getInstance() { return instance; }

	CmdLocation cmdLocation = new CmdLocation();
	CmdConfig swtConfigCommand = new CmdConfig();
	CmdOpenWorld openWorldCommand = new CmdOpenWorld();
	CmdReload cmdReload = new CmdReload();
	CmdAdmin cmdAdmin = new CmdAdmin();
	CmdLocationTeleport cmdTeleport = new CmdLocationTeleport();
	CmdPublicLocationTeleport cmdPublicLocationTeleport = new CmdPublicLocationTeleport();

	public CmdWorldTeleport()
	{
		this.addAliases("wt", "worldteleport");
		this.setPermission("worldteleport.main");
		this.setDescription("WorldTeleport main command");
		this.addChild(this.cmdLocation);
		this.addChild(this.swtConfigCommand);
		this.addChild(this.cmdReload);
		this.addChild(this.cmdAdmin);
		this.addMainExternalCommand(this.openWorldCommand);
		this.addMainExternalCommand(this.cmdTeleport);
		this.addMainExternalCommand(this.cmdPublicLocationTeleport);
	}
}

