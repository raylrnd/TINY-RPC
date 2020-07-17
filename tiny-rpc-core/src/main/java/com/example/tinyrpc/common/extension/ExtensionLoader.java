package com.example.tinyrpc.common.extension;

import com.example.tinyrpc.common.domain.Constants;
import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;


/**
 * @auther zhongshunchao
 * @date 27/06/2020 18:07
 * dubbo中的@SPI注解：可以认为是定义默认实现类。如果在@Reference中没有定义，则使用@SPI注解中定义的value。
 * 该类在BeanPostProcessor中首次生成，因此会在Spring IOC 容器初始化时加载扩展点而不是真正的请求调用阶段
 * 先把用户自定义的类全部先加载到内存缓存，如果再缓存中找不到，再使用延迟加载策略加载系统内部定义的加载器。(注意：项目的路径不能包含中文名)
 */
public class ExtensionLoader {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionLoader.class);

    // alias -> Class
    private static final Map<String, Class> EXTERNAL_ALIAS_CLASS_MAP = new HashMap<>();

    // Class -> Instance
    private static final Map<Class, Object> INSTANCE_MAP = new ConcurrentHashMap<>();

    private static List<Filter> defaultClientFilterList = new ArrayList<>();

    private static List<Filter> defaultServerFilterList = new ArrayList<>();

    private static final String [] CLIENT_SIDE_FILTERS = {"consumer-context-filter", "active-limit-filter", "trace-filter"};

    private static final String [] SERVER_SIDE_FILTERS = {"context-filter", "trace-filter"};

    private static volatile ExtensionLoader instance;

    public static ExtensionLoader getExtensionLoader() {
        if (instance == null) {
            synchronized (ExtensionLoader.class) {
                if (instance == null) {
                    instance = new ExtensionLoader();
                }
            }
        }
        return instance;
    }

    // 这里的corePoolSize和maxmumPoolSize是按经验公式设置的。队列长度如果设置过长，会导致调用超时；如果设置过短，会导致大量请求被拒绝。
    private static final ExecutorService DEFAULT_EXECUTOR_SERVICE = new ThreadPoolExecutor(8, 16, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100)
            , new CustomizableThreadFactory("default-business-thread-"), new ThreadPoolExecutor.AbortPolicy());

    // dubbo 源码中采用策略模式获取不同的目录
    private ExtensionLoader() {
        loadExternalDirectory(this.getClass().getClassLoader().getResource(Constants.EXTERNAL_PATH));
        logger.info("Current ALIAS_CLASS_MAP:{}, INSTANCE_MAP:{}", EXTERNAL_ALIAS_CLASS_MAP, INSTANCE_MAP);
        buidDefaultFilterChain();
    }

    private void loadExternalDirectory(java.net.URL parent) {
        if (parent != null) {
            logger.info("start reading Extension Files, under path:" + Constants.EXTERNAL_PATH);
            File dir = new File(parent.getFile());
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    handleFile(file);
                }
                logger.info("Extension configuration file read complete");
            }
        }
    }

    private void handleFile(File file) {
        logger.info("start reading file:{}", file);
        Properties prop = new Properties();
        String interfaceName = file.getName();
        try {
            InputStream input = new FileInputStream(file);
            prop.load(input);
            for (String key : prop.stringPropertyNames()) {
                String implName = prop.getProperty(key);
                Class<?> interfaceClass = Class.forName(interfaceName);
                //检查是否是该接口的实现类
                Class<?> impl = checkImpl(interfaceClass, implName);
                EXTERNAL_ALIAS_CLASS_MAP.putIfAbsent(key, impl);
            }
        } catch (Exception e) {
            logger.error("Fail to load file，filename->" + interfaceName + ", exception:" + e.getMessage());
        }
    }

    private Class<?> checkImpl(Class<?> interfaceClass, String implClassName) throws ClassNotFoundException {
        Class<?> impl = Class.forName(implClassName);
        if (!interfaceClass.isAssignableFrom(impl)) {
            logger.error("实现类{}不是该接口{}的子类", impl, interfaceClass);
            throw new IllegalStateException("实现类{}不是该接口{}的子类");
        }
        return impl;
    }

    //如果map里没有，则通过反射生成
    @SuppressWarnings("unchecked")
    public <T> T getExtension(Class<T> type, String alias) {
        if (INSTANCE_MAP.containsKey(type)) {
            return (T) INSTANCE_MAP.get(type);
        } else if (!StringUtils.isEmpty(alias) && EXTERNAL_ALIAS_CLASS_MAP.containsKey(alias)){
            //从map中取出class反射出instance
            Class<T> clazz = EXTERNAL_ALIAS_CLASS_MAP.get(alias);
            if (clazz == null) {
                logger.error("Fail to get Extension. alias=" + alias);
                throw new BusinessException("Fail to get Extension. alias=" + alias);
            }
            Object instance = INSTANCE_MAP.get(clazz);
            if (instance == null) {
                synchronized (ExtensionLoader.class) {
                    instance = INSTANCE_MAP.get(clazz);
                    if (instance == null) {
                        try {
                            instance = clazz.newInstance();
                            INSTANCE_MAP.put(clazz, instance);
                        } catch (Exception e) {
                            logger.error("Fail to instantiate class " +  clazz.getTypeName(), e);
                        }
                    }
                }
            }
            return (T) INSTANCE_MAP.get(clazz);
        } else {
            return loadInternalExtension(type);
        }
    }

    public <T> T getDefaultExtension(Class<T> type) {
        return getExtension(type, null);
    }

    @SuppressWarnings("unchecked")
    private <T> T loadInternalExtension(Class<T> type) {
        // 获取默认的扩展点别名
        String alias = getSPIValue(type);
        // 加载默认扩展点
        InputStream resourceAsStream = ExtensionLoader.class.getResourceAsStream(Constants.INTERNAL_PATH + type.getTypeName());
        Properties prop = new Properties();
        try {
            prop.load(resourceAsStream);
            for (String key : prop.stringPropertyNames()) {
                if (key.equals(alias)) {
                    String impl = prop.getProperty(key);
                    Class<?> clazz = Class.forName(impl);
                    Object instance = clazz.newInstance();
                    INSTANCE_MAP.put(clazz, instance);
                    return (T) instance;
                }
            }
        } catch (Exception e) {
            logger.error("Fail to instantiate class " +  type.getTypeName(), ".exception:", e);
        }
        return null;
    }
    private  <T> String getSPIValue(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Extension type == null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type (" + type + ") is not an interface!");
        }
        if (!type.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("Extension type (" + type +
                    ") is not an extension, because it is NOT annotated with @" + SPI.class.getSimpleName() + "!");
        }
        final SPI defaultAnnotation = type.getAnnotation(SPI.class);
        String value = defaultAnnotation.value();
        if ((value.trim()).length() <= 0) {
            throw new IllegalArgumentException("Default Extension SPI value == null");
        }
        return value;
    }

    private void loadInternalFilterExtension(String alias, int side) {
        // 加载默认扩展点
        InputStream resourceAsStream = ExtensionLoader.class.getResourceAsStream(Constants.INTERNAL_PATH + Filter.class.getTypeName());
        Properties prop = new Properties();
        try {
            prop.load(resourceAsStream);
            for (String key : prop.stringPropertyNames()) {
                if (key.equals(alias)) {
                    String impl = prop.getProperty(key);
                    Class<?> clazz = Class.forName(impl);
                    Filter instance = (Filter) clazz.newInstance();
                    if (side == Constants.CLIENT_SIDE) {
                        defaultClientFilterList.add(instance);
                    } else {
                        defaultServerFilterList.add(instance);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Fail to instantiate Internal Filter Extension", e);
        }
    }

    private void buidDefaultFilterChain() {
        for (String filter : CLIENT_SIDE_FILTERS) {
            loadInternalFilterExtension(filter, Constants.CLIENT_SIDE);
        }
        for (String filter : SERVER_SIDE_FILTERS) {
            loadInternalFilterExtension(filter, Constants.SERVER_SIDE);
        }
    }
    public List<Filter> buidFilterChain(String[] filters, int side) {
        List<Filter> filterList = new ArrayList<>();
        // 加载默认的Filter Chain
        if (side == Constants.CLIENT_SIDE) {
            filterList.addAll(defaultClientFilterList);
        } else {
            filterList.addAll(defaultServerFilterList);
        }
        // 加载用户自定义的Filter Chain
        if (filters != null) {
            List<Filter> newFilterList = new ArrayList<>();
            for (String filter : filters) {
                Filter newFilter = getExtension(Filter.class, filter);
                newFilterList.add(newFilter);
            }
            filterList.addAll(newFilterList);
        }
        logger.info("###global filterList: " + filterList);
        return filterList;
    }

    public ExecutorService getDefaultExecutor() {
        return DEFAULT_EXECUTOR_SERVICE;
    }
}
