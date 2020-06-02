package com.example.tinyrpc.proxy;

import com.example.tinyrpc.common.Invocation;
import com.example.tinyrpc.common.Request;
import com.example.tinyrpc.common.Response;
import com.example.tinyrpc.config.ReferenceConfig;
import com.example.tinyrpc.protocol.MyInvoker;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * @auther zhongshunchao
 * @date 2020/5/21 10:27 上午
 */
public class JdkProxyFactory implements InvocationHandler{

    final private ReferenceConfig referenceConfig;

    public JdkProxyFactory(ReferenceConfig referenceConfig) {
        this.referenceConfig = referenceConfig;
    }

    private Request buildRequest(Method method, Object[] args) {
        Invocation invocation = new Invocation();
        invocation.setClassName(method.getDeclaringClass().getName());
        invocation.setMethodName(method.getName());
        invocation.setParameterTypes(method.getParameterTypes());
        invocation.setParameters(args);
        Request request = new Request(123456789);
        request.setData(invocation);
        request.setIs2way(!referenceConfig.isOneway());
        request.setSerializationId(referenceConfig.getSerializer());
        return request;
    }

    public static Object createProxy(Class<?> interfaceClass, ReferenceConfig referenceConfig) {
        return Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class[]{interfaceClass},
                new JdkProxyFactory(referenceConfig)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?>[] parameterTypes = method.getParameterTypes();
        String[] paramTypes = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            paramTypes[i] = parameterTypes[i].getName();
        }
        if (method.getDeclaringClass() == Object.class) {
            // 定义在 Object 类中的方法（未被子类重写），比如 wait/notify等，直接调用
            return method.invoke(proxy, args);
        }
        // 如果 toString、hashCode 和 equals 等方法被子类重写了，这里也直接调用
        String methodName = method.getName();
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return proxy.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return proxy.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return proxy.equals(args[0]);
        }
        Request request = buildRequest(method, args);
        MyInvoker myInvoker = new MyInvoker();
        //发送Request
        Response response = myInvoker.invoke(request);
        return response;
    }
}
