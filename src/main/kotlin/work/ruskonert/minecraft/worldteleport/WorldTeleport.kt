package work.ruskonert.minecraft.worldteleport

import io.github.ruskonert.ruskit.plugin.IntegratedPlugin
import io.github.ruskonert.ruskit.plugin.RuskitServerPlugin

import work.ruskonert.minecraft.worldteleport.command.WorldTeleportCommand
import work.ruskonert.minecraft.worldteleport.inventory.WorldInventory
import work.ruskonert.minecraft.worldteleport.engine.WorldInventoryStation

import org.bukkit.Bukkit

class WorldTeleport : IntegratedPlugin()
{
    companion object {
        private var instance : RuskitServerPlugin? = null
        @JvmStatic
        fun getInstance() : RuskitServerPlugin? = instance
    }

    override fun onInit(handleInstance: Any?): Any?
    {
        super.onInit(this)
        this.registerSustainableHandlers(WorldInventoryStation::class.java, WorldTeleportCommand::class.java)
        return true
    }

    override fun unload(handleInstance: Any?) : Any?
    {
        for (p in Bukkit.getOnlinePlayers())
        {
            if (WorldInventory.get(p) == null) continue
            if (WorldInventory.get(p)!!.getInventoryBase() == p.openInventory.topInventory)
            {
                p.closeInventory()
                this.getMessageHandler().sendConfigMessage("System.UNEXPECTED_DISABLED_PLUGIN", p)
            }
        }
        this.unregisterSustainableHandlers(WorldTeleportCommand::class.java, WorldInventoryStation::class.java)
        return true
    }
}