package io.github.kunonx.WorldTeleport;

import io.github.kunonx.DesignFramework.message.Msg;
import io.github.kunonx.DesignFramework.plugin.DesignFrameworkPlugin;
import io.github.kunonx.DesignFramework.plugin.config.SyncYamlConfiguration;
import io.github.kunonx.DesignFramework.system.VariableString;
import io.github.kunonx.WorldTeleport.command.CmdOpenWorld;
import io.github.kunonx.WorldTeleport.command.CmdPublicLocationTeleport;
import io.github.kunonx.WorldTeleport.command.CmdLocationTeleport;
import io.github.kunonx.WorldTeleport.command.CmdWorldTeleport;
import io.github.kunonx.WorldTeleport.core.WorldInventoryCore;
import io.github.kunonx.WorldTeleport.core.WorldPlayerCore;
import io.github.kunonx.WorldTeleport.entity.WorldPlayer;
import io.github.kunonx.WorldTeleport.inventory.WorldInventory;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class WorldTeleport extends DesignFrameworkPlugin
{
    private static WorldTeleport instance;
    public static WorldTeleport getInstance() { return instance; }

    private static int INVENTORY_TABLE_SIZE = 0;
    public static int getInventoryTableSize() { return INVENTORY_TABLE_SIZE; }

    private static long DEFAULT_DELAY_TIME = 30L;
    public static long getDelayTime() { return DEFAULT_DELAY_TIME; }

    @Override
    protected void onEnableInner()
    {
        this.startActivation(WorldInventoryCore.class, WorldPlayerCore.class,
        CmdWorldTeleport.class, CmdOpenWorld.class, CmdLocationTeleport.class, CmdPublicLocationTeleport.class);
    }

    @Override
    protected void onEnableFirst()
    {
        saveResource("Custom_Test.yml", false);
    }

    @Override
    protected void preLoad()
    {
        instance = this;
        Msg m = this.getPluginMsg();
        SyncYamlConfiguration yaml = this.getSyncConfig();

        // Check data folders
        this.getSyncConfig().createDataFolders("players", "worlds");
        m.sendToConsole("&eChecking config data...");

        // Main keys
        yaml.fixedDefault("Main.language", this.getLocale());
        yaml.fixedDefault("Main.Refresh_Ticks", 30);
        yaml.fixedDefault("Main.Default_Location_Limit", 5);
        yaml.fixedDefault("Main.Prefix", "&e[&aW&forld&bT&feleport&e] ");
        yaml.fixedDefault("Main.Table_Size", VariableString.getValueFormatted("AUTO"));

        m.sendToConsole("&eVerifying world data...");
        // Generating world data
        for(World w : Bukkit.getServer().getWorlds())
        {
            if(! new File(yaml.getFolder(), "worlds/" + w.getName()).exists())
            {
                this.getPluginMsg().sendToConsole("&3Registering the world data \'&f" + w.getName() + "&a\'...");
                yaml.createDataFolder("worlds/" + w.getName());
            }
            if(! new File(yaml.getFolder(), "worlds/" + w.getName() +"/settings.yml").exists())
            {
                try
                {
                    m.sendToConsole("&3Creating settings file of world \'&f" + w.getName() + "&2\'...");
                    new File(yaml.getFolder(), "worlds/" + w.getName() +"/settings.yml").createNewFile();

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if(! yaml.containsKey("Default." + w.getName()))
            {
                // World data keys
                yaml.fixedDefault("Default." + w.getName() + ".Enabled", true);
                yaml.fixedDefault("Default." + w.getName() + ".Name", VariableString.getValueFormatted("DEFAULT"));
                yaml.fixedDefault("Default." + w.getName() + ".Icon.Amount", VariableString.getValueFormatted("DEFAULT"));
                yaml.fixedDefault("Default." + w.getName() + ".Icon.ID", VariableString.getValueFormatted("DEFAULT"));
                yaml.fixedDefault("Default." + w.getName() + ".Icon.Data", 0);
                yaml.fixedDefault("Default." + w.getName() + ".description", new String[]{});
                yaml.fixedDefault("Default." + w.getName() + ".Permission.Enabled", false);
                yaml.fixedDefault("Default." + w.getName() + ".Permission.Custom", "worldteleport.defaultpass." + w.getName());
                yaml.save();
            }
        }
        // Load default values
        DEFAULT_DELAY_TIME = yaml.getYaml().getLong("Main.Refresh_Ticks");

        if(yaml.getYaml().contains("Main.Table_Size") && yaml.getYaml().getString("Main.Table_Size").equalsIgnoreCase(VariableString.getValueFormatted("AUTO")))
            INVENTORY_TABLE_SIZE = WorldInventory.getAutomaticSize();
        else
            INVENTORY_TABLE_SIZE = yaml.getYaml().getInt("Main.Table_Size");

        if(yaml.getYaml().contains("Main.language"))
        {
            this.setLanguage(yaml.getString("Main.language"));
        }
        this.setPluginMsg(new Msg(this.getSyncConfig().getString("Main", "Prefix")));
    }

    @Override
    public void onDisableInner()
    {
        for(Player p : Bukkit.getOnlinePlayers())
        {
            if(! WorldInventory.hasWorldInventory(p)) continue;
            if(WorldInventory.getPlayerInventory(p).equals(p.getOpenInventory().getTopInventory()))
            {
                p.closeInventory();
                this.getPluginMsg().send(p, this.getLangConfiguration().getMessage("System.UNEXPECT_DISABLED_PLUGIN"));
            }
        }

        WorldInventory.getRegisterInventories().clear();
        WorldPlayer.getRegisterPlayer().clear();
    }

    public static WorldPlayer getWorldPlayer(Object obj)
    {
        if(obj instanceof Player)
            return WorldPlayer.fromPlayer((Player)obj);
        else if(obj instanceof String)
        {
            String s = (String)obj;
            try
            {
                return WorldPlayer.fromPlayer(UUID.fromString(s));
            }
            catch(IllegalArgumentException e)
            {
                return WorldPlayer.fromPlayer(s);
            }
        }
        return null;
    }

    public static WorldInventory getWorldInventory(Object obj)
    {
        if(obj instanceof Player)
            return WorldInventory.getPlayerInventory((Player)obj);
        else if(obj instanceof String)
        {
            String s = (String)obj;
            try
            {
                return WorldInventory.getPlayerInventory(UUID.fromString(s));
            }
            catch(IllegalArgumentException e)
            {
                return WorldInventory.getPlayerInventory(s);
            }
        }
        else if(obj instanceof WorldPlayer)
        {
            return WorldInventory.getPlayerInventory((WorldPlayer)obj);
        }
        return null;
    }
}
