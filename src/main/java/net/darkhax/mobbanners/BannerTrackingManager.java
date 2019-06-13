package net.darkhax.mobbanners;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.darkhax.mobbanners.block.BlockMobBanner;
import net.darkhax.mobbanners.packets.PacketSyncBanners;
import net.darkhax.mobbanners.packets.PacketSyncKill;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

@EventBusSubscriber
public class BannerTrackingManager {

    private static final Map<UUID, UnlockTracker> GLOBAL_BANNER_DATA = new HashMap<>();

    @SideOnly(Side.CLIENT)
    public static UnlockTracker clientData;

    @SubscribeEvent
    public static void onMobKill (LivingDeathEvent event) {

        // If the killer is a player
        if (event.getSource() != null && event.getSource().getTrueSource() instanceof EntityPlayerMP) {

            final EntityPlayerMP player = (EntityPlayerMP) event.getSource().getTrueSource();
            final ResourceLocation entityId = EntityList.getKey(event.getEntity());

            // If the entity is not null, and in the egg list.
            if (entityId != null && EntityList.ENTITY_EGGS.containsKey(entityId)) {

                final UnlockTracker tracker = BannerTrackingManager.getPlayerData(player.getPersistentID());

                tracker.addKill(entityId, 1);

                if (tracker.getKills(entityId) >= MobBanners.config.getKillsToObtain() && !tracker.isAwarded(entityId)) {

                    ItemHandlerHelper.giveItemToPlayer(player, BlockMobBanner.createBannerItem(EntityList.ENTITY_EGGS.get(entityId)));
                    tracker.setAwarded(entityId, true);
                }

                MobBanners.NETWORK.sendTo(new PacketSyncKill(entityId, tracker.getKills(entityId), tracker.isAwarded(entityId)), player);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoad (PlayerEvent.LoadFromFile event) {

        final File playerFile = getPlayerFile(event.getPlayerDirectory(), event.getPlayerUUID());
        final UnlockTracker playerData = new UnlockTracker();

        if (playerFile.exists()) {

            try {

                final NBTTagCompound tag = CompressedStreamTools.read(playerFile);
                playerData.read(tag);
            }

            catch (final IOException e) {

                MobBanners.LOG.error("Could not read player data for {}.", event.getEntityPlayer().getName());
                MobBanners.LOG.catching(e);
            }
        }

        GLOBAL_BANNER_DATA.put(event.getEntityPlayer().getPersistentID(), playerData);
    }

    @SubscribeEvent
    public static void onPlayerSave (PlayerEvent.SaveToFile event) {

        final UUID playerUUID = event.getEntityPlayer().getPersistentID();

        if (GLOBAL_BANNER_DATA.containsKey(playerUUID)) {

            final UnlockTracker playerData = getPlayerData(playerUUID);
            final File playerFile = getPlayerFile(event.getPlayerDirectory(), event.getPlayerUUID());
            final NBTTagCompound tag = new NBTTagCompound();
            playerData.save(tag);

            if (tag != null) {

                try {

                    CompressedStreamTools.write(tag, playerFile);
                }

                catch (final IOException e) {

                    MobBanners.LOG.error("Could not write player data for {}.", playerFile.getName());
                    MobBanners.LOG.catching(e);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn (PlayerLoggedInEvent event) {

        // When a player connects to the server, sync their client data with the
        // server's data.
        if (event.player instanceof EntityPlayerMP) {

            MobBanners.NETWORK.sendTo(new PacketSyncBanners(getPlayerData(event.player.getPersistentID())), (EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut (PlayerLoggedOutEvent event) {

        if (event.player instanceof EntityPlayerMP) {

            GLOBAL_BANNER_DATA.remove(event.player.getPersistentID());
        }
    }

    public static UnlockTracker getPlayerData (UUID uuid) {

        return GLOBAL_BANNER_DATA.computeIfAbsent(uuid, playerUUID -> new UnlockTracker());
    }

    private static File getPlayerFile (File playerDir, String uuid) {

        final File saveDir = new File(playerDir, "mobbanners");

        if (!saveDir.exists()) {

            saveDir.mkdirs();
        }

        return new File(saveDir, uuid + ".dat");
    }
}