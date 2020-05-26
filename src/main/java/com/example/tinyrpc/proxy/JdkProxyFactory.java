package com.example.tinyrpc.proxy;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.protocol.Invoker;
import com.example.tinyrpc.transport.Client;
import com.example.tinyrpc.transport.client.NettyClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 10:27 上午
 */
public class JdkProxyFactory extends AbstractProxyFactory {

    @Override
    public <T> T createProxy(Invoker<T> invoker) {
        return (T) Proxy.newProxyInstance(
                invoker.getInterface().getClassLoader(),
                new Class<?>[]{invoker.getInterface()},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return invokeProxy(invoker, method, args);
                    }
                }
        );
    }

    public Object invokeProxy(Invoker invoker, Method method, Object[] args) throws Throwable{
        Class<?>[] parameterTypes = method.getParameterTypes();
        String[] paramTypes = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            paramTypes[i] = parameterTypes[i].getName();
        }
        if (method.getDeclaringClass() == Object.class) {
            // 定义在 Object 类中的方法（未被子类重写），比如 wait/notify等，直接调用
            return method.invoke(invoker, args);
        }
        // 如果 toString、hashCode 和 equals 等方法被子类重写了，这里也直接调用
        String methodName = method.getName();
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return invoker.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return invoker.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return invoker.equals(args[0]);
        }
        Request request = buildRequest(method, args);
        invoker.invoke(request);
        return request;
    }

    private Request buildRequest(Method method, Object[] args) {
        Invocation invocation = new Invocation();
        invocation.setClassName(method.getDeclaringClass().getName());
        invocation.setMethodName(method.getName());
        invocation.setParameterTypes(method.getParameterTypes());
        invocation.setParameters(args);
        Request request = new Request(UUID.randomUUID().getLeastSignificantBits());
        request.setData(invocation);
        return request;
    }
}
