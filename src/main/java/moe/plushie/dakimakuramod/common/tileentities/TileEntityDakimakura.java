package moe.plushie.dakimakuramod.common.tileentities;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import moe.plushie.dakimakuramod.DakimakuraMod;
import moe.plushie.dakimakuramod.common.block.BlockDakimakura;
import moe.plushie.dakimakuramod.common.config.ConfigHandler;
import moe.plushie.dakimakuramod.common.dakimakura.Daki;
import moe.plushie.dakimakuramod.common.dakimakura.serialize.DakiNbtSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityDakimakura extends TileEntity {
    
    private String packDirName;
    private String dakiDirName;
    private boolean flipped;
    
    public void setDaki(Daki daki) {
        if (daki != null) {
            packDirName = daki.getPackDirectoryName();
            dakiDirName = daki.getDakiDirectoryName();
        } else {
            packDirName = null;
            dakiDirName = null;
        }
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public Daki getDaki() {
        return DakimakuraMod.getProxy().getDakimakuraManager().getDakiFromMap(packDirName, dakiDirName);
    }
    
    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    public boolean isFlipped() {
        return flipped;
    }
    
    public void flip() {
        setFlipped(!flipped);
    }
    
    @Override
    public boolean canUpdate() {
        return false;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(DakiNbtSerializer.TAG_DAKI_PACK_NAME, NBT.TAG_STRING) & compound.hasKey(DakiNbtSerializer.TAG_DAKI_DIR_NAME, NBT.TAG_STRING)) {
            packDirName = compound.getString(DakiNbtSerializer.TAG_DAKI_PACK_NAME);
            dakiDirName = compound.getString(DakiNbtSerializer.TAG_DAKI_DIR_NAME);
        }
        flipped = DakiNbtSerializer.isFlipped(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (packDirName != null & dakiDirName != null) {
            compound.setString(DakiNbtSerializer.TAG_DAKI_PACK_NAME, packDirName);
            compound.setString(DakiNbtSerializer.TAG_DAKI_DIR_NAME, dakiDirName);
        }
        DakiNbtSerializer.setFlipped(compound, flipped);
    }
    
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 5, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
        readFromNBT(packet.func_148857_g());
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public double getMaxRenderDistanceSquared() {
        return ConfigHandler.dakiRenderDist;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if (BlockDakimakura.isStanding(getBlockMetadata())) {
            return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 2, zCoord + 1);
        }
        AxisAlignedBB[] rots = {
                AxisAlignedBB.getBoundingBox(0, 0, -1, 1, 0.28F, 1),
                AxisAlignedBB.getBoundingBox(0, 0, 0, 1, 0.28F, 2),
                AxisAlignedBB.getBoundingBox(-1, 0, 0, 1, 0.28F, 1),
                AxisAlignedBB.getBoundingBox(0, 0, 0, 2, 0.28F, 1)
                };
        
        ForgeDirection rot = BlockDakimakura.getRotation(getBlockMetadata());
        return rots[(rot.ordinal() - 2) & 3].offset(xCoord, yCoord, zCoord);
    }
}
