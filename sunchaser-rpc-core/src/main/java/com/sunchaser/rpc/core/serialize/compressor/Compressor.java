package com.sunchaser.rpc.core.serialize.compressor;

/**
 * 压缩器
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/12
 */
public interface Compressor {

    /**
     * 将数据进行压缩
     *
     * @param data 原比特数组
     * @return 压缩后的数据
     * @throws Exception throw
     */
    byte[] compress(byte[] data) throws Exception;

    /**
     * 将数据解压缩
     *
     * @param data 压缩的数据
     * @return 原数据
     * @throws Exception throw
     */
    byte[] unCompress(byte[] data) throws Exception;
}
