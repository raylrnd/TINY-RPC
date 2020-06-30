package com.example.tinyrpc.proxy;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.protocol.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @auther zhongshunchao
 * @date 19/06/2020 23:35
 */
public class InvokerInvocationHandler implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(InvokerInvocationHandler.class);

    private final Invoker invoker;

    private final Invocation invocation;

    public InvokerInvocationHandler(Invoker invoker, Invocation invocation) {
        this.invoker = invoker;
        this.invocation = invocation;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String methodName = method.getName();
        logger.info("start invoking method {}", methodName);
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            if ("toString".equals(methodName)) {
                return invoker.toString();
            }  else if ("hashCode".equals(methodName)) {
                return invoker.hashCode();
            }
        } else if (parameterTypes.length == 1 && "equals".equals(methodName)) {
            return invoker.equals(args[0]);
        }
        invocation.setMethodName(methodName);
        invocation.setArguments(args);
        invocation.setParameterTypes(parameterTypes);
        return invoker.invoke(invocation);
    }
}
