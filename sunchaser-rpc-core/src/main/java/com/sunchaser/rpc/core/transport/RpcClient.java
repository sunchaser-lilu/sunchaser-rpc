package com.sunchaser.rpc.core.transport;

import com.sunchaser.rpc.core.protocol.RpcProtocol;

/**
 * rpc client interface
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/15
 */
public interface RpcClient<T> {

    void invoke(RpcProtocol<T> rpcProtocol);
}
