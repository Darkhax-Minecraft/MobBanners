package net.darkhax.mobbanners.packets;

import net.darkhax.bookshelf.network.MessageNBT;
import net.darkhax.mobbanners.BannerTrackingManager;
import net.darkhax.mobbanners.UnlockTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncBanners extends MessageNBT {

    public UnlockTracker tracker;

    public PacketSyncBanners () {

        this(new UnlockTracker());
    }

    public PacketSyncBanners (UnlockTracker tracker) {

        this.tracker = tracker;
    }

    @Override
    public IMessage handleMessage (MessageContext context) {

        Minecraft.getMinecraft().addScheduledTask( () -> {

            BannerTrackingManager.clientData = this.tracker;
        });

        return null;
    }

    @Override
    public void read (NBTTagCompound tag) {

        this.tracker.read(tag);
    }

    @Override
    public void write (NBTTagCompound tag) {

        this.tracker.save(tag);
    }
}
