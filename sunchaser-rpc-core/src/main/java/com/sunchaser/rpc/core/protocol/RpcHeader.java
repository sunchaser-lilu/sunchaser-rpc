package com.sunchaser.rpc.core.protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * rpc 消息头
 * +-------------------------------------------------+
 * |  魔数 1byte  |  协议版本 1byte  |  协议信息 1byte  |
 * +-------------------------------------------------+
 * |      序列ID 8byte      |      消息长度 4byte      |
 * +-------------------------------------------------+
 * |               消息体（实际发送的数据）              |
 * +-------------------------------------------------+
 * <p>
 * +-------------------------------------------------+
 * |                  协议版本 1byte                  |
 * +-------------------------------------------------+
 * |   0   |  1  |  2  |  3  |  4  |  5  |  6  |  7  |
 * +-------------------------------------------------+
 * | 符号位 |           协议版本号          |  消息类型  |
 * +-------------------------------------------------+
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
 * @since JDK8 2022/7/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcHeader implements Serializable {

    private static final long serialVersionUID = 3050587176252634022L;

    /**
     * 魔数：1101110
     * 天王盖地虎
     * 宝塔镇河妖
     */
    private byte magic;

    /**
     * 协议头（版本号+消息类型）
     * 消息类型：
     * 00：REQUEST
     * 01：RESPONSE
     * 11：HEARTBEAT
     */
    private byte protocolHeader;

    /**
     * 协议信息（序列化方式+压缩方式）
     */
    private byte protocolInfo;

    /**
     * 序列号ID
     */
    private long sequenceId;

    /**
     * 消息体长度
     */
    private int length;
}
