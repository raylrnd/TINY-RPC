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
public class JdkProxyFactory {

    private Class interfaceClass;

    private ReferenceConfig referenceConfig;

    public JdkProxyFactory(Class interfaceClass, ReferenceConfig referenceConfig) {
        this.interfaceClass = interfaceClass;
        this.referenceConfig = referenceConfig;
    }

    public Object invokeProxy(Method method, Object[] args) throws Throwable{
        MyInvoker myInvoker = new MyInvoker();
        Class<?>[] parameterTypes = method.getParameterTypes();
        String[] paramTypes = new String[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            paramTypes[i] = parameterTypes[i].getName();
        }
        if (method.getDeclaringClass() == Object.class) {
            // 定义在 Object 类中的方法（未被子类重写），比如 wait/notify等，直接调用
            return method.invoke(myInvoker, args);
        }
        // 如果 toString、hashCode 和 equals 等方法被子类重写了，这里也直接调用
        String methodName = method.getName();
        if ("toString".equals(methodName) && parameterTypes.length == 0) {
            return myInvoker.toString();
        }
        if ("hashCode".equals(methodName) && parameterTypes.length == 0) {
            return myInvoker.hashCode();
        }
        if ("equals".equals(methodName) && parameterTypes.length == 1) {
            return myInvoker.equals(args[0]);
        }
        Request request = buildRequest(method, args);
        Object result = myInvoker.invoke(request);
        return result;
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

    public Object createProxy() throws Exception{
        return Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[]{interfaceClass},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        return invokeProxy(method, args);
                    }
                }
        );
    }
}
