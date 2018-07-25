package work.ruskonert.minecraft.worldteleport.event

import io.github.ruskonert.ruskit.event.inventory.AbstractInventoryEvent
import work.ruskonert.minecraft.worldteleport.inventory.WorldInventory

abstract class WorldInventoryEvent(inventory : WorldInventory) : AbstractInventoryEvent(inventory)
