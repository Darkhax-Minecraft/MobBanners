package net.darkhax.mobbanners.tile;

import net.darkhax.bookshelf.block.tileentity.TileEntityBasicTickable;
import net.darkhax.mobbanners.MobBanners;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class TileEntityMobBanner extends TileEntityBasicTickable {

    private EntityEggInfo eggInfo = null;
    private EntityEntry entityInfo;
    private AxisAlignedBB bounds;

    @Override
    public void writeNBT (NBTTagCompound dataTag) {

        if (this.eggInfo != null) {

            dataTag.setString("MobType", this.getMobInfo().spawnedID.toString());
        }
    }

    @Override
    public void onEntityUpdate () {

        if (MobBanners.config.isDebuffNearbyMobs() && this.getWorld().getTotalWorldTime() % 100 == 0) {

            if (this.bounds == null) {

                this.bounds = new AxisAlignedBB(this.getPos()).grow(MobBanners.config.getBannerRange());
            }

            for (final Entity entity : this.getWorld().getEntitiesWithinAABB(this.entityInfo.getEntityClass(), this.bounds)) {

                if (entity instanceof EntityLivingBase) {

                    final EntityLivingBase living = (EntityLivingBase) entity;
                    living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 120));
                    living.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 120));
                }
            }
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