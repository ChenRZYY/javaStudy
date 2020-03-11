package cn.hp._07_kudu_impala;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ImpalaJavaKudu {

    private Connection connection = null;

    private PreparedStatement statement = null;
    @Before
    public void init() throws Exception{
        //1、加载驱动
        Class.forName("com.cloudera.impala.jdbc41.Driver");
        //2、创建Connection
        connection = DriverManager.getConnection("jdbc:impala://hadoop01:21050/myhive");

    }

    /**
     * 1、创建表
     */
    @Test
    public void createTable() throws Exception{

        String sql = "create table my_impala_kudu (" +
                "id int," +
                "name string," +
                "age int," +
                "primary key(id)) " +
                "partition by hash partitions 3 " +
                "stored as kudu " +
                "tblproperties('kudu.table_name'='my_impala_kudu','kudu.master_address'='hadoop01:7051,hadoop02:7051,hadoop03:7051')";
        //3、创建Statement对象
        statement = connection.prepareStatement(sql);

        //4、执行sql操作
        statement.execute();

    }

    /**
     * 2、插入数据
     */
    @Test
    public void insert() throws Exception{
        //1、创建Statement对象
        String sql = "insert into my_impala_kudu values(?,?,?)";
        statement = connection.prepareStatement(sql);
        //2、构建插入数据
        for(int i=0;i<=10;i++){
            statement.setInt(1,i);
            statement.setString(2,"zhangsan-"+i);
            statement.setInt(3,20+i);
            statement.addBatch();
        }

        //3、数据插入
        statement.executeBatch();
    }

    /**
     * 3、查询数据
     */
    @Test
    public void query() throws Exception{
        //1、创建statement
        String sql = "select * from my_impala_kudu where age>22 and age<=27";
        statement = connection.prepareStatement(sql);
        //2、查询得到结果，遍历
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()){
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int age = resultSet.getInt("age");
            System.out.println("id="+id+",name="+name+",age="+age);
        }
    }

    /**
     * 4、数据的更新
     */
    @Test
    public void update() throws Exception{
        //1、创建statement对象
        String sql = "update my_impala_kudu set name=? where id=?";
        statement = connection.prepareStatement(sql);
        //2、构建更新数据
        statement.setString(1,"lisi");
        statement.setInt(2,2);
        //3、执行
        statement.execute();
    }

    /**
     * 5、数据删除
     */
    @Test
    public void delete() throws Exception{
        //1、创建statement
        String sql = "delete from my_impala_kudu where id=?";
        statement = connection.prepareStatement(sql);
        //2、构建数据
        statement.setInt(1,2);
        //3、执行
        statement.execute();
    }

    @After
    public void close() throws Exception{
        if(statement!=null){
            statement.close();
        }
        if(connection!=null){
            connection.close();
        }
    }
}
