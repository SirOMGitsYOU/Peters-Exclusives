package xjon.jexclusives;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import xjon.jexclusives.event.ConfigEvents;
import xjon.jexclusives.event.PlayerEvents;
import xjon.jexclusives.proxy.CommonProxy;
import xjon.jexclusives.util.JEConfiguration;
import xjon.jexclusives.util.Reference;

import java.io.File;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY, acceptedMinecraftVersions = Reference.ACCEPTED_VERSIONS)
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

        MinecraftForge.EVENT_BUS.register(new PlayerEvents());
        proxy.registerGuiEvents();
    }

    @EventHandler
    public void Init(FMLInitializationEvent event) {
        PlayerEvents.fetchData();
        MinecraftForge.EVENT_BUS.register(new ConfigEvents());
    }

    @EventHandler
    public void PostInit(FMLPostInitializationEvent event) {
        proxy.initCapes();
    }

}
