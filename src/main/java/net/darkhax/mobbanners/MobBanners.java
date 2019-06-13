package net.darkhax.mobbanners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.darkhax.bookshelf.network.NetworkHandler;
import net.darkhax.bookshelf.registry.RegistryHelper;
import net.darkhax.mobbanners.block.BlockMobBanner;
import net.darkhax.mobbanners.packets.PacketSyncBanners;
import net.darkhax.mobbanners.packets.PacketSyncKill;
import net.darkhax.mobbanners.tile.TileEntityMobBanner;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = MobBanners.MOD_ID, name = "Mob Banners", version = "@VERSION@", dependencies = "required-after:bookshelf@[2.3.550,);", certificateFingerprint = "@FINGERPRINT@")
@EventBusSubscriber(modid = MobBanners.MOD_ID)
public class MobBanners {

    public static final Logger LOG = LogManager.getLogger("Mob Banners");
    public static final String MOD_ID = "mobbanners";

    public static final NetworkHandler NETWORK = new NetworkHandler(MOD_ID);
    public static final RegistryHelper REGISTRY = new RegistryHelper(MOD_ID).setTab(CreativeTabs.MISC).enableAutoRegistration();

    public static ConfigurationHandler config;
    public static Block blockMobBanner;

    @EventHandler
    public void preInit (FMLPreInitializationEvent event) {

        config = new ConfigurationHandler(event.getSuggestedConfigurationFile());
        NETWORK.register(PacketSyncKill.class, Side.CLIENT);
        NETWORK.register(PacketSyncBanners.class, Side.CLIENT);

        blockMobBanner = REGISTRY.registerBlock(new BlockMobBanner(), "mob_banner");
        GameRegistry.registerTileEntity(TileEntityMobBanner.class, new ResourceLocation(MOD_ID, "mob_banner"));
    }
}