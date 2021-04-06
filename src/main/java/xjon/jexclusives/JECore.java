package xjon.jexclusives;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.MinecraftForge;
import xjon.jexclusives.event.ConfigEvents;
import xjon.jexclusives.event.GuiEvents;
import xjon.jexclusives.event.PlayerEvents;
import xjon.jexclusives.proxy.CommonProxy;
import xjon.jexclusives.util.JEConfiguration;
import xjon.jexclusives.util.Reference;

import java.io.File;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY)
public class JECore {

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    public static CommonProxy proxy;

    public static Configuration config;

    @Instance(Reference.MOD_ID)
    public static JECore instance;

    @EventHandler
    public void PreInit(FMLPreInitializationEvent event) {
        config = new Configuration(new File("config/JonsExclusives.cfg"));
        config.load();
        JEConfiguration.syncConfig();
        FMLCommonHandler.instance().bus().register(new PlayerEvents());
        MinecraftForge.EVENT_BUS.register(new GuiEvents());
    }

    @EventHandler
    public void Init(FMLInitializationEvent event) {
        PlayerEvents.fetchData();
        FMLCommonHandler.instance().bus().register(new ConfigEvents());
    }

    @EventHandler
    public void PostInit(FMLPostInitializationEvent event) {
        proxy.initCapes();
    }

}
