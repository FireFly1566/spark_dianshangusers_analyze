import java.sql.*;

/**
 * @Description: JDBC 增删改查示范类
 */
public class JDBCTest {

    public static void main(String[] args) {
        //insert();
        // update();
        //delete();
        //select();
        preparedStatement();
    }

    private static void insert() {

        // 数据库连接对象
        Connection conn = null;

        // SQL 语句执行句柄：Statement 对象
        // Statement 对象，其实就是底层基于Connection数据库连接
        // 可以让使用者方便针对数据库中的表，执行增删改查的SQL语句
        Statement stmt = null;

        try {
            // 第一步，记载数据库的驱动，面向 java.sql 包下接口的编程
            // 要想让 JDBC 代码能够真正操作数据库，必须第一步先加载要操作的数据库

            /*
             *  Class.forName() 是Java 提供的一种基于反射的方式，直接根据类的全限定名（包+类）
             *  从类所在的磁盘文件（.class文件）中加载类对应的内容，并创建对应的 Class 对象
             * */
            Class.forName("com.mysql.jdbc.Driver");

            // 获取数据库的连接
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/spark_project?useSSL=false",
                    "root",
                    "root");

            // 基于数据库连接对象，创建 SQL语句执行句柄，Statement 对象
            stmt = conn.createStatement();

            String sql = "insert into test_user(name,age) values('张三',18)";

            // 返回值：SQL 语句影响的行数
            int rtn = stmt.executeUpdate(sql);
            System.out.println("SQL语句影响了 " + rtn + " 行");


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static void update() {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/spark_project?useSSL=false",
                    "root",
                    "root");

            stmt = conn.createStatement();

            String sql = "update test_user set age=30 where name='张三'";
            stmt.executeUpdate(sql);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }


    private static void delete() {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/spark_project?useSSL=false",
                    "root",
                    "root");

            stmt = conn.createStatement();

            String sql = "delete from test_user where name='李思'";
            stmt.executeUpdate(sql);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

    }

    /*
     * 测试查询数据
     * */
    private static void select() {
        Connection conn = null;
        Statement stmt = null;

        /*
         * 对于 select 查询语句，需要定义 ResultSet,代表查询出来的数据
         *
         * */
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/spark_project",
                    "root",
                    "root");
            stmt = conn.createStatement();

            String sql = "select * from test_user";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt(1);
                String name = rs.getString(2);
                int age = rs.getInt(3);
                System.out.println("id = " + id + " name = " + name + " age = " + age);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }


    /*
     * 测试 PreparedStatement
     * */
    private static void preparedStatement() {

        Connection conn = null;

        /*
         *  如果使用 Statement ,必须在SQL语句中嵌入值，
         *  但是这种方式有弊端，容易发生SQL注入，
         *
         *SQL注入： 网页的用户，比如留言板，电商网站的评论页面，提交内容的时候
         *可以使用 “1 or 1”，诸如此类的非法字符，然后后台，如果在插入评论数据到
         * 表中的时候，在使用 Statement 时候就会原封不动的将用户填写的内容拼接在SQL中，
         * 此时可能会发生数据库的意外损坏，甚至数据泄露
         *
         * 第二种弊端： 性能低下比如在 insert 时，插入不同的数据，就需要对每一条数据SQL都进行编译，
         * 编译的耗时在整个SQL语句的执行耗时中占据了大部分的比例
         *
         * 使用PreparedStatement，有好处
         * 1. SQL 注入，对值所在的位置使用 ？这种占位符，实际的值，可以通过另外一份放在数组中的参数来代表
         * 此时PreparedStatement会对值做特殊的处理，使得恶意注入的 SQL 代码失效
         *
         * 2. 提升性能，结构类似的SQL 语句，因为值的地方变成 ？，那么一条SQL语句在mysql 中只会编译一次，
         * 后面的SQL语句过来就直接拿编译后的执行计划加上不同的参数直接执行，
         * */
        PreparedStatement pstmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/spark_project?characterEncoding=utf8",
                    "root",
                    "root");


            // 1. SQL语句中值所在的地方，都用？代表
            String sql = "insert into test_user(name,age) values(?,?)";
            pstmt = conn.prepareStatement(sql);

            // 2. 用setX()系列方法设置占位符实际的值
            pstmt.setString(1,"赵子龙");
            pstmt.setInt(2,24);

            // 3. 执行SQL 语句，不用传入参数
            int rtn = pstmt.executeUpdate();

            System.out.println("SQL语句影响了 " + rtn + " 行");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
