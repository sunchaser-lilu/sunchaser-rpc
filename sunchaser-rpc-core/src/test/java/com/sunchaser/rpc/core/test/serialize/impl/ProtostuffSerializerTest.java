package com.sunchaser.rpc.core.test.serialize.impl;

import com.sunchaser.rpc.core.protocol.RpcRequest;
import com.sunchaser.rpc.core.serialize.Serializer;
import com.sunchaser.rpc.core.serialize.impl.ProtostuffSerializer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

/**
 * ProtostuffSerializer Test
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/19
 */
@Slf4j
class ProtostuffSerializerTest {

    private static final RpcRequest REQUEST = RpcRequest.builder()
            .serviceName("A")
            .methodName("b")
            .version("1")
            .argTypes(new Class[]{String.class, null, Integer.class})
            .args(new Object[]{"xxx", null, 666})
            .build();

    @Test
    void serialize() {
        Serializer serializer = new ProtostuffSerializer();
        byte[] serialize = serializer.serialize(REQUEST);
        RpcRequest deserialize = serializer.deserialize(serialize, RpcRequest.class);
        log.info("deserialize: {}", deserialize);
    }
}