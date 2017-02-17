package io.github.kunonx.WorldTeleport.core;

import io.github.kunonx.DesignFramework.core.Core;
import io.github.kunonx.WorldTeleport.entity.WorldPlayer;

public class WorldPlayerCore extends Core
{
	private static WorldPlayerCore instance = new WorldPlayerCore();
	public static WorldPlayerCore getInstance() { return instance; }
	
	@Override
	public void run()
	{
		for(WorldPlayer p : WorldPlayer.getRegisterPlayer())
		{
			p.refreshLocationData();
		}
	}
} 
