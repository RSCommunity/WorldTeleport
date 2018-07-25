package work.ruskonert.minecraft.worldteleport.inventory

import io.github.ruskonert.ruskit.command.misc.Permission
import io.github.ruskonert.ruskit.entity.inventory.InventoryComponent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World

class WorldInventoryComponent : InventoryComponent()
{
    private var locX : Int = 1
    fun getLocationX() : Int = this.locX

    private var locY : Int = 1
    fun getLocationY() : Int = this.locY

    private var world : World? = Bukkit.getWorld("world")
    fun setWorld(worldName : String) {
        this.world = Bukkit.getWorld(worldName) }

    fun setWorld(world : World) { this.world = world }

    fun getWorld() : World = this.world!!

    private var worldX : Double = 0.0

    private var worldY : Double = 0.0

    private var worldZ : Double = 0.0

    private var worldYaw : Float = 0.0f

    private var worldPitch : Float = 0.0f

    fun getWorldLocation() : Location? { return Location(getWorld(), worldX, worldY, worldZ, worldYaw, worldPitch) }

    private var permission : Permission? = null
    fun serPermission(p : Permission) { this.permission = p }
    fun getPermission() : Permission = this.permission!!

    private var isPermissionDefault : Boolean = true
    fun setDefault(default : Boolean) { this.isPermissionDefault = default }
    fun isDefault() : Boolean = this.isPermissionDefault

    private var componentType : ComponentType = ComponentType.TELEPORT
    fun setComponentType(c : ComponentType) { this.componentType = c }
    fun getComponentType() : ComponentType = this.componentType

    private val commandList : ArrayList<String> = ArrayList()
}

enum class ComponentType
{
    TELEPORT,
    EXIT,
    UNKNOWN
}
