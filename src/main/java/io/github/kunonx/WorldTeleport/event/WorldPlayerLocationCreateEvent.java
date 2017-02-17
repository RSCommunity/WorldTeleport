package io.github.kunonx.WorldTeleport.event;

import io.github.kunonx.WorldTeleport.entity.WorldPlayer;
import io.github.kunonx.WorldTeleport.entity.WorldPlayerLocation;

public class WorldPlayerLocationCreateEvent extends WorldPlayerLocationEvent
{
	private WorldPlayer player;
	public void setPlayer(WorldPlayer player) { this.player = player; }
	public WorldPlayer getPlayer() { return this.player; }
	
	public WorldPlayerLocationCreateEvent(WorldPlayer player, WorldPlayerLocation location)
	{
		super(location);
		if(player == null) return;
		this.player = player;
	}
}
