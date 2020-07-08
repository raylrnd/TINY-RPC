package com.example.tinyrpc.filter;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @auther zhongshunchao
 * @date 03/07/2020 23:26
 */
public class Span implements Serializable {
    private long traceId;                                   // 一次请求的全局唯一id
    private String spanId;                                    // 调用关系id, 标识一次trace中的某一次rpc调用, 签名方式命名, EG : 0, 0.1, 0.2, 01.1
    private transient AtomicInteger currentSpanNum = new AtomicInteger(0);    // 标识分配到了第几个span, 用于生成调用下游的spanId
    private String spanName;                                   // 调用接口的Class Name + "." + Method Name
    /**
     * 远端的appKey, ip, port 信息
     */
    private String remoteAddress;
    private long start;                                             // 开始的时间
    private long end;                                               // 结束的时间
    private int side;                                // 是client span 还是 server span, 0 client, 1 server
    /**
     * 服务返回状态
     */
    private String status;
    private boolean async = false;                                  // 异步标识

    public Span(long traceId, String remoteAddress, long start, long end, String spanName, int side) {
        this.traceId = traceId;
        this.remoteAddress = remoteAddress;
        this.start = start;
        this.end = end;
        this.spanName = spanName;
        this.side = side;
    }

    public String getSpanId() {
        return spanId;
    }

    public void incSpanId() {
        currentSpanNum.incrementAndGet();
        this.spanId = currentSpanNum.toString();
    }

}
