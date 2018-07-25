package work.ruskonert.minecraft.worldteleport.command

import io.github.ruskonert.ruskit.command.plugin.ReloadCommand
import org.bukkit.command.CommandSender
import work.ruskonert.minecraft.worldteleport.WorldTeleport

class WTReloadCommand : ReloadCommand()
{
    override fun perform(sender: CommandSender, argc: Int, argv: List<String>?, handleInstance: Any?): Any? {
        return WorldTeleport.getInstance()!!.reload()
    }
}
