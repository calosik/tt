package moe.plushie.dakimakuramod.common.config;

import java.io.File;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import moe.plushie.dakimakuramod.DakimakuraMod;
import moe.plushie.dakimakuramod.common.UpdateCheck;
import moe.plushie.dakimakuramod.common.lib.LibModInfo;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

public class ConfigHandler {
    
    public static final String CATEGORY_RECIPE = "recipe";
    public static final String CATEGORY_LOOT = "loot";
    public static final String CATEGORY_CLIENT = "client";
    public static final String CATEGORY_SERVER = "server";
    public static final String CATEGORY_OTHER = "other";
    private static final String LANG_KEY_PREFIX = "config." + LibModInfo.ID + ":";
    
    public static Configuration config;
    
    // Recipes
    public static boolean enableRecipe;
    public static boolean useAltRecipe;
    public static boolean enableRecycleRecipe;
    public static boolean enableClearingRecipe;
    
    // Loot
    public static boolean addUnlockToLootChests;
    public static float mobDropChance;
    public static float mobDropLootingBonus;
    
    // Client
    public static int textureMaxSize;
    public static int dakiRenderDist;
    public static boolean checkForUpdates;
    public static int cacheTimeClient;
    
    // Server
    public static int cacheTimeServer;
    
    // Other
    public static String lastVersion;
    public static boolean hasUpdated;
    
    public ConfigHandler(File file) {
        if (config == null) {
            config = new Configuration(file, "1");
            loadConfigFile();
        }
        checkIfUpdated();
        FMLCommonHandler.instance().bus().register(this);
    }
    
    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent eventArgs) {
        if (eventArgs.modID.equals(LibModInfo.ID)) {
            loadConfigFile();
        }
    }
    
    public void checkIfUpdated() {
        String localVersion = LibModInfo.VERSION;
        if (LibModInfo.isDevelopmentVersion()) {
            return;
        }
        try {
            if (UpdateCheck.versionCompare(lastVersion.replaceAll("-", "."), localVersion.replaceAll("-", ".")) < 0) {
                DakimakuraMod.getLogger().info(String.format("Updated from version %s to version %s.", lastVersion, localVersion));
                config.getCategory(CATEGORY_OTHER).get("lastVersion").set(localVersion);
                if (config.hasChanged()) {
                    config.save();
                }
                hasUpdated = true;
            } else {
                hasUpdated = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            hasUpdated = true;
        }
    }

    public void loadConfigFile() {
        loadCategoryCommon();
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            loadCategoryClient(config.getCategory(CATEGORY_CLIENT));
        }
        if (config.hasChanged()) {
            config.save();
        }
    }
    
    private void loadCategoryCommon() {
        loadCategoryRecipe(config.getCategory(CATEGORY_RECIPE));
        loadCategoryLoot(config.getCategory(CATEGORY_LOOT));
        loadCategoryServer(config.getCategory(CATEGORY_SERVER));
        lastVersion = config.getString("lastVersion", CATEGORY_OTHER, "0.0", "Used by the mod to check if it has been updated.");
    }
    
    private void loadCategoryRecipe(ConfigCategory category) {
        enableRecipe = config.getBoolean("enableRecipe", CATEGORY_RECIPE, true,
                "Enable the crafting recipe for dakimakuras.",
                LANG_KEY_PREFIX + "enableRecipe");
        category.get("enableRecipe").setRequiresMcRestart(true);
        
        useAltRecipe = config.getBoolean("useAltRecipe", CATEGORY_RECIPE, false,
                "Changes the crafting recipe from 6 wool to 3 wool and 6 string.",
                LANG_KEY_PREFIX + "useAltRecipe");
        category.get("useAltRecipe").setRequiresMcRestart(true);
        
        enableRecycleRecipe = config.getBoolean("enableRecycleRecipe", CATEGORY_RECIPE, true,
                "Allow getting a new dakimakura design by crafting 2 unwanted designs together.",
                LANG_KEY_PREFIX + "enableRecycleRecipe");
        category.get("enableRecycleRecipe").setRequiresMcRestart(true);
        
        enableClearingRecipe = config.getBoolean("enableClearingRecipe", CATEGORY_RECIPE, true,
                "Allow crafting a dakimakura to clear its design.",
                LANG_KEY_PREFIX + "enableClearingRecipe");
        category.get("enableClearingRecipe").setRequiresMcRestart(true);
    }
    
    private void loadCategoryLoot(ConfigCategory category) {
        addUnlockToLootChests = config.getBoolean("addUnlockToLootChests", CATEGORY_LOOT, false,
                "Add the dakimakura design items to loot chests around the world.",
                LANG_KEY_PREFIX + "addUnlockToLootChests");
        category.get("addUnlockToLootChests").setRequiresMcRestart(true);
        
        mobDropChance = config.getFloat("mobDropChance", CATEGORY_LOOT, 1F, 0F, 100F,
                "Percentage chance of mobs dropping a dakimakura design. 0 disables mob drops.",
                LANG_KEY_PREFIX + "mobDropChance");
        
        mobDropLootingBonus = config.getFloat("mobDropLootingBonus", CATEGORY_LOOT, 1F, 0F, 100F,
                "Extra bonus percentage chance of mobs dropping a dakimakura design for each level of looting.\n"
                + "mobDropChance + (mobDropLootingBonus * lootingLevel)",
                LANG_KEY_PREFIX + "mobDropLootingBonus");
        
    }
    
    private void loadCategoryClient(ConfigCategory category) {
        textureMaxSize = config.getInt("textureMaxSize", CATEGORY_CLIENT, 1024, 32, 8192,
                "Max texture size for dakimakuras.\n"
                + "This will be rounded up to the nearest power of 2.\n"
                + "Will be capped at the GPUs max texture size.",
                LANG_KEY_PREFIX + "textureMaxSize");
        
        dakiRenderDist = config.getInt("dakiRenderDist", CATEGORY_CLIENT, 64, 32, 256,
                "The maximum distance away in blocks dakimakuras will render.",
                LANG_KEY_PREFIX + "dakiRenderDist");
        dakiRenderDist = dakiRenderDist * dakiRenderDist;
        
        checkForUpdates = config.getBoolean("checkForUpdates", CATEGORY_CLIENT, true,
                "Should the mod check for newer versions?",
                LANG_KEY_PREFIX + "checkForUpdates");
        
        cacheTimeClient = config.getInt("cacheTime", CATEGORY_CLIENT, 20, 1, Integer.MAX_VALUE,
                "How long in minutes dakimakuras can be loaded into the clients memory without getting used.\n"
                + "Lower values will reduce memory use while higher values will reduce bandwidth usage.",
                LANG_KEY_PREFIX + "cacheTimeClient");
        category.get("cacheTime").setRequiresMcRestart(true);
    }
    
    private void loadCategoryServer(ConfigCategory category) {
        cacheTimeServer = config.getInt("cacheTime", CATEGORY_SERVER, 30, 1, Integer.MAX_VALUE,
                "How long in minutes dakimakuras can be loaded into the servers memory without getting used.\n"
                + "Lower values will reduce memory use while higher values will reduce disk access.",
                LANG_KEY_PREFIX + "cacheTimeServer");
        category.get("cacheTime").setRequiresMcRestart(true);
    }
}
