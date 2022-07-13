package com.sunchaser.rpc.core.codec;

import com.sunchaser.rpc.core.common.RpcContext;
import com.sunchaser.rpc.core.common.RpcMessageTypeEnum;
import com.sunchaser.rpc.core.protocol.RpcHeader;
import com.sunchaser.rpc.core.protocol.RpcMessage;
import com.sunchaser.rpc.core.serialize.compressor.Compressor;
import com.sunchaser.rpc.core.serialize.factory.CompressorFactory;
import com.sunchaser.rpc.core.serialize.factory.SerializerFactory;
import com.sunchaser.rpc.core.serialize.serializator.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * 编解码
 * +-----------------------------------------------+
 * |  魔数 1byte  |  协议头 1byte  |  协议信息 1byte  |
 * +-----------------------------------------------+
 * |      消息ID 8byte      |     消息长度 4byte     |
 * +-----------------------------------------------+
 * <p>
 * +-----------------------------------------------+
 * |                  协议头 1byte                  |
 * +-----------------------------------------------+
 * |   0   |  1  |  2  |  3  |  4  |  5  |  6 | 7  |
 * +-----------------------------------------------+
 * | 符号位 |           协议版本号          | 请求类型 |
 * +-----------------------------------------------+
 * <p>
 * +-----------------------------------------------+
 * |                 协议信息 1byte                 |
 * +-----------------------------------------------+
 * |  0  |  1  |  2  |  3  |  4  |  5  |  6  |  7  |
 * +-----------------------------------------------+
 * |       序列化方式        |        压缩方式        |
 * +-----------------------------------------------+
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/13
 */
public class RpcCodec<T> extends ByteToMessageCodec<RpcMessage<T>> {

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage<T> msg, ByteBuf out) throws Exception {
        RpcHeader rpcHeader = msg.getRpcHeader();
        byte protocolHeader = rpcHeader.getProtocolHeader();
        byte protocolInfo = rpcHeader.getProtocolInfo();
        out.writeByte(rpcHeader.getMagic());
        out.writeByte(protocolHeader);
        out.writeByte(protocolInfo);
        out.writeLong(rpcHeader.getMessageId());
        out.writeInt(rpcHeader.getLength());
        T content = msg.getContent();
        if (RpcContext.isHeartbeat(protocolHeader)) {// 心跳消息，无消息体
            out.writeInt(0);
            return;
        }
        Serializer serializer = SerializerFactory.getSerializer(protocolInfo);
        Compressor compressor = CompressorFactory.getCompressor(protocolInfo);
        byte[] data = compressor.compress(serializer.serialize(content));
        out.writeInt(data.length);
        out.writeBytes(data);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < RpcContext.HEADER_SIZE) {// 不足消息头长度15字节，暂不读取
            return;
        }
        in.markReaderIndex();
        byte magic = in.readByte();
        if (magic != RpcContext.MAGIC) {
            in.resetReaderIndex();
            throw new IllegalArgumentException("magic: " + magic + " is illegal.");
        }
        byte protocolHeader = in.readByte();
        byte protocolInfo = in.readByte();
        long messageId = in.readLong();
        int length = in.readInt();
        if (in.readableBytes() < length) {
            // 可读的数据长度小于消息体长度，丢弃此次读取并重置读指针位置
            in.resetReaderIndex();
            return;
        }
        byte[] data = new byte[length];
        in.readBytes(data);
        RpcHeader rpcHeader = RpcHeader.builder()
                .magic(magic)
                .protocolHeader(protocolHeader)
                .protocolInfo(protocolInfo)
                .messageId(messageId)
                .length(length)
                .build();
        RpcMessageTypeEnum.match(protocolHeader)
                .invoke(protocolInfo, rpcHeader, data, out);
    }
}
