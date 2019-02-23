package jdbc;


/**
 * 在正式的项目的代码编写过程中，完全严格按照大公司的编码标准
 * 在代码中，不能出现硬编码字符，不如 ”张三“ ”com.mysql.jdbc.Driver“
 * 所有的这些东西都需要通过常量封装和使用
 */

import conf.ConfigurationManager;
import constant.Constants;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class JDBCHelper {

    // 第一步，在静态代码块中，直接加载数据库的驱动
    // 不要硬编码，com.mysql.jdbc.Driver 只代表了 MySQL数据库的驱动
    // 如果以后数据库要迁移，就要很费劲去代码里找硬编码的地方

    /**
     *  通常，我们都是用一个常量接口中的某个常量，来代表一个值
     *  然后再这个值改变的时候，只要改变常量接口中的常量的值就可以了
     *
     * 项目要尽量做成可以配置的，
     * 这里的数据库驱动，也不只是放在常量接口中，
     * 最好的方式，是放在外部的配置文件中，和代码彻底分离
     * 常量接口中只是包含了这个值对应的 key 的名字
     * */
    static {
        try {
            String driver = ConfigurationManager.getProperty(Constants.JDBC_DRIVER);
            Class.forName(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 第二步，实现JDBCHelper 的单例化
    /**
     * 为何要实现单例化？
     * 它的内部要封装一个内部的数据库连接池，为了保证数据库连接池有且仅有一份
     */

    private static JDBCHelper instance = null;

    public static JDBCHelper getInstance() {
        if (instance == null) {
            synchronized (JDBCHelper.class) {
                if (instance == null) {
                    instance = new JDBCHelper();
                }
            }
        }
        return instance;
    }


    // 数据库连接池
    private LinkedList<Connection> datasource = new LinkedList<>();

    /**
     * 第三步，实现单例的过程中，创建唯一的数据库连接池
     * <p>
     * 私有化构造方法，JDBCHelper 在整个程序运行生命周期中，只会创建一个实例
     * 在这一次创建实例的过程中，就会调用 JDBCHelper() 构造方法
     * 此时，就可以在构造方法中，创建唯一的一个数据库连接池
     */
    private JDBCHelper() {

        // 第一步，获取数据库连接池的大小，可以在配置文件中配置
        int datasourceSize = ConfigurationManager.getInteger(
                Constants.JDBC_DATASOURCE_SIZE);

        String url = ConfigurationManager.getProperty(Constants.JDBC_URL);
        String user = ConfigurationManager.getProperty(Constants.JDBC_USER);
        String password = ConfigurationManager.getProperty(Constants.JDBC_PASSWORD);

        // 创建指定数量的数据库连接，并放入指定的数据库连接池
        for (int i = 0; i < datasourceSize; i++) {
            try {
                Connection conn = DriverManager.getConnection(url, user, password);

                datasource.push(conn);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 第四步，获取数据库连接的方法
     * <p>
     * 有可能去获取的时候，连接被用完了，需要等待
     * 这里实现一个简单的等待机制
     */

    public synchronized Connection getConnection() {
        while (datasource.size() == 0) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return datasource.poll();
    }

    /**
     *  第五步，开发增删改查的方法
     *  1. 执行增删改 SQL 语句的方法
     *  2. 执行查询 SQL 语句的方法
     *  3. 批量执行 SQL 语句的方法
     *
     * */

    /**
     * @param sql
     * @param params
     * @return int 返回的行数
     * @Description: 执行增删改SQL语句
     */
    public int executeUpdate(String sql, Object[] params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int rtn = 0;
        try {

            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            rtn = pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                datasource.push(conn);
            }
        }

        return rtn;
    }

    /**
     * @param sql
     * @param params
     * @param callback
     * @return void
     * @Description: 执行查询SQL语句
     */
    public void executeQuery(String sql, Object[] params, QueryCallback callback) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            rs = pstmt.executeQuery();

            callback.process(rs);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                datasource.push(conn);
            }
        }

    }

    /**
     * @param sql
     * @param paramsList
     * @return int[]  每条SQL语句影响的行数
     * @Description: 批量执行SQL语句
     */
    public  int[] executeBatch(String sql, List<Object[]> paramsList) throws SQLException {

        int[] rtn = null;
        Connection conn = null;
        PreparedStatement pstmt = null;

        conn = getConnection();

        // 第一步，使用 Connection 对象取消自动提交
        conn.setAutoCommit(false);

        pstmt = conn.prepareStatement(sql);

        // 第二步，使用 PreparedStatement.addBatch() 加入批量的SQL参数
        for (Object[] params : paramsList) {

            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }

            pstmt.addBatch();
        }

        // 第三步，执行批量的 SQL语句
        rtn = pstmt.executeBatch();

        // 最后一步，使用Connection对象，提交批量的SQL语句
        conn.commit();

        return rtn;
    }


    /**
     * @Description: 查询回调接口
     */
    public static interface QueryCallback {
        void process(ResultSet rs) throws Exception;
    }


}
