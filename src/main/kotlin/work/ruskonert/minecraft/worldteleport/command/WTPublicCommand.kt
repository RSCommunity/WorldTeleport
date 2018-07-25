package work.ruskonert.minecraft.worldteleport.command

import io.github.ruskonert.ruskit.command.RuskitCommand
import io.github.ruskonert.ruskit.plugin.IntegratedPlugin
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import work.ruskonert.minecraft.worldteleport.WorldTeleport

import work.ruskonert.minecraft.worldteleport.inventory.WorldInventory

class WTPublicCommand : RuskitCommand<WTPublicCommand>("worldinventory", "winventory")
{
    init
    {
        this.setPermission("worldteleport.publicinventory", false)
        this.setCommandDescription("Open a public world inventory")
    }

    override fun perform(sender: CommandSender, argc: Int, argv: List<String>?, handleInstance: Any?): Any? {
        val messageHandler = this.getPlugin()!!.getMessageHandler()
        if(sender is ConsoleCommandSender) {
            messageHandler.defaultMessage("Sorry. This command not supported console.")
            return false
        }
        var worldInventory = WorldInventory.get(sender as Player)
        if(worldInventory == null)
        {
            worldInventory = WorldInventory(sender)
            worldInventory.setEnabled(WorldTeleport.getInstance() as? IntegratedPlugin)
        }
        worldInventory.open()
        return true
    }
}
