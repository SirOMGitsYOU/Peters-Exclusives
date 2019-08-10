/**
 * DeveloperCapes by Jadar
 * License: MIT License
 * (https://raw.github.com/jadar/DeveloperCapes/master/LICENSE)
 * version 4.0.0.x
 */
package xjon.developercapes.cape;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import xjon.developercapes.DevCapes;
import xjon.developercapes.HDImageBuffer;

/**
 * Default Cape implementation
 * 
 * @author jadar
 */
public class StaticCape extends AbstractCape {

    public StaticCape(String name, URL url) {
        this.setName(name);
        this.setURL(url);
    }

    public StaticCape(String name) {
        this(name, null);
    }

    @Override
    public void loadTexture(AbstractClientPlayer player) {
    	ResourceLocation location = this.getLocation();

    	try {
            NetworkPlayerInfo npi = Minecraft.getMinecraft().getConnection().getPlayerInfo(player.getUniqueID());
            Field plrTxtrsF;
        	try {
        		plrTxtrsF = NetworkPlayerInfo.class.getDeclaredField("playerTextures");
        	}
        	catch(NoSuchFieldException e) {
        		plrTxtrsF = NetworkPlayerInfo.class.getDeclaredField("field_187107_a");
            }
        	plrTxtrsF.setAccessible(true);
        	Map<Type, ResourceLocation> plrTxtrs = (Map<Type, ResourceLocation>) plrTxtrsF.get(npi);
        	plrTxtrs.put(Type.CAPE, location);
        	plrTxtrs.put(Type.ELYTRA, location);
        	plrTxtrsF.setAccessible(false);
        	
        } catch (Exception e) {
            e.printStackTrace();
            DevCapes.logger.error("Setting cape ResourceLocation failed!");
        }

        Minecraft.getMinecraft().renderEngine.loadTexture(location, this.getTexture());
    }

    @Override
    public boolean isTextureLoaded(AbstractClientPlayer player) {
        ResourceLocation cape = player.getLocationCape();
        return cape != null;
    }

    public void setURL(URL url) {
        if (url == null) {
            this.texture = null;
            return;
        }
        this.texture = new ThreadDownloadImageData(null, url.toString(), null, new HDImageBuffer());
    }

    public void setName(String name) {
        this.name = name;
        this.location = new ResourceLocation("DevCapes/" + name);
    }
}