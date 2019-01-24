package net.darkhax.mobbanners.block;

import java.util.List;

import javax.annotation.Nullable;

import net.darkhax.bookshelf.util.PlayerUtils;
import net.darkhax.bookshelf.util.StackUtils;
import net.darkhax.mobbanners.BannerTrackingManager;
import net.darkhax.mobbanners.MobBanners;
import net.darkhax.mobbanners.tile.TileEntityMobBanner;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMobBanner extends BlockBannerBase {

    @Override
    public boolean canPlaceBlockOnSide (World worldIn, BlockPos pos, EnumFacing side) {

        return super.canPlaceBlockOnSide(worldIn, pos, side);
    }

    @Override
    public void getSubBlocks (CreativeTabs itemIn, NonNullList<ItemStack> items) {

        for (final EntityEggInfo eggInfo : EntityList.ENTITY_EGGS.values()) {

            items.add(createBannerItem(eggInfo));
        }
    }

    @Override
    public void onBlockPlacedBy (World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {

        if (worldIn.getTileEntity(pos) instanceof TileEntityMobBanner) {

            final TileEntityMobBanner tile = (TileEntityMobBanner) worldIn.getTileEntity(pos);

            if (tile != null) {

                tile.setMobInfo(getInfo(stack));
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity (World worldIn, int meta) {

        return new TileEntityMobBanner();
    }

    @Override
    public ItemStack getStack (IBlockAccess world, BlockPos pos) {

        return world.getTileEntity(pos) instanceof TileEntityMobBanner ? createBannerItem(getInfo(world, pos)) : ItemStack.EMPTY;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation (ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {

        final EntityPlayer player = PlayerUtils.getClientPlayerSP();
        final EntityEggInfo eggInfo = getInfo(stack);

        if (eggInfo != null) {

            if (player != null && BannerTrackingManager.clientData != null) {

                final int kills = BannerTrackingManager.clientData.getKills(eggInfo.spawnedID);

                if (kills > 0) {

                    tooltip.add(I18n.format("tooltip.mobbanners.kills", kills));
                }

                else {

                    tooltip.add(TextFormatting.RED + I18n.format("tooltip.mobbanners.nokills"));
                }
            }

            tooltip.add(I18n.format("entity." + EntityList.getTranslationName(eggInfo.spawnedID) + ".name"));
        }

        else {

            final String rawInfo = getRawInfo(stack);

            if (rawInfo != null) {

                tooltip.add(I18n.format("tooltip.mobbanners.invalid.mob", rawInfo));
            }

            else {

                tooltip.add(I18n.format("tooltip.mobbanners.invalid"));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IBlockColor getColorHandler () {

        return (state, worldIn, pos, tintIndex) -> {

            final EntityEggInfo eggInfo = getInfo(worldIn, pos);
            return eggInfo == null ? 0 : tintIndex == 0 ? eggInfo.primaryColor : eggInfo.secondaryColor;
        };
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IItemColor getItemColorHandler () {

        return (stack, tintIndex) -> {

            final EntityEggInfo eggInfo = getInfo(stack);
            return eggInfo == null ? 0 : tintIndex == 0 ? eggInfo.primaryColor : eggInfo.secondaryColor;
        };
    }

    public static ItemStack createBannerItem (EntityEggInfo eggInfo) {

        if (eggInfo == null) {

            return ItemStack.EMPTY;
        }

        final ItemStack stack = new ItemStack(MobBanners.blockMobBanner);
        StackUtils.prepareStackTag(stack).setString("MobInfo", eggInfo.spawnedID.toString());
        return stack;
    }

    public static EntityEggInfo getInfo (ItemStack stack) {

        if (stack.hasTagCompound()) {

            final EntityEggInfo eggInfo = EntityList.ENTITY_EGGS.get(new ResourceLocation(stack.getTagCompound().getString("MobInfo")));

            if (eggInfo != null) {

                return eggInfo;
            }
        }

        return null;
    }

    public static String getRawInfo (ItemStack stack) {

        final NBTTagCompound tag = stack.getTagCompound();
        return tag != null && tag.hasKey("MobInfo") ? tag.getString("MobInfo") : null;
    }

    public static EntityEggInfo getInfo (IBlockAccess world, BlockPos pos) {

        final TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileEntityMobBanner) {

            return ((TileEntityMobBanner) tile).getMobInfo();
        }

        return null;
    }
}
