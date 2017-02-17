package io.github.kunonx.WorldTeleport.inventory;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import io.github.kunonx.DesignFramework.SoundEffect;
import io.github.kunonx.DesignFramework.message.StringUtil;
import io.github.kunonx.DesignFramework.nms.EnchantmentTag;
import io.github.kunonx.DesignFramework.system.GraphicUserInterface;
import io.github.kunonx.DesignFramework.system.VariableString;
import io.github.kunonx.DesignFramework.util.InventoryUtil;

import io.github.kunonx.WorldTeleport.WorldTeleport;
import io.github.kunonx.WorldTeleport.entity.WorldPlayer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * WorldInventory visualizes all the active worlds on the server through the inventory interface. 
 * Synchronization is performed repeatedly and differently depending on the user.
 * If there is no parameter in the object constructor, this is a publicly used WorldInventory and if the type of the parameter is Player, WorldInventory visualizes it to fit player's level.
 * <br>
 * It is also a final class and cannot be inherited from any other class, no reason to override this class.
 * If you want to open a publicly available WorldInventory, Please note:
 * <pre>
 * {@code
 * WorldInventory publicInventory = new WorldInventory();
 * publicInventory.open();
 * }
 * </pre>
 * If you want to create a personal WorldInventory that takes the user's level into consideration, see the following Example Code:
 * <pre>
 * {@code
 * Player player = Bukkit.getPlayer("TestUser");
 * WorldInventory privateInventory = new WorldInventory(player);
 * privateInventory.open();
 * }
 * </pre>
 * WorldInventory enables persistent synchronized by default, <b>which can cause bugs or cause performance degradation depending on your personal server specifications.</b>
 * If you want to disable the synchronization function, please refer to the following method.
 * <pre>
 * {@code
 * WorldInventory privateInventory = new WorldInventory(player);
 * privateInventory.setEnabled(false);
 * }
 * </pre>
 * If the Player wants to see a customized WorldInventory (such as with the name HelloWorld), Just add the parameter value.
 * <pre>
 * {@code
 * WorldInventory privateInventory2 = new WorldInventory(player, "HelloWorld");
 * privateInventory.open();
 * }
 * </pre>
 * You must have a file called<b> Custom_HelloWorld.yml</b> in the data folder, and cannot create a new constructor if the file is missing or incorrect.
 * @version 1.0.0
 * @author kunonx
 * @see #WorldInventory(Player)
 * @see #setEnabled(boolean)
 */
public final class WorldInventory extends GraphicUserInterface
{
	//////////////////
	//              //
	//     DATA     //
	//              //
	//////////////////
	private static transient final Map<Player, WorldInventory> SYNC_INVENTORIES = new HashMap<Player, WorldInventory>();

	public static Map<Player, WorldInventory> getRegisterInventories() { return SYNC_INVENTORIES; }

	public static WorldInventory getPlayerInventory(Player player) { return getRegisterInventories().get(player); }

	public static WorldInventory getPlayerInventory(UUID uuid) { return getRegisterInventories().get(Bukkit.getServer().getPlayer(uuid)); }

	public static WorldInventory getPlayerInventory(String name) { return getRegisterInventories().get(Bukkit.getServer().getPlayer(name)); }

	public static WorldInventory getPlayerInventory(WorldPlayer player) { return getRegisterInventories().get(player.getPlayer()); }

	public static boolean hasWorldInventory(Player player) { return getRegisterInventories().containsKey(player); }

	//////////////////////
	//                  //
	//     SETTINGS     //
	//                  //
	//////////////////////
	private final int INVENTORY_TABLE_SIZE = WorldTeleport.getInventoryTableSize();
	@Override public int getTableSize() { return this.INVENTORY_TABLE_SIZE; }

	private final String CUSTOMIZATION_NAME;
	public boolean isCustomizedInventory() { return ! this.CUSTOMIZATION_NAME.equalsIgnoreCase("DEFAULT") || this.CUSTOMIZATION_NAME == null; }
	public String getCustomizationName() { return this.CUSTOMIZATION_NAME; }
	
	private YamlConfiguration config;
	public YamlConfiguration getConfig() { return this.config; }
	public void setConfig(YamlConfiguration yaml) { this.config = yaml; }
	
	private Map<Integer, SoundEffect> CUSTOM_SOUND_EFFECT = new HashMap<Integer, SoundEffect>();
	
	private final Player VIEWER;
	public Player getPlayer() { return VIEWER; }

	private final Inventory SYNC_INVENTORY;
	public Inventory getInventory() { return this.SYNC_INVENTORY; }
	public synchronized Inventory getSyncInventory() { return this.SYNC_INVENTORY; }
	
	private final Map<Integer, ItemStack> INVENTORY_ITEM_LOCATION;
	@Override public synchronized Map<Integer, ItemStack> getRegisterCategory() { return this.INVENTORY_ITEM_LOCATION; }
	
	private final Map<Integer, World> SLOT_WORLD;
	@Override public Map<Integer, World> getFunctionalizationSlots() { return this.SLOT_WORLD; }

	private final Map<Integer, Location> CUSTOMIZED_LOCATION;
	public Map<Integer, Location> getCustomizedLocation() { return CUSTOMIZED_LOCATION; }

	
	private boolean enabled = true;
	public boolean isEnabled() { return this.enabled; }
	public void setEnabled(boolean enable) { this.enabled = enable; }

	private final Map<Integer, Boolean> isScriptingBlock = new HashMap<Integer, Boolean>();
	public boolean isScripting(int i)
    {
        if(! this.isScriptingBlock.containsKey(i)) return false;
        return this.isScriptingBlock.get(i);
    }
	
	public boolean isScriptingUndefined(int i)
	{
		if(this.isScripting(i))
		{
			return this.scriptCode.get(i).equalsIgnoreCase("UNDEFINED");
		}
		return false;
	}

    private final Map<Integer, String> scriptCode = new HashMap<Integer, String>();
	public String getScript(int i)
    {
        if(! this.scriptCode.containsKey(i)) return null;
        return this.scriptCode.get(i);
    }

	/**
	 * A constructor for WorldInventory that is common to all users.
	 */
	public WorldInventory()
	{
		this(null, "DEFAULT");
	}
	
	/**
	 * The WorldInventory constructor visible to each as user.
	 * @param target The player who joined the game
	 */
	public WorldInventory(Player target) 
	{
		this.CUSTOMIZATION_NAME = "DEFAULT";
		if(target == null)
		{
			this.VIEWER = null;
		}
		else
		{
			this.VIEWER = target;
		}
		this.config = WorldTeleport.getInstance().getSyncConfig().getYaml();
		this.SYNC_INVENTORY = Bukkit.createInventory(null, INVENTORY_TABLE_SIZE * 9, StringUtil.Color(WorldTeleport.getInstance().getLangConfiguration().getMessage("System.TITLE")));
		this.INVENTORY_ITEM_LOCATION = new ConcurrentHashMap<Integer, ItemStack>();
		this.SLOT_WORLD = new HashMap<Integer, World>();
		this.CUSTOMIZED_LOCATION = new HashMap<Integer, Location>();
		WorldInventory.SYNC_INVENTORIES.put(target, this);
		WorldPlayer.initLoad(target);
	}
	
	/**
	 * The WorldInventory constructor visible to different users.
	 * @param target Players joining the game
	 * @param customName The name using to world inventory 
	 */
	public WorldInventory(Player target, String customName)
	{
		if(customName == null) customName = "DEFAULT";
		this.CUSTOMIZATION_NAME = customName;
		if(target == null) throw new IllegalStateException("player cannot be null from customizing world inventory");
		this.VIEWER = target;
		if(! this.isCustomizedInventory())
		{
			this.config = WorldTeleport.getInstance().getSyncConfig().getYaml();
			this.SYNC_INVENTORY = Bukkit.createInventory(null, INVENTORY_TABLE_SIZE * 9, StringUtil.Color(WorldTeleport.getInstance().getLangConfiguration().getMessage("System.TITLE")));
		}
		else
		{
			File file = WorldInventory.getCustomFile(customName);
			if(! WorldInventory.isExistedCustomFile(customName))
			{
				try
				{
					throw new FileNotFoundException(file.getName() + " not found! Is this a valid file?");
				}
				catch(FileNotFoundException e)
				{
					e.printStackTrace();
				}
			}
			this.config = YamlConfiguration.loadConfiguration(file);
			int size = 9;
			if(config.contains("Main.Table_Size"))
				if(config.getString("Main.Table_Size").equalsIgnoreCase(VariableString.getValueFormatted("AUTO")))
					size = WorldInventory.getAutomaticSize();
				else
					size = config.getInt("Main.Table_Size");
			this.SYNC_INVENTORY = Bukkit.createInventory(null, size * 9 , StringUtil.Color(this.config.getString("Main.Title")));
		}
		this.INVENTORY_ITEM_LOCATION = new ConcurrentHashMap<Integer, ItemStack>();
		this.SLOT_WORLD = new HashMap<Integer, World>();
		this.CUSTOMIZED_LOCATION = new HashMap<Integer, Location>();
		WorldInventory.SYNC_INVENTORIES.put(target, this);
		WorldPlayer.initLoad(target);
	}
	
	
	@Override
	protected void Initialized()
	{
		this.INVENTORY_ITEM_LOCATION.clear();
		this.SYNC_INVENTORY.clear();
		this.SLOT_WORLD.clear();
		this.isScriptingBlock.clear();
		this.scriptCode.clear();
		this.CUSTOMIZED_LOCATION.clear();
	}
	
	public void refreshInventoryData()
	{
		this.Initialized();
		YamlConfiguration yc = this.getConfig();
		List<World> worlds = Bukkit.getServer().getWorlds();
		if(! this.isCustomizedInventory())
		{
			for(int i = 0; i < worlds.size(); i++)
			{
				try
				{
					World w = worlds.get(i);
					if(! yc.getBoolean("Default." + w.getName() + ".Enabled")) continue;
					ItemStack itemStack =  this.getDefaultWorldItemStack(w);

					if(yc.contains("Default." + w.getName() + "Icon.Amount"))
					{
						if((yc.get("Default." + w.getName() + ".Icon.Amount")) instanceof String)
						{
							String s = (String) yc.get("Default." + w.getName() + ".Icon.Amount");
							if(s.equalsIgnoreCase(VariableString.getValueFormatted("DEFAULT")))
							{
								int playerAmount = w.getPlayers().size();
								if(playerAmount > 64) playerAmount = 64;
								itemStack.setAmount(playerAmount);
							}
						}
						else if(yc.get("Default." + w.getName() + ".Icon.Amount") instanceof Integer)
						{
							int s = (Integer) yc.get("Default." + w.getName() + ".Icon.Amount");
							
							if(s > 64) s = 64;
							else if(s < 0) s = 1;
							
							itemStack.setAmount(s);
						}
					}
					else
					{
						int playerAmount = w.getPlayers().size();
						if(playerAmount > 64) playerAmount = 64;
						else if(playerAmount <= 0) playerAmount = 1;
						itemStack.setAmount(playerAmount);
					}
					if(this.VIEWER != null)
						if(this.isEnabledWorldPermission(w) && !this.viewerHasPermission(w))
						{
							itemStack.setType(Material.BARRIER);
						}
					int j;
					for(j = 0; this.INVENTORY_ITEM_LOCATION.containsKey(j); j++) {}
					if(this.VIEWER != null) 
						if(this.VIEWER.getWorld() == w) itemStack = this.addGlow(itemStack);
					this.INVENTORY_ITEM_LOCATION.put(j, itemStack);
					this.SYNC_INVENTORY.setItem(j, this.INVENTORY_ITEM_LOCATION.get(j));
					this.SLOT_WORLD.put(j, w);
				}
				catch(NullPointerException e)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			for(String key : yc.getConfigurationSection("Worlds").getKeys(false))
			{
				try
				{
				    // Check the key is enabled
					if(! yc.getBoolean("Worlds." + key + ".Enabled")) continue;

					// for script block

					if(yc.contains("Worlds." + key + ".ETC"))
                    {
                        ItemStack itemStack = this.getScriptItemStack(key);

                        int slot = InventoryUtil.getSlot(yc.getInt("Worlds." + key + ".LOC-X"), yc.getInt("Worlds." + key + ".LOC-Y"));
	                       if(yc.getString("Worlds." + key + ".ETC").equalsIgnoreCase("Command"))
	                        {
		                        if(yc.contains("Worlds." + key + ".SCRIPT"))
		                        {
		                            this.scriptCode.put(slot, yc.getString("Worlds." + key + ".SCRIPT"));
		                        }
	                        }
	                        else
	                        {
		                        if(yc.contains("Worlds." + key + ".SCRIPT"))
		                        {
		                            this.scriptCode.put(slot, "UNDEFINED");
		                        }
	                        }
                        this.INVENTORY_ITEM_LOCATION.put(slot, itemStack);
                        this.SYNC_INVENTORY.setItem(slot, this.INVENTORY_ITEM_LOCATION.get(slot));
                        this.isScriptingBlock.put(slot, true);
                        continue;
                    }
                    World w = Bukkit.getServer().getWorld(yc.getString("Worlds." + key + ".World"));
                    // for world block
					ItemStack itemStack =  this.getDefaultWorldItemStack(w, key);
					
					if(yc.contains("Worlds." + key + ".Icon.Amount"))
					{
						if((yc.get("Worlds." + key + ".Icon.Amount")) instanceof String)
						{
							String s = (String) yc.get("Worlds." + key + ".Icon.Amount");
							if(s.equalsIgnoreCase(VariableString.getValueFormatted("DEFAULT")))
							{
								int playerAmount = w.getPlayers().size();
								if(playerAmount > 64) playerAmount = 64;
								itemStack.setAmount(playerAmount);
							}
						}
						else if(yc.get("Worlds." + key + ".Icon.Amount") instanceof Integer)
						{
							int s = (Integer) yc.get("Worlds." + key + ".Icon.Amount");
							
							if(s > 64) s = 64;
							else if(s < 0) s = 1;
						}
					}
					else
					{
						if(w.getPlayers().size() > 64)
							itemStack.setAmount(64);
						else
						{
						    if(w.getPlayers().size() <= 1)
							    itemStack.setAmount(1);
						    else
                                itemStack.setAmount(w.getPlayers().size());
						}
					}
					
					if(this.VIEWER != null)
						if(this.isEnabledWorldPermission(w) && ! this.viewerHasPermission(w))
						{
							itemStack.setType(Material.BARRIER);
						}

					int slot = InventoryUtil.getSlot(yc.getInt("Worlds." + key + ".LOC-X"), yc.getInt("Worlds." + key + ".LOC-Y"));
					if(yc.contains("Worlds." + key + ".Location"))
                    {
                        Location location = new Location(
                                w, yc.getDouble("Worlds." + key + ".Location.X"),
                                yc.getDouble("Worlds." + key + ".Location.Y"),
                                yc.getDouble("Worlds." + key + ".Location.Z"),
                                (float)yc.getDouble("Worlds." + key + ".Location.Pitch"),
                                (float)yc.getDouble("Worlds." + key + ".Location.Yaw")
                        );
                        this.CUSTOMIZED_LOCATION.put(slot, location);
                    }
					if(this.VIEWER != null) 
						if(this.VIEWER.getWorld() == w) itemStack = this.addGlow(itemStack);
					this.INVENTORY_ITEM_LOCATION.put(slot, itemStack);
					this.SYNC_INVENTORY.setItem(slot, this.INVENTORY_ITEM_LOCATION.get(slot));
					this.SLOT_WORLD.put(slot, w);
				}
				catch(NullPointerException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public Inventory open()
	{
		if(this.VIEWER == null && this.CUSTOMIZATION_NAME.equalsIgnoreCase("DEFAULT")) throw new RuntimeException("This inventory is public-only. The inventory cannot opened.");
		final WorldInventory inner = this;

		// Prevents errors that prevent WorldInventory from opening when the player is viewing inventory.
		Bukkit.getScheduler().scheduleSyncDelayedTask(WorldTeleport.getInstance(), new Runnable() {
			public void run()
			{
				inner.getPlayer().openInventory(SYNC_INVENTORY);
			}
		}, 1L);
		return SYNC_INVENTORY;
	}
	
	public boolean isEnabledWorldPermission(World world)
	{
		YamlConfiguration yc = WorldTeleport.getInstance().getSyncConfig().getYaml();
        if (yc.contains("Default." + world.getName() + ".Permission.Enabled"))
        {
            return yc.getBoolean("Default." + world.getName() + ".Permission.Enabled");
        }
        else
        {
            return false;
        }
	}
	
	public boolean viewerHasPermission(World world)
	{
		if(this.getPlayer().isOp()) return true;
		YamlConfiguration yc = WorldTeleport.getInstance().getSyncConfig().getYaml();
		if(! this.isEnabledWorldPermission(world)) return true;
		if (yc.contains("Default." + world.getName() + ".Permission.Custom"))
		{
			return this.getPlayer().hasPermission(yc.getString("Default." + world.getName() + ".Permission.Custom"));
		}
		else
		{
			return true;
		}
	}

	private ItemStack getScriptItemStack(String key)
    {
        YamlConfiguration yc = this.getConfig();
        byte data = 0;
        int s = 1;
        Material mat = Material.BEDROCK;
        String type = yc.getString("Worlds." + key + ".ETC");

        if(yc.getString("Worlds." + key + ".Icon.ID").equalsIgnoreCase(VariableString.getValueFormatted("DEFAULT"))) mat = Material.GRASS;
        else if(yc.get("Worlds." + key + ".Icon.ID") instanceof String)
            mat = Material.getMaterial(yc.getString("Worlds." + key + ".Icon.ID"));
        else if(yc.get("Worlds." + key + ".Icon.ID") instanceof Integer)
            mat = Material.getMaterial(yc.getInt("Worlds." + key + ".Icon.ID"));

        if(yc.getInt("Worlds." + key + ".Icon.Data") != 0)
            data = (byte)yc.getInt("Worlds." + key + ".Icon.Data");

        if(yc.contains("Worlds." + key + ".Icon.Amount"))
        {
            if(yc.get("Worlds." + key + ".Icon.Amount") instanceof Integer)
            {
                s = (Integer) yc.get("Worlds." + key + ".Icon.Amount");

                if(s > 64) s = 64;
                else if(s < 0) s = 1;
            }
        }
        ItemStack itemStack = new ItemStack(mat, s, data);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(StringUtil.Color(yc.getString("Worlds." + key + ".Name")));

        List<String> l = yc.getStringList("Worlds." + key + ".description");
        itemMeta.setLore(StringUtil.ColorStringList(l));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
	
	private ItemStack getDefaultWorldItemStack(World world)
	{
		return this.getDefaultWorldItemStack(world, null);
	}
	
	@SuppressWarnings("deprecation")
	private ItemStack getDefaultWorldItemStack(World world, String key)
	{
		YamlConfiguration yc = this.getConfig();
		byte data = 0;
		Material mat = Material.BEDROCK;
		String word = "Default.";
		String keyName = world.getName();
		if(this.isCustomizedInventory())
		{
			word = "Worlds.";
			keyName = key;
		}
		if(yc.getString(word + keyName + ".Icon.ID").equalsIgnoreCase(VariableString.getValueFormatted("DEFAULT"))) mat = Material.GRASS;
		else if(yc.get(word + keyName + ".Icon.ID") instanceof String)
			mat = Material.getMaterial(yc.getString(word + keyName + ".Icon.ID"));
		else if(yc.get(word + keyName + ".Icon.ID") instanceof Integer)
			mat = Material.getMaterial(yc.getInt(word + keyName + ".Icon.ID"));
		
		if(yc.getInt(word + keyName + ".Icon.Data") != 0)
			data = (byte)yc.getInt(word + keyName + ".Icon.Data");
		
		ItemStack i = new ItemStack(mat, 1, data);
		
		ItemMeta im = i.getItemMeta();
		if(this.getPlayer().getLocation().getWorld() == world)
		{
			if(yc.getString(word + keyName + ".Name").equalsIgnoreCase(VariableString.getValueFormatted("DEFAULT")))
				im.setDisplayName(StringUtil.Color("&e" + keyName + "&b [" +
						WorldTeleport.getInstance().getLangConfiguration().getMessage("Words.CURRENTLY_HERE") + "]"));
			else
				im.setDisplayName(StringUtil.Color("&e" + yc.getString(word + keyName + ".Name") + "&b [ " +
						WorldTeleport.getInstance().getLangConfiguration().getMessage("Words.CURRENTLY_HERE") + " ]"));
		}
		else
		{
			if(yc.getString(word + keyName + ".Name").equalsIgnoreCase(VariableString.getValueFormatted("DEFAULT")))
				im.setDisplayName(StringUtil.Color("&e" + keyName));
			else
				im.setDisplayName(StringUtil.Color("&e" + yc.getString(word + keyName + ".Name")));
		}
		
		// change the color code.
		ArrayList<String> s = new ArrayList<String>();
		s.addAll(StringUtil.ColorStringList(yc.getStringList(word + keyName + ".description")));
		
		// replace values
		for(String str : s)
		{
			int num = s.indexOf(str);
			str = VariableString.replaceAll(str, world, this.getPlayer());
			s.set(num, str);
		}
		
		// default descriptions
		if(s.size() == 0)
		{
			s.add(StringUtil.Color("&b" + WorldTeleport.getInstance().getLangConfiguration().getMessage("Words.TIME") + ":&f " + world.getTime()));
			s.add(StringUtil.Color("&e" + WorldTeleport.getInstance().getLangConfiguration().getMessage("Words.USUGE") + ": ") + String.valueOf
					(world.getPlayers().size() / (double)Bukkit.getOnlinePlayers().size() * 100) + "%");
		}
		
		// check has permission
		if(this.VIEWER != null)
			if(this.isEnabledWorldPermission(world) && ! this.viewerHasPermission(world))
			{
				s.add(StringUtil.Color("&4" + WorldTeleport.getInstance().getLangConfiguration().getMessage("System.DENY_PERMISSION")));
				s.add(StringUtil.Color(StringUtil.replaceValue("&4Required: {0}", this.getCustomPermission(world))));
			}
		
		im.setLore(s);
		i.setItemMeta(im);
		return i;
	}
	
	public String getCustomPermission(World world)
	{
		return WorldTeleport.getInstance().getSyncConfig().getString("Default." + world.getName() + ".Permission.Custom");
	}
	
	public World getWorldFromCurrentItem(ItemStack item)
	{
		for(int i : this.getRegisterCategory().keySet())
		{
			if(!(this.getRegisterCategory().get(i) == item))
				continue;
			
			return this.SLOT_WORLD.get(i);
		}
		return null;
	}

	public Location getLocationFromCurrentItem(ItemStack item)
	{
		for(int i : this.getRegisterCategory().keySet())
		{
			if(!(this.getRegisterCategory().get(i) == item)) continue;

			if(CUSTOMIZED_LOCATION.containsKey(i))
			{
				return CUSTOMIZED_LOCATION.get(i);
			}
		}
		return null;
	}


	public ItemStack setWorldItemStackLore(int slot, List<String> l)
	{
		ItemStack i = this.INVENTORY_ITEM_LOCATION.get(slot);
		ItemMeta im = i.getItemMeta();
		im.setLore(l); 
		i.setItemMeta(im);

		this.INVENTORY_ITEM_LOCATION.put(slot, i);
		return i;
		
	}
	
	public static WorldInventory getPublicInventory()
	{
		return new WorldInventory();
	}

	public static int getAutomaticSize()
	{
		int size = Bukkit.getServer().getWorlds().size();
		if(size % 9 == 0)
		{
			return size / 9;
		}
		else
		{
			return (size / 9) + 1;
		}
	}

	private ItemStack addGlow(ItemStack item)
	{
	    /*
	    net.minecraft.server.v1_11_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
	    NBTTagCompound nbt = nmsStack.getTag() == null ? new NBTTagCompound() : nmsStack.getTag();
	    NBTTagList ench = new NBTTagList();
	    nbt.set("ench", ench);
	    nmsStack.setTag(nbt);
	    return CraftItemStack.asCraftMirror(nmsStack);*/
        return EnchantmentTag.hideAttributes(item);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof Inventory)
		{
			Inventory inv = (Inventory) obj;
			return inv.equals(this.getSyncInventory());
		}
		else if(obj instanceof WorldInventory)
		{
			WorldInventory w = ((WorldInventory) obj);
			if(! this.getSyncInventory().getName().equals(w.getSyncInventory().getName())) return false;
			if(! this.getConfig().equals(w.getConfig())) return false;
			for(int i = 0; i < this.getSyncInventory().getSize() * 9; i++)
			{
				if(!(w.getRegisterCategory().get(i) == this.getRegisterCategory().get(i))) return false;
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public int hashCode()
	{
		String s = this.getRegisterCategory().toString() + this.getSyncInventory().getName();
		return s.hashCode();
	}

	public static File getCustomFile(String filename)
	{
		if(! filename.endsWith(".yml")) filename = filename + ".yml";
		return new File(WorldTeleport.getInstance().getDataFolder(), "Custom_" + filename);
	}
	
	public static boolean isExistedCustomFile(String filename)
	{
		return new File(WorldTeleport.getInstance().getDataFolder(), "Custom_" + filename + ".yml").exists();
	}
}
