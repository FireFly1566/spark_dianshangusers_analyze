package conf;

import org.apache.spark.sql.sources.In;

import java.io.InputStream;
import java.util.Properties;

/**
 * @Description: 配置管理组件
 * 1、 配置管理组件可以复杂，也可以很简单，对于简单的配置和管理组件来说，只要开发一个类，可以在第一次
 * 访问它的时候，就从对应的 properties 文件中，读取该配置项，并提供获取某个配置 key 对应的 value 的方法
 * <p>
 * 2、 如果是特别复杂的配置管理组件，那么可能需要使用一些软件设计模式，比如单例模式，解释器模式
 * 可能需要管理多个不同的 properties ，甚至是 xml 类型的配置文件
 */
public class ConfigurationManager {

    private static Properties prop = new Properties();

    /**
     * @Description: 静态代码块
     *  Java 中每一个类第一次使用的时候，就会被 JVM 中的类加载器从磁盘上的 .class 文件中加载出来，
     *      然后为每个类都会构建一个 Class 对象，就代表了这个类
     *
     *  每个类在第一次加载的时候，都会进行自身的初始化，类初始化会执行哪些操作呢？
     *  由每个类内部的 static{} 构成的静态代码块决定
     *   类第一次使用的时候，就会加载，加载的时候，就会初始化类，初始化类的时候就会执行类的静态代码块
     *
     *   对于配置管理组件，就在静态代码块中，编写读取配置文件的代码
     *   这样，第一次外界代码调用这个 ConfigurationManager 类的静态方法的时候，就会加载配置文具中的数据
     *
     *   好处：类的初始化在整个 JVM 生命周期内，有且仅有一次，也就是配置文件只会加载一次，以后就是重复使用，
     *          效率高，不用反复加载多次
     */
    static {
        try {
            // 通过一个 类名.class 的方式，就可以获取到这个类在 JVM 中对应的 Class 对象
            // 然后再通过这个 Class 对象的 getClassLoader() 方法，可以获得当初加载这个类的 JVM
            // 中的类加载器ClassLoader,然后用 ClassLoader 的 getResourceAsStream() 方法去加载
            // 类加载路径中的指定的文件
            InputStream in = ConfigurationManager.class
                    .getClassLoader().getResourceAsStream("my.properties");

            // 将文件中的符合 “key=value” 格式的配置项，都加载到 Properties对象中
            prop.load(in);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param key
     * @return java.lang.String
     * @Description: 获取指定 key 对应的 value
     */
    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

    public static Integer getInteger(String key){
        String value = getProperty(key);

        try{
            return Integer.valueOf(value);
        }catch (Exception e){
            e.printStackTrace();
        }

        return 0;
    }

}
