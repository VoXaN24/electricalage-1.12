package mods.eln;

import mods.eln.misc.Coordinate;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import io.netty.channel.ChannelHandler.Sharable;
import mods.eln.client.ClientKeyHandler;
import mods.eln.client.ClientProxy;
import mods.eln.misc.Utils;
import mods.eln.node.INodeEntity;
import mods.eln.node.NodeBase;
import mods.eln.node.NodeManager;
import mods.eln.server.PlayerManager;
import mods.eln.sound.SoundClient;
import mods.eln.sound.SoundCommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.tileentity.TileEntity;

import java.io.*;

@Sharable
public class PacketHandler {

    public PacketHandler() {
        Eln.eventChannel.register(this);
    }


    @SubscribeEvent
    public void onServerPacket(ServerCustomPacketEvent event) {
        FMLProxyPacket packet = event.getPacket();
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(packet.payload().array()));
        NetworkManager manager = event.getManager();
        EntityPlayer player = ((NetHandlerPlayServer) event.getHandler()).player; // EntityPlayerMP

        packetRx(stream, manager, player);
    }


    public void packetRx(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        try {
            switch (stream.readByte()) {
                case Eln.packetPlayerKey:
                    packetPlayerKey(stream, manager, player);
                    break;
                case Eln.packetNodeSingleSerialized:
                    packetNodeSingleSerialized(stream, manager, player);
                    break;
                case Eln.packetPublishForNode:
                    packetForNode(stream, manager, player);
                    break;
                case Eln.packetForClientNode:
                    packetForClientNode(stream, manager, player);
                    break;
                case Eln.packetOpenLocalGui:
                    packetOpenLocalGui(stream, manager, player);
                    break;
                case Eln.packetPlaySound:
                    packetPlaySound(stream, manager, player);
                    break;
                case Eln.packetDestroyUuid:
                    packetDestroyUuid(stream, manager, player);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void packetDestroyUuid(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        try {
            ClientProxy.uuidManager.kill(stream.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void packetPlaySound(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        try {
            if (stream.readByte() != player.dimension)
                return;
            SoundClient.play(SoundCommand.fromStream(stream, player.world));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void packetOpenLocalGui(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        EntityPlayer clientPlayer = (EntityPlayer) player;
        try {
            clientPlayer.openGui(Eln.Companion, stream.readInt(),
                clientPlayer.world, stream.readInt(), stream.readInt(),
                stream.readInt());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void packetForNode(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        try {
            Coordinate coordinate = new Coordinate(stream.readInt(),
                stream.readInt(), stream.readInt(), stream.readByte());

            NodeBase node = NodeManager.instance.getNodeFromCoordinate(coordinate);
            if (node != null && node.getNodeUuid().equals(stream.readUTF())) {
                node.networkUnserialize(stream, (EntityPlayerMP) player);
            } else {
                Utils.println("packetForNode node found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void packetForClientNode(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        EntityPlayer clientPlayer = (EntityPlayer) player;
        int x, y, z, dimension;
        try {

            x = stream.readInt();
            y = stream.readInt();
            z = stream.readInt();
            dimension = stream.readByte();


            if (clientPlayer.dimension == dimension) {
                TileEntity entity = clientPlayer.world.getTileEntity(new BlockPos(x,y,z));
                if (entity != null && entity instanceof INodeEntity) {
                    INodeEntity node = (INodeEntity) entity;
                    if (node.getNodeUuid().equals(stream.readUTF())) {
                        node.serverPacketUnserialize(stream);
                        if (0 != stream.available()) {
                            Utils.println("0 != stream.available()");
                        }
                    } else {
                        Utils.println("Wrong node UUID warning");
                        int dataSkipLength = stream.readByte();
                        for (int idx = 0; idx < dataSkipLength; idx++) {
                            stream.readByte();
                        }
                    }
                }
            } else
                Utils.println("No node found for " + x + " " + y + " " + z);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void packetNodeSingleSerialized(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        try {
            EntityPlayer clientPlayer = player;
            int x, y, z, dimension;
            x = stream.readInt();
            y = stream.readInt();
            z = stream.readInt();
            dimension = stream.readByte();

            if (clientPlayer.dimension == dimension) {
                TileEntity entity = clientPlayer.world.getTileEntity(new BlockPos(x,y,z));
                if (entity != null && entity instanceof INodeEntity) {
                    INodeEntity node = (INodeEntity) entity;
                    if (node.getNodeUuid().equals(stream.readUTF())) {
                        node.serverPublishUnserialize(stream);
                        if (0 != stream.available()) {
                            Utils.println("0 != stream.available()");

                        }
                    } else {
                        Utils.println("Wrong node UUID warning");
                        int dataSkipLength = stream.readByte();
                        for (int idx = 0; idx < dataSkipLength; idx++) {
                            stream.readByte();
                        }
                    }
                } else
                    Utils.println("No node found for " + x + " " + y + " " + z);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void packetPlayerKey(DataInputStream stream, NetworkManager manager, EntityPlayer player) {
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        byte id;
        try {
            id = stream.readByte();
            boolean state = stream.readBoolean();

            if (id == ClientKeyHandler.wrenchId) {
                PlayerManager.PlayerMetadata metadata = Eln.playerManager.get(playerMP);
                metadata.setInteractEnable(state);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
