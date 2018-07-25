package work.ruskonert.minecraft.worldteleport.event

import org.bukkit.entity.Player
import work.ruskonert.minecraft.worldteleport.inventory.WorldInventory
import work.ruskonert.minecraft.worldteleport.inventory.WorldInventoryComponent

class WorldInventoryClickEvent(inventory : WorldInventory, var executor: Player, var slot : Int, var clicked : WorldInventoryComponent?) : WorldInventoryEvent(inventory)