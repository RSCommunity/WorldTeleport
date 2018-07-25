package work.ruskonert.minecraft.worldteleport.command

import io.github.ruskonert.ruskit.command.RuskitCommand

class WorldTeleportCommand : RuskitCommand<WorldTeleportCommand>("worldteleport", "wteleport", "wt")
{
    companion object
    {
        private val instance = WorldTeleportCommand()
        @JvmStatic fun getInstance() : WorldTeleportCommand = instance
    }

    private val wtReloadCommand = WTReloadCommand()
    private val wtPublicCommand = WTPublicCommand()

    init
    {
        this.addChildCommands(wtReloadCommand, wtPublicCommand)
        this.setPermission("worldteleport")
        this.setCommandDescription("The WorldTeleport plugin main command")
    }
}
