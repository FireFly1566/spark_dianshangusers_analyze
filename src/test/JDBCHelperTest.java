
/*
* JBDC 辅助组件测试类
*
*
* */

import jdbc.JDBCHelper;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCHelperTest {
    public static void main(String[] args) throws Exception{

        // 获取JDBCHelper 的单例
        JDBCHelper jdbcHelper = JDBCHelper.getInstance();

        // 测试普通的增删改查语句
        /*jdbcHelper.executeUpdate(
                "insert into test_user(name,age) values(?,?)",
                new Object[]{"王二",22});*/

        // 测试查询语句
        final Map<String,Object> testUser = new HashMap<>();


        // 设计一个内部接口 QueryCallback
        // 在执行查询语句的时候，可以封装和指定自己的查询结果的处理逻辑
        // 封装在一个内部接口的匿名内部类对象中，传入JDBCHelper 的方法
        // 在方法内部，可以回调定义的逻辑，处理查询结果
        // 并将查询结果，放入外部的变量中
      /*  jdbcHelper.executeQuery(
                "select name,age from test_user where id=?",
                new Object[]{9},

                new JDBCHelper.QueryCallback(){
                    @Override
                    public void process(ResultSet rs) throws Exception {
                        if(rs.next()){
                            String name = rs.getString(1);
                            int age = rs.getInt(2);

                            // 匿名内部类的重要知识点
                            // 如果要访问外部类中的一些成员，比如方法内的局部变量
                            // 必须将局部变量，声明为 final 类型，才可以访问
                            testUser.put("name",name);
                            testUser.put("age",age);

                        }
                    }
                }
        );

        System.out.println(testUser.get("name") + " " + testUser.get("age"));*/


      // 测试批量执行语句
        String sql = "insert into test_user(name,age) values(?,?)";
        List<Object[]> paramsList = new ArrayList<Object[]> ();
        paramsList.add(new Object[]{"赵云",30});
        paramsList.add(new Object[]{"李逵",34});

        jdbcHelper.executeBatch(sql,paramsList);


    }
}
