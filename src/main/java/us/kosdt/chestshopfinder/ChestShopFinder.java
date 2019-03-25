package us.kosdt.chestshopfinder;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ChestShopFinder.MODID, name = ChestShopFinder.NAME, version = ChestShopFinder.VERSION)
public class ChestShopFinder
{
    public static final String MODID = "chestshopfinder";
    public static final String NAME = "Chest Shop Finder";
    public static final String VERSION = "1.0.0";

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        KeyInputEventHandler.registerKeyBindings();
    }
}
