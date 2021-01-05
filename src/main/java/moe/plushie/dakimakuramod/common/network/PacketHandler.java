package moe.plushie.dakimakuramod.common.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import moe.plushie.dakimakuramod.common.lib.LibModInfo;
import moe.plushie.dakimakuramod.common.network.message.client.MessageClientRequestTextures;
import moe.plushie.dakimakuramod.common.network.message.server.MessageServerCommand;
import moe.plushie.dakimakuramod.common.network.message.server.MessageServerSendDakiList;
import moe.plushie.dakimakuramod.common.network.message.server.MessageServerSendTextures;

public class PacketHandler {
    
    public static final SimpleNetworkWrapper NETWORK_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(LibModInfo.CHANNEL);
    private static int packetId = 0;
    
    public static void init() {
        // Server messages.
        registerMessage(MessageServerSendDakiList.class, MessageServerSendDakiList.class, Side.CLIENT);
        registerMessage(MessageServerSendTextures.class, MessageServerSendTextures.class, Side.CLIENT);
        registerMessage(MessageServerCommand.class, MessageServerCommand.class, Side.CLIENT);
        
        // Client messages.
        registerMessage(MessageClientRequestTextures.class, MessageClientRequestTextures.class, Side.SERVER);
    }
    
    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
        NETWORK_WRAPPER.registerMessage(messageHandler, requestMessageType, packetId, side);
        packetId++;
    }
}
