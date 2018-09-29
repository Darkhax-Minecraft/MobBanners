package net.darkhax.mobbanners.packets;

import net.darkhax.bookshelf.network.SerializableMessage;
import net.darkhax.mobbanners.BannerTrackingManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncKill extends SerializableMessage {

    public ResourceLocation id;
    public int kills;
    public boolean unlocked;

    public PacketSyncKill () {

    }

    public PacketSyncKill (ResourceLocation id, int kills, boolean unlocked) {

        this.id = id;
        this.kills = kills;
        this.unlocked = unlocked;
    }

    @Override
    public IMessage handleMessage (MessageContext context) {

        Minecraft.getMinecraft().addScheduledTask( () -> {

            BannerTrackingManager.clientData.setKills(this.id, this.kills);
            BannerTrackingManager.clientData.setAwarded(this.id, this.unlocked);
        });

        return null;
    }
}