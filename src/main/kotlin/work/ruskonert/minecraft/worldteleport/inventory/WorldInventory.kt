package work.ruskonert.minecraft.worldteleport.inventory

import io.github.ruskonert.ruskit.config.SynchronizeReader
import io.github.ruskonert.ruskit.entity.AbstractInventory
import io.github.ruskonert.ruskit.entity.SerializableEntity
import io.github.ruskonert.ruskit.entity.inventory.InventoryComponent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*

class WorldInventory : AbstractInventory
{
    override fun serialize(): String
    {
        return ""
    }

    override fun deserialize(): SerializableEntity<AbstractInventory>?
    {
        return null
    }

    constructor(viewer : Player) : super(viewer.displayName)
    {
        this.initialize(5, owner=viewer)
        for(i in 0..45)
        {
            this.setComponent(i, WorldInventoryComponent())
        }
    }

    constructor() : super("public inventory")
    {
        this.initialize(5)
    }

    fun getRefreshTime() : Long = this.delay

    companion object
    {
        fun get(playerUUID: String) : WorldInventory? = get(Bukkit.getPlayer(UUID.fromString(playerUUID)))

        fun get(p : Player) : WorldInventory? {
            if(! wasInitialized(p))
                return null
            else {
                for(value in getRunningInventories()) {
                    if(value.getOwner() == p)
                        return value
                }
                return null
            }
        }

        fun wasInitialized(p : Player) : Boolean
        {
            for(v in getRunningInventories())
            {
                if(v.getOwner() == p)
                    return true
            }
            return false
        }

        fun getRunningInventories() : MutableList<WorldInventory>
        {
            val list = ArrayList<WorldInventory>()
            for(value in SynchronizeReader.RegisterHandledReader().values())
            {
                if(value is WorldInventory)
                {
                    list.add(value)
                }
            }
            return list
        }
    }

    private val teleportFunction = fun(p : Player, item : WorldInventoryComponent) : Boolean {
        if(item.getWorldLocation() == null)
            p.teleport(item.getWorldLocation()!!)
        return true
    }

    private val exitFunction = fun(p : Player) : Boolean {
        p.closeInventory()
        return true
    }

    override fun onInit(handleInstance: Any?): Any?
    {
        super.onInit(this)
        val inventoryBase = this.getInventoryBase()!!
        if(this.getSlotComponents().isNotEmpty()) {
            for (slot in this.getSlotComponents().keys) {
                val item = this.getSlotComponents()[slot]
                if(item is WorldInventoryComponent) {
                    when (item.getComponentType()) {
                        ComponentType.EXIT -> {
                            item.setClick { player: Player -> this.exitFunction(player) }
                        }
                        ComponentType.TELEPORT -> {
                            item.setClick { player: Player -> this.teleportFunction(player, item) }
                        }
                        else -> {
                        }
                    }
                    val slotNumber = InventoryComponent.getSlot(item.getLocationX(), item.getLocationY())
                    val itemStack = item.toItemStack()
                    inventoryBase.setItem(slotNumber, itemStack)
                }
                else
                {
                    val slotNumber = InventoryComponent.getSlot(1, 1)
                    val itemStack = item!!.toItemStack()
                    inventoryBase.setItem(slotNumber, itemStack)
                }
            }
        }
        return inventoryBase

    }
}