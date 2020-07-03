package com.example.tinyrpc.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * RpcContext的作用是提供隐式传参，可以让用户已添加Filter的方法参与到整个链路调用逻辑中来，同时可用于链路追踪的场景
 * @auther zhongshunchao
 * @date 03/07/2020 09:00
 */
public class RpcContext {

    private static final ThreadLocal<RpcContext> LOCAL = new ThreadLocal<RpcContext>(){
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    private static final ThreadLocal<RpcContext> SERVER_LOCAL = new ThreadLocal<RpcContext>(){
        @Override
        protected RpcContext initialValue() {
            return new RpcContext();
        }
    };

    private Map<String, String> attachments = new HashMap<>();

    private Invocation invocation;

    private Future future;

    /**
     * get context.
     *
     * @return context
     */
    public static RpcContext getContext() {
        return LOCAL.get();
    }

    /**
     * get server side context.
     *
     * @return server context
     */
    public static RpcContext getServerContext() {
        return SERVER_LOCAL.get();
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    public static void removeServerContext() {
        SERVER_LOCAL.remove();
    }

    public void clearAttachments() {
        this.attachments.clear();
    }

    public static void removeContext() {
        LOCAL.remove();
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }
}
