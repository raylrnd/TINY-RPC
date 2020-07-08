package com.example.tinyrpc.filter;

import com.example.tinyrpc.common.domain.URL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @auther zhongshunchao
 * @date 28/06/2020 14:00
 */
public class RpcStatus {

    private static final Map<String, RpcStatus> METHOD_STATISTICS = new ConcurrentHashMap<>();

    private final AtomicInteger active = new AtomicInteger();

    private RpcStatus() {
    }

    public static RpcStatus getStatus(URL url) {
        String uri = url.toIdentityString();
        return METHOD_STATISTICS.computeIfAbsent(uri, key -> new RpcStatus());
    }

    public int getActive() {
        return active.get();
    }

    public static void removeStatus(URL url) {
        String uri = url.toIdentityString();
        METHOD_STATISTICS.remove(uri);
    }

    public static boolean beginCount(URL url, int max) {
        max = (max <= 0) ? Integer.MAX_VALUE : max;
        RpcStatus methodStatus = getStatus(url);
        if (methodStatus.active.get() == Integer.MAX_VALUE) {
            return false;
        }
        int i;
        while (true) {
            i = methodStatus.active.get();
            if (i + 1 > max) {
                return false;
            }
            if (methodStatus.active.compareAndSet(i, i + 1)) {
                break;
            }
        }
        methodStatus.active.incrementAndGet();
        return true;
    }



}
