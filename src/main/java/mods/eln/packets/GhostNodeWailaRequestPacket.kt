package mods.eln.packets

import io.netty.buffer.ByteBuf
import mods.eln.misc.Coordinate
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage

open class GhostNodeWailaRequestPacket : IMessage {
    lateinit var coord: Coordinate

    constructor() {}

    constructor(coord: Coordinate) {
        this.coord = coord
    }

    override fun fromBytes(buf: ByteBuf?) {
        val x = ByteBufUtils.readVarInt(buf, 5)
        val y = ByteBufUtils.readVarInt(buf, 5)
        val z = ByteBufUtils.readVarInt(buf, 5)
        val w = ByteBufUtils.readVarInt(buf, 5)
        coord = Coordinate(x, y, z, w)
    }

    override fun toBytes(buf: ByteBuf?) {
        ByteBufUtils.writeVarInt(buf, coord.pos.x, 5)
        ByteBufUtils.writeVarInt(buf, coord.pos.y, 5)
        ByteBufUtils.writeVarInt(buf, coord.pos.z, 5)
        ByteBufUtils.writeVarInt(buf, coord.dimension, 5)
    }
}
