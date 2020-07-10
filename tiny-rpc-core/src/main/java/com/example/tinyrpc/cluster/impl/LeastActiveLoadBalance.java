package com.example.tinyrpc.cluster.impl;

import com.example.tinyrpc.cluster.LoadBalance;
import com.example.tinyrpc.common.domain.URL;
import com.example.tinyrpc.filter.RpcStatus;
import com.example.tinyrpc.protocol.Invoker;

import java.util.List;
import java.util.Random;

/**
 * 基于权重的最小活跃数算法：活跃调用数越小，表明该服务提供者效率越高，单位时间内可处理更多的请求。此时应优先将请求分配给该服务提供者。
 *
 */
public class LeastActiveLoadBalance implements LoadBalance {

    private final Random random = new Random();

    @Override
    public Invoker select(List<Invoker> invokers) {
        int length = invokers.size(); // 总个数
        int leastActive = -1; // 最小的活跃数
        int leastCount = 0; // 相同最小活跃数的个数
        int[] leastIndexs = new int[length]; // 记录相同最小活跃数的下标
        int totalWeight = 0; // 总权重
        int firstWeight = 0; // 第一个权重，用于计算是否相同
        boolean sameWeight = true; // 是否所有权重相同，如果所有权重相同则无差别均等随机
        for (int i = 0; i < length; i++) {
            Invoker invoker = invokers.get(i);
            URL url = invoker.getUrl();
            int active = RpcStatus.getStatus(url).getActive(); // 活跃数
            int weight = invoker.getUrl().getWeight();
            if (leastActive == -1 || active < leastActive) { // 发现更小的活跃数，重新开始
                leastActive = active; // 记录最小活跃数
                leastCount = 1; // 重新统计相同最小活跃数的个数
                leastIndexs[0] = i; // 重新记录最小活跃数下标
                totalWeight = weight; // 重新累计总权重
                firstWeight = weight; // 记录第一个权重
                sameWeight = true; // 还原权重相同标识
            } else if (active == leastActive) { // 累计相同最小的活跃数
                leastIndexs[leastCount ++] = i; // 累计相同最小活跃数下标
                totalWeight += weight; // 累计总权重
                // 判断所有权重是否一样
                if (sameWeight && i > 0
                        && weight != firstWeight) {
                    sameWeight = false;
                }
            }
        }
        if (leastCount == 1) {
            // 如果只有一个最小则直接返回
            return invokers.get(leastIndexs[0]);
        }
        // 在相同最小或约数中按总权重数随机
        if (! sameWeight && totalWeight > 0) {
            // 如果权重不相同且权重大于0则按总权重数随机
            int offsetWeight = random.nextInt(totalWeight);
            // 并确定随机值落在哪个片断上
            for (int i = 0; i < leastCount; i++) {
                int leastIndex = leastIndexs[i];
                offsetWeight -= invokers.get(leastIndex).getUrl().getWeight();
                if (offsetWeight <= 0)
                    return invokers.get(leastIndex);
            }
        }
        // 如果权重相同或权重为0则均等随机
        return invokers.get(leastIndexs[random.nextInt(leastCount)]);
    }
}