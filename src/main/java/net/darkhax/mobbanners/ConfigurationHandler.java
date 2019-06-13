package net.darkhax.mobbanners;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class ConfigurationHandler {

    /**
     * An instance of Forge's configuration system.
     */
    private final Configuration config;

    private int killsToObtain = 50;

    private boolean debuffNearbyMobs = true;

    private float bannerRange = 16f;

    /**
     * Constructs a configuration handler instance. This should only be done once per run of
     * the game.
     *
     * @param file The configuration file handle suggested by Forge.
     */
    ConfigurationHandler (File file) {

        this.config = new Configuration(file);
        this.syncConfigData();
    }

    /**
     * Initializes the properties used by the configuration handler. This will load the values
     * from the file if one exists, if not the file will be created.
     */
    private void syncConfigData () {

        final String category = Configuration.CATEGORY_GENERAL;

        this.killsToObtain = this.config.getInt("killsToObtain", category, 50, 1, Short.MAX_VALUE, "The amount of kills a player needs to achieve in order to get a banner.");
        this.bannerRange = this.config.getFloat("bannerRange", category, 32f, 0f, Short.MAX_VALUE, "The range of the banner's buff effect. Mobs must be within this range to be affected by the banner.");
        this.debuffNearbyMobs = this.config.getBoolean("debuffNearbyMobs", category, true, "Should mob banners give nearby mobs of the same type a debuff?");

        // Check if the config has had any changes, if so save them.
        if (this.config.hasChanged()) {
            this.config.save();
        }
    }

    public int getKillsToObtain () {

        return this.killsToObtain;
    }

    public float getBannerRange () {

        return this.bannerRange;
    }

    public boolean isDebuffNearbyMobs () {

        return this.debuffNearbyMobs;
    }
}