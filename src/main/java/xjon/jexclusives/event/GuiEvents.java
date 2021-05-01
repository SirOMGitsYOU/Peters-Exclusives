package xjon.jexclusives.event;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xjon.jexclusives.util.JEConfiguration;
import xjon.jexclusives.util.Log;

import java.net.URI;

public class GuiEvents {
    @SubscribeEvent
    public void open(InitGuiEvent.Post e) {
        if (e.getGui() instanceof GuiMultiplayer && !JEConfiguration.recommendedServerHostButtonDisabled)
            e.getButtonList().add(new GuiButton(18436, e.getGui().width - 105, 5, 100, 20, "\u00A7c\u2726\u00A7r Get a Server"));
    }

    @SubscribeEvent
    public void action(ActionPerformedEvent.Post e) {
        if ((e.getGui() instanceof GuiMultiplayer) && e.getButton().id == 18436) {
            try {
                Class<?> oclass = Class.forName("java.awt.Desktop");
                Object object = oclass.getMethod("getDesktop").invoke(null);
                oclass.getMethod("browse", URI.class).invoke(object, new URI("https://www.bisecthosting.com/VicariousNetwork"));
            } catch (Throwable throwable1) {
                Throwable throwable = throwable1.getCause();
                Log.error("Couldn't open link: " + (throwable == null ? "<UNKNOWN>" : throwable.getMessage()));
            }


        }
    }
}
