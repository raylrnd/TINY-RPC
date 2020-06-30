package com.example.tinyrpc.common;

import com.example.tinyrpc.common.exception.BusinessException;
import com.example.tinyrpc.filter.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * @auther zhongshunchao
 * @date 27/06/2020 18:07
 * dubbo中的@SPI注解：可以认为是定义默认实现类。如果在@Reference中没有定义，则使用@SPI注解中定义的value。
 * 这里为了简化设计，没有用@SPI，而是在@Reference里定义了默认的扩展点名。
 * 该类在BeanPostProcessor中首次生成，因此会在Spring IOC 容器初始化时加载扩展点而不是真正的请求调用阶段
 * 先把用户自定义的类全部先加载到内存缓存，因为用户既然自定义了一般会用到。如果再缓存中找不到，再使用延迟加载策略加载系统内部定义的加载器。(注意：项目的路径不能包含中文名)
 */
public class ExtensionLoader {

    private static Logger logger = LoggerFactory.getLogger(ExtensionLoader.class);

    // alias -> Class
    private static final Map<String, Class> EXTERNAL_ALIAS_CLASS_MAP = new HashMap<>();

    // Class -> Instance
    private static final Map<Class, Object> INSTANCE_MAP = new ConcurrentHashMap<>();

    /**
     * 对 Class.getResourceAsStream() 方法来说，不加”/”表示从当前类路径下查找，加”/”表示从classpath的根路径下查找
     */
    private static final String INTERNAL_LOAD = "/META-INF/TINY-RPC/internal/";

    private static final String EXTERNAL_LOAD = "META-INF/TINY-RPC";

    private static List<Filter> defaultFilterList = new ArrayList<>();

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
        // 通过类加载器读取目录下所有文件并读取
//        loadDirectory(ExtensionLoader.class.getResource(INTERNAL_LOAD));
        loadExternalDirectory(this.getClass().getClassLoader().getResource(EXTERNAL_LOAD));
//        loadDirectory(ExtensionLoader.class.getClassLoader().getResource(EXTERNAL_LOAD));
        logger.info("Current ALIAS_CLASS_MAP:{}, INSTANCE_MAP:{}", EXTERNAL_ALIAS_CLASS_MAP, INSTANCE_MAP);
    }

    private void loadInternalDirectory(InputStream resourceAsStream) {
        Properties prop = new Properties();
        try {
            prop.load(resourceAsStream);
            for (String key : prop.stringPropertyNames()) {
                System.out.println(key + ":" + prop.getProperty(key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadExternalDirectory(java.net.URL parent) {
        if (parent != null) {
            logger.info("start reading Extension Files, under path:" + EXTERNAL_LOAD);
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
        String interfaceName = file.getName();
            //根据文件名找到对应的Class对象
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                String[] kv = line.split("=");
                if (kv.length != 2) {
                    throw new BusinessException("Configuration pattern is not as 'x=y', filename:" + interfaceName + "line at:" + line);
                }
                Class<?> interfaceClass = Class.forName(interfaceName);
                //检查是否是该接口的实现类
                Class<?> impl = checkImpl(interfaceClass, kv[1]);
                EXTERNAL_ALIAS_CLASS_MAP.putIfAbsent(kv[0], impl);
                //加载所有的Filters
                if (impl != null && interfaceClass.equals(Filter.class)) {
                    Filter instance = (Filter) impl.newInstance();
                    defaultFilterList.add(instance);
                }
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
    public <T> T getExtension(Class<T> type, String alias) {
        if (INSTANCE_MAP.containsKey(type)) {
            return (T) INSTANCE_MAP.get(type);
        } else if (EXTERNAL_ALIAS_CLASS_MAP.containsKey(alias)){
            //从map中取出class反射出instance
            Class<T> clazz = EXTERNAL_ALIAS_CLASS_MAP.get(alias);
            if (clazz == null) {
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
                            logger.error("Fail to instantiate class " +  clazz.getCanonicalName(), ".exception:" + e.getMessage());
                        }
                    }
                }
            }
            return (T) INSTANCE_MAP.get(clazz);
        } else {
            // 加载默认扩展点
            InputStream resourceAsStream = ExtensionLoader.class.getResourceAsStream(INTERNAL_LOAD + type.getTypeName());
            Properties prop = new Properties();
            try {
                prop.load(resourceAsStream);
                for (String key : prop.stringPropertyNames()) {
                    if (key.equals(alias)) {
                        String impl = prop.getProperty(key);
                        Class<?> clazz = Class.forName(impl);
                        Object instance = clazz.newInstance();
                        INSTANCE_MAP.put(clazz, ExtensionLoader.instance);
                        return (T) instance;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    public List<Filter> buidFilterChain(String[] filters) {
        List<Filter> filterList = defaultFilterList;
        List<Filter> newFilterList = new ArrayList<>();
        for (String filter : filters) {
            Filter newFilter = getExtension(Filter.class, filter);
            newFilterList.add(newFilter);
        }
        filterList.addAll(newFilterList);
        return filterList;
    }

    public ExecutorService getDefaultExecutor() {
        return DEFAULT_EXECUTOR_SERVICE;
    }
}
