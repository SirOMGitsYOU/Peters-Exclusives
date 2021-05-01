package xjon.jexclusives.event;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.Gson;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import xjon.jexclusives.util.*;

import java.net.URL;
import java.text.DecimalFormat;

public class PlayerEvents {

    private static int mode = -1;
    private static int downloadsMilestone = -1;
    private static int upToDownloads = -1;
    private static int currentDownloadAmount = -1;
    private static boolean fireworksEnabled = false;
    private static String message = "";

    private static String formatNumber(int number) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        return formatter.format(number);
    }

    public static void fetchData() {
        Thread thread = new Thread(() -> {
            try {
                if (!JEConfiguration.specialLoginsDisabled) {
                    String remoteConfigUrl = JEConfiguration.urlForRemoteConfigs;
                    String technicApiUrl = "http://api.technicpack.net/launcher/version/stable4";

                    if (remoteConfigUrl != null && !remoteConfigUrl.isEmpty()) {
                        if (UrlValidator.isUrlValid(remoteConfigUrl)) {
                            PackJson remoteConfigs = new Gson().fromJson(Resources.toString(new URL(remoteConfigUrl), Charsets.UTF_8), PackJson.class);

                            if (remoteConfigs.enabled) {
                                message = remoteConfigs.message;
                                fireworksEnabled = remoteConfigs.fireworks;
                                mode = remoteConfigs.mode;

                                if (remoteConfigs.mode == 1) {
                                    //Store pack data if the mode is equal to 1
                                    if (!JEConfiguration.customModpackSlug.isEmpty() && UrlValidator.isUrlValid(technicApiUrl)) {
                                        TechnicApiData technicApiData = new Gson().fromJson(Resources.toString(new URL(technicApiUrl), Charsets.UTF_8), TechnicApiData.class);
                                        TechnicModpackApiData packData = new Gson().fromJson(Resources.toString(new URL("http://api.technicpack.net/modpack/" + JEConfiguration.customModpackSlug + "?build=" + technicApiData.build), Charsets.UTF_8), TechnicModpackApiData.class);

                                        currentDownloadAmount = packData.downloads;

                                        //Use backup if needed
                                        //if (currentDownloadAmount == 0 && JEConfiguration.customModpackSlug.equals("the-1710-pack") && UrlValidator.isUrlValid("http://the-1710-pack.com/repo?api=true")) {
                                        //    packData = new Gson().fromJson(Resources.toString(new URL("http://the-1710-pack.com/repo?api=true"), Charsets.UTF_8), TechnicModpackApiData.class);
                                        //    currentDownloadAmount = packData.downloads;
                                        //}

                                        if (currentDownloadAmount >= remoteConfigs.x && currentDownloadAmount <= remoteConfigs.y) {
                                            downloadsMilestone = remoteConfigs.x;
                                            upToDownloads = remoteConfigs.y;
                                        }
                                    } else {
                                        Log.error("Either Technic's API is down, or Jon's Exclusives' remote config's mode is set to 1 (for Technic packs only) while local configs are set to non-Technic packs");
                                    }
                                }
                            }
                        } else {
                            Log.error("Login events were not activated as the remote config URL is invalid");
                        }
                    } else {
                        Log.warn("Login events were not activated as the remote config URL is missing");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        switch (mode) {
            case 0: // basic message
                event.player.sendMessage(new TextComponentString(message));
                Firework.Fireworks(fireworksEnabled, new BlockCoord(event.player), event.player.dimension);
                break;

            case 1: // message (for formatting) + Thank you for x downloads! shows up until y downloads are reached. Will work only if Technic's API works
                if (upToDownloads != -1 && downloadsMilestone != -1 && currentDownloadAmount != -1 && currentDownloadAmount <= upToDownloads) {
                    event.player.sendMessage(new TextComponentString(message + "Thank you for " + formatNumber(downloadsMilestone) + " downloads!"));
                    Firework.Fireworks(fireworksEnabled, new BlockCoord(event.player), event.player.dimension);
                }
                break;

            case 2: // message (for formatting) + player name + Have fun playing!
                event.player.sendMessage(new TextComponentString(message + event.player.getName() + ", have fun playing!"));
                Firework.Fireworks(fireworksEnabled, new BlockCoord(event.player), event.player.dimension);
                break;
            case 3: // combines mode 1 & 2 but automatically picks downloadsMilestone as every X with upToDownloads as += Y (e.g. 100,000 to 115,000)
                if (currentDownloadAmount % downloadsMilestone <= upToDownloads) {
                    int roundedDownloadsMilestone = (currentDownloadAmount / downloadsMilestone) * downloadsMilestone;
                    event.player.sendMessage(new TextComponentString(message + "Thank you for " + formatNumber(roundedDownloadsMilestone) + " downloads!"));
                } else
                    event.player.sendMessage(new TextComponentString(message + event.player.getDisplayName() + ", have fun playing!"));

                Firework.Fireworks(fireworksEnabled, new BlockCoord(event.player), event.player.dimension);
                break;
            default:
                Log.error("Jon's Exclusives' remote config's mode is invalid");
                break;
        }
    }

    public static class PackJson {
        public String capeurl;
        boolean enabled;
        String message;
        boolean fireworks;
        int mode;
        int x;
        int y;
    }

    static class TechnicApiData {
        int build;
    }

    static class TechnicModpackApiData {
        int downloads;
    }

}
