package com.example.tinyrpc.proxy;

import javassist.*;

import java.lang.reflect.InvocationHandler;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @auther zhongshunchao
 * @date 05/07/2020 17:54
 */
public class Proxy {

    private static final String PREFIX = "$Proxy";

    private static final AtomicInteger SUFFIX = new AtomicInteger(0);

    protected static InvocationHandler invocationHandler;

    protected Proxy(InvocationHandler invocationHandler) {
        this.invocationHandler = invocationHandler;
    }

    public static Object newProxyInstance(ClassLoader loader, Class<?> targetClass, InvocationHandler h) throws Exception {
        invocationHandler = h;
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get(targetClass.getName());
        CtClass proxyCls = pool.makeClass(generateName(ctClass));
        int methodIndex = 0;
        CtClass[] interfaces = ctClass.getInterfaces();
        //将Proxy类设置成父类   关键！！！
        proxyCls.setSuperclass(pool.get(Proxy.class.getName()));
        for (int i = 0; i < interfaces.length; i++) {
            CtClass ctInter = interfaces[i];
            proxyCls.addInterface(ctInter);
            CtMethod[] methods = ctInter.getDeclaredMethods();
            for (int j = 0; j < methods.length; j++) {
                String fieldSrc = String.format("private java.lang.reflect.Method method%d = " +
                        "Class.forName(\"%s\").getDeclaredMethods()[%d];", methodIndex, ctInter.getName(), i);
                CtField field = CtField.make(fieldSrc, proxyCls);
                proxyCls.addField(field);
                CtMethod ctMethod = methods[i];
                generateMethod(proxyCls, ctMethod, pool, methodIndex);

            }
            methodIndex++;

        }
        //生成构造函数
        generateConstructor(pool, proxyCls);
        // 持久化class到硬盘
        proxyCls.writeFile(Proxy.class.getResource("/").getPath());

        return proxyCls.toClass(loader, null).getConstructor(InvocationHandler.class).newInstance(invocationHandler);
    }

    private static void generateConstructor(ClassPool pool, CtClass proxy) throws NotFoundException, CannotCompileException {
        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{pool.get(InvocationHandler.class.getName())}, proxy);
        String methodBodySrc = String.format("super(%s);", "$1");
        ctConstructor.setBody(methodBodySrc);
        proxy.addConstructor(ctConstructor);
    }

    /**
     * 生成代理方法
     * 横切的实现
     *
     * @param proxyClass
     * @param ctMethod
     * @param pool
     * @param methodIndex
     */
    private static void generateMethod(CtClass proxyClass, CtMethod ctMethod, ClassPool pool, int methodIndex) throws Exception {
        String body = String.format("super.invocationHandler.invoke(this,method%d,$args);", methodIndex);
        CtMethod method = CtNewMethod.make(ctMethod.getModifiers(), ctMethod.getReturnType(),
                ctMethod.getName(), ctMethod.getParameterTypes(), ctMethod.getExceptionTypes(), body, proxyClass);
        proxyClass.addMethod(method);
    }

    /**
     * 生成全限定代理类名
     *
     * @param ctClass
     * @return
     */
    private static String generateName(CtClass ctClass) {
        String packageName = ctClass.getPackageName();
        return packageName + PREFIX + SUFFIX.getAndIncrement();
    }
}