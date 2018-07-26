package work.ruskonert.minecraft.worldteleport.inventory

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext

import io.github.ruskonert.ruskit.command.misc.Permission
import io.github.ruskonert.ruskit.entity.inventory.InventoryComponent
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import java.lang.reflect.Type

class WorldInventoryComponent : InventoryComponent()
{
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): InventoryComponent {
        return super.deserialize(json, typeOfT, context)
    }

    override fun serialize(src: InventoryComponent?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement
    {
        val parentJson = super.serialize(src, typeOfSrc, context) as JsonObject
        val root = JsonObject()
        val locJson = JsonObject()
        val permissionJson = JsonObject()
        locJson.addProperty("loc-x", this.locX)
        locJson.addProperty("loc-y", this.locY)

        permissionJson.addProperty("default", this.isPermissionDefault)
        permissionJson.addProperty("permission", this.permission!!.getPermissionName())

        root.addProperty("type", this.componentType.toString())
        root.addProperty("world", this.world!!.name)
        root.addProperty("x", this.worldX)
        root.addProperty("y", this.worldY)
        root.addProperty("z", this.worldZ)
        root.addProperty("pitch", this.worldPitch)
        root.addProperty("yaw", this.worldYaw)

        root.add("material", parentJson)
        root.add("location", locJson)
        root.add("permission", permissionJson)
        return root
    }

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
