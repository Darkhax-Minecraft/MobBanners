package net.darkhax.mobbanners;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;

public class UnlockTracker {

    private static final String TRACKER_TAG = "MobBannerTracker";
    private static final String EVENT_ID_TAG = "Id";
    private static final String KILLS_TAG = "Kills";
    private static final String AWARDED_TAG = "Awarded";

    private final Map<ResourceLocation, Integer> kills;
    private final Map<ResourceLocation, Boolean> awarded;

    public UnlockTracker () {

        this.kills = new HashMap<>();
        this.awarded = new HashMap<>();
    }

    public int getKills (ResourceLocation event) {

        return this.kills.getOrDefault(event, 0);
    }

    public void addKill (ResourceLocation event, int amount) {

        this.setKills(event, this.getKills(event) + amount);
    }

    public void setKills (ResourceLocation event, int amount) {

        this.kills.put(event, amount);
    }

    public boolean isAwarded (ResourceLocation event) {

        return this.awarded.getOrDefault(event, false);
    }

    public void setAwarded (ResourceLocation event, boolean awarded) {

        this.awarded.put(event, awarded);
    }

    /**
     * Saves the data to a specified NBT tag.
     *
     * @param persistTag The tag to save to.
     */
    public void save (NBTTagCompound persistTag) {

        final NBTTagList eventsTag = new NBTTagList();

        for (final Entry<ResourceLocation, Integer> entry : this.kills.entrySet()) {

            final NBTTagCompound currentEvent = new NBTTagCompound();
            currentEvent.setString(EVENT_ID_TAG, entry.getKey().toString());
            currentEvent.setInteger(KILLS_TAG, entry.getValue());
            currentEvent.setBoolean(AWARDED_TAG, this.isAwarded(entry.getKey()));
            eventsTag.appendTag(currentEvent);
        }

        persistTag.setTag(TRACKER_TAG, eventsTag);
    }

    /**
     * Reads data from a specific NBT tag.
     *
     * @param tag The tag to read from.
     */
    public void read (NBTTagCompound tag) {

        final NBTTagList killListTag = tag.getTagList(TRACKER_TAG, NBT.TAG_COMPOUND);

        // Check if kill list exists in tag
        if (killListTag != null) {

            // Iterate tags
            for (int index = 0; index < killListTag.tagCount(); index++) {

                final NBTTagCompound currentKillTag = killListTag.getCompoundTagAt(index);

                // Validate event tag
                if (currentKillTag.hasKey(EVENT_ID_TAG) && currentKillTag.hasKey(KILLS_TAG) && currentKillTag.hasKey(AWARDED_TAG)) {

                    final ResourceLocation id = new ResourceLocation(currentKillTag.getString(EVENT_ID_TAG));

                    // Read kill data from tag.
                    this.kills.put(id, currentKillTag.getInteger(KILLS_TAG));
                    this.awarded.put(id, currentKillTag.getBoolean(AWARDED_TAG));
                }
            }
        }
    }
}