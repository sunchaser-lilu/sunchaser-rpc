package com.sunchaser.rpc.core.balancer.impl;

import com.sunchaser.rpc.core.balancer.Invoker;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机 & 加权随机
 * 当所有权重都相同时，退化为普通随机。
 *
 * @author sunchaser admin@lilu.org.cn
 * @since JDK8 2022/7/17
 */
public class RandomLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, String routeKey) {
        int length = invokers.size();
        // 每个server的权重是否全部一样，如果一样则退化成简单随机
        boolean sameWeight = true;
        int[] weights = new int[length];
        int totalWeight = 0;
        int lastWeight = -1;
        for (int i = 0; i < length; i++) {
            int weight = invokers.get(i).getWeight();
            totalWeight += weight;
            weights[i] = totalWeight;
            // 出现了server权重不一样的情况
            if (sameWeight && i > 0 && weight != lastWeight) {
                sameWeight = false;
            }
        }
        if (!sameWeight && totalWeight > 0) {
            // 随机0~totalWeight
            int offset = ThreadLocalRandom.current().nextInt(totalWeight);
            for (int i = 0; i < length; i++) {
                // 第一个比offset大的
                if (offset < weights[i]) {
                    return invokers.get(i);
                }
            }
        }
        // all servers have the same weight value, 退化成简单随机算法。
        return invokers.get(ThreadLocalRandom.current().nextInt(length));
    }
}
