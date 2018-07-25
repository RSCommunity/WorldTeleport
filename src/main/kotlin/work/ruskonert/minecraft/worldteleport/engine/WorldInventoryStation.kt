package work.ruskonert.minecraft.worldteleport.engine

import io.github.ruskonert.ruskit.engine.RuskitThread
import io.github.ruskonert.ruskit.event.inventory.AbstractInventoryClickEvent
import io.github.ruskonert.ruskit.plugin.IntegratedPlugin

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

import work.ruskonert.minecraft.worldteleport.WorldTeleport
import work.ruskonert.minecraft.worldteleport.event.WorldInventoryClickEvent
import work.ruskonert.minecraft.worldteleport.inventory.WorldInventory
import work.ruskonert.minecraft.worldteleport.inventory.WorldInventoryComponent

class WorldInventoryStation : RuskitThread()
{
    companion object {
        private val instance = WorldInventoryStation()
        @JvmStatic
        fun getInstance() : WorldInventoryStation = instance
    }

    override fun onInit(handleInstance: Any?): Any?
    {
        val p : IntegratedPlugin? = if(this.hasActivePlugin())
            this.activePlugin
        else
            IntegratedPlugin.CorePlugin

        for(wi in WorldInventory.getRunningInventories())
        {
            if(wi.isActivated())
            {
                // TODO("implement the code")
            }
        }
        return true
    }

    @EventHandler
    fun onJoin(p : PlayerJoinEvent)
    {
        val player = p.player
        if(! WorldInventory.wasInitialized(player))
        {
            val worldInventory = WorldInventory(player)
            worldInventory.setEnabled(WorldTeleport.getInstance() as? IntegratedPlugin)
        }
    }

    @EventHandler
    fun onLeave(p : PlayerQuitEvent)
    {
        val player = p.player
        if(WorldInventory.wasInitialized(player))
        {
            val worldInventory = WorldInventory.get(player)!!
            worldInventory.setEnabled(false)
        }
    }

    @EventHandler
    fun onClick(e: AbstractInventoryClickEvent)
    {
        if(e.inventory is WorldInventory)
        {
            val event = WorldInventoryClickEvent(e.inventory as WorldInventory, e.executor, e.slot, e.clicked as? WorldInventoryComponent)
            event.run()
            if(! event.isCancelled)
            {
                e.isCancelled = true
                val function = e.clicked!!.getClickFunction()
                if(function != null) function(e.executor)
            }
        }
    }
}