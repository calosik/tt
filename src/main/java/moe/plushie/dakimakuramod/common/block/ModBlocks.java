package moe.plushie.dakimakuramod.common.block;

import cpw.mods.fml.common.registry.GameRegistry;
import moe.plushie.dakimakuramod.common.tileentities.TileEntityDakimakura;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class ModBlocks {
    
    public static Block blockDakimakura;
    
    public ModBlocks() {
        blockDakimakura = new BlockDakimakura();
    }
    
    public void registerTileEntities() {
        registerTileEntity(TileEntityDakimakura.class, "dakimakura");
    }

    private void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id) {
        GameRegistry.registerTileEntity(tileEntityClass, "tileentity." + id);
    }
}
