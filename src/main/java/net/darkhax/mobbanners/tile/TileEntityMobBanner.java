package net.darkhax.mobbanners.tile;

import net.darkhax.bookshelf.block.tileentity.TileEntityBasic;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class TileEntityMobBanner extends TileEntityBasic {

    private EntityEggInfo eggInfo = null;
    private EntityEntry entityInfo;

    @Override
    public void writeNBT (NBTTagCompound dataTag) {

        if (this.eggInfo != null) {

            dataTag.setString("MobType", this.getMobInfo().spawnedID.toString());
        }
    }

    @Override
    public void readNBT (NBTTagCompound dataTag) {

        this.setMobInfo(EntityList.ENTITY_EGGS.get(new ResourceLocation(dataTag.getString("MobType"))));
    }

    public void setMobInfo (EntityEggInfo info) {

        this.eggInfo = info;
        this.entityInfo = info == null ? null : ForgeRegistries.ENTITIES.getValue(info.spawnedID);
    }

    public EntityEggInfo getMobInfo () {

        return this.eggInfo;
    }

    public EntityEntry getEntity () {

        return this.entityInfo;
    }
}